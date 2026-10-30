package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.AnimationContext;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Turns the animation configuration of an inventory into a flat, sorted list of {@link AnimationStep}.
 *
 * <p>Compilation happens in two passes. The first one lays the groups out one after the other and
 * records where each of them starts and ends. The second one resolves the {@link AnimationAnchor} of
 * everything that is positioned relative to those boundaries. All of the timing is therefore decided
 * before anything is displayed, and the engine only has to apply the steps whose time has come.</p>
 *
 * <p>Steps carry an absolute time rather than a delay from the previous one, so the passes never
 * have to know about each other: whatever they produce is merged by sorting.</p>
 */
public final class AnimationCompiler {

    private AnimationCompiler() {
    }

    /**
     * Compiles the animation of an inventory.
     *
     * @param inventorySize the size of the inventory
     * @param settings      the animation settings of the inventory
     * @param filledSlots   the slots holding an item, in ascending order
     * @param slotSettings  the per slot configuration, slots missing from the map use
     *                      {@link ButtonAnimationSettings#DEFAULT}
     * @return the compiled animation, empty if nothing is animated
     */
    @NotNull
    public static CompiledAnimation compile(int inventorySize,
                                            @NotNull SlotAnimationSettings settings,
                                            @NotNull List<Integer> filledSlots,
                                            @NotNull Map<Integer, ButtonAnimationSettings> slotSettings) {
        return compile(inventorySize, settings, filledSlots, slotSettings, AnimationPlayback.REVEAL);
    }

    /**
     * Compiles the animation of an inventory for a given way of playing it.
     *
     * @param inventorySize the size of the inventory
     * @param settings      the animation settings of the inventory
     * @param filledSlots   the slots holding an item, in ascending order
     * @param slotSettings  the per slot configuration, slots missing from the map use
     *                      {@link ButtonAnimationSettings#DEFAULT}
     * @param playback      whether the buttons are revealed, or only drawn over
     * @return the compiled animation, empty if nothing is animated
     */
    @NotNull
    public static CompiledAnimation compile(int inventorySize,
                                            @NotNull SlotAnimationSettings settings,
                                            @NotNull List<Integer> filledSlots,
                                            @NotNull Map<Integer, ButtonAnimationSettings> slotSettings,
                                            @NotNull AnimationPlayback playback) {
        return compile(inventorySize, settings, filledSlots, slotSettings, playback, null);
    }

    /**
     * Compiles the animation of an inventory, telling the orderings who it is played for.
     *
     * @param inventorySize the size of the inventory
     * @param settings      the animation settings of the inventory
     * @param filledSlots   the slots holding an item, in ascending order
     * @param slotSettings  the per slot configuration, slots missing from the map use
     *                      {@link ButtonAnimationSettings#DEFAULT}
     * @param playback      whether the buttons are revealed, or only drawn over
     * @param context       the player and the menu the animation is played for, null outside of one
     * @return the compiled animation, empty if nothing is animated
     */
    @NotNull
    public static CompiledAnimation compile(int inventorySize,
                                            @NotNull SlotAnimationSettings settings,
                                            @NotNull List<Integer> filledSlots,
                                            @NotNull Map<Integer, ButtonAnimationSettings> slotSettings,
                                            @NotNull AnimationPlayback playback,
                                            @Nullable AnimationContext context) {

        AnimationContext animationContext = context == null
                ? new AnimationContext(inventorySize, filledSlots, null, null)
                : context;

        List<AnimationStep> steps = new ArrayList<>();
        Map<Integer, Long> revealTimes = new LinkedHashMap<>();
        AnimationTiming timing = revealButtons(inventorySize, settings, filledSlots, slotSettings, steps, revealTimes, animationContext, playback);

        if (playback == AnimationPlayback.OVERLAY) steps.clear();

        if (playback == AnimationPlayback.CONCEAL) {
            steps.replaceAll(step -> step.action() == StepAction.REVEAL
                    ? new AnimationStep(step.time(), step.slot(), StepAction.CLEAR, null, step.sound(), step.useCache())
                    : step);
            revealTimes.clear();
        }

        applyTemporaryItems(inventorySize, settings, timing, steps, revealTimes, slotSettings, playback, animationContext);

        if (steps.isEmpty()) return CompiledAnimation.EMPTY;

        steps.sort(Comparator.comparingLong(AnimationStep::time).thenComparingInt(AnimationStep::slot));
        return new CompiledAnimation(dedupeSounds(steps), timing);
    }

    /**
     * First pass: lays the groups out and emits one {@link StepAction#REVEAL} per animated slot.
     *
     * @return where every group starts and ends
     */
    @NotNull
    private static AnimationTiming revealButtons(int inventorySize,
                                                 @NotNull SlotAnimationSettings settings,
                                                 @NotNull List<Integer> filledSlots,
                                                 @NotNull Map<Integer, ButtonAnimationSettings> slotSettings,
                                                 @NotNull List<AnimationStep> steps,
                                                 @NotNull Map<Integer, Long> revealTimes,
                                                 @NotNull AnimationContext context,
                                                 @NotNull AnimationPlayback playback) {

        boolean honourOptOut = playback != AnimationPlayback.CONCEAL;

        Map<Integer, List<Integer>> slotsByGroup = new TreeMap<>();
        for (int slot : filledSlots) {
            ButtonAnimationSettings slotSetting = slotSettings.getOrDefault(slot, ButtonAnimationSettings.DEFAULT);
            if (honourOptOut && !slotSetting.enabled()) continue;
            slotsByGroup.computeIfAbsent(slotSetting.group(), key -> new ArrayList<>()).add(slot);
        }

        Map<Integer, GroupTiming> groups = new LinkedHashMap<>();
        long groupStart = 0;

        for (Map.Entry<Integer, List<Integer>> entry : slotsByGroup.entrySet()) {
            SlotAnimationGroup group = settings.group(entry.getKey());
            List<Integer> ordered = new ArrayList<>(group.animation().order(context.withSlots(entry.getValue())));
            if (group.reverse()) Collections.reverse(ordered);

            long start = groupStart + group.delay();
            long groupEnd = start;

            for (int index = 0; index < ordered.size(); index++) {
                int slot = ordered.get(index);
                long buttonDelay = slotSettings.getOrDefault(slot, ButtonAnimationSettings.DEFAULT).delay();
                long time = start + ((long) (index / group.slotsPerStep()) * group.interval()) + buttonDelay;
                if (group.jitter() > 0) {
                    time = Math.max(0, time + context.random().nextLong(-group.jitter(), group.jitter() + 1));
                }

                ButtonAnimationSettings slotSetting = slotSettings.getOrDefault(slot, ButtonAnimationSettings.DEFAULT);
                SoundOption sound = slotSetting.sound() != null ? slotSetting.sound() : group.sound();

                steps.add(new AnimationStep(time, slot, StepAction.REVEAL, null, sound));
                revealTimes.put(slot, time);
                groupEnd = Math.max(groupEnd, time + group.interval());
            }

            groups.put(entry.getKey(), new GroupTiming(start, groupEnd));

            groupStart = groupEnd;
        }

        return new AnimationTiming(groups, groupStart);
    }

    /**
     * Second pass: places the items that only exist while the animation plays.
     *
     * <p>Every temporary item is a list of keyframes. A keyframe holding an item displays it from
     * then on, a keyframe without one removes it. An item still shown when the last group is over is
     * removed there, so an animation can never leak an item into the finished menu.</p>
     *
     * <p>Items are resolved per slot rather than one after the other, because several of them may
     * cover the same slot: at any moment the one declared last wins, and a step is only emitted when
     * what the slot displays actually changes.</p>
     *
     * <p>A temporary item wins over the button owning its slot for as long as it is shown. When the
     * last of them goes away the slot is not blanked but re-resolved: it goes back to the real item
     * if the button would have been revealed by then, and is emptied otherwise. Because the reveal
     * times are already known, that decision is taken here rather than at playback.</p>
     */
    private static void applyTemporaryItems(int inventorySize,
                                            @NotNull SlotAnimationSettings settings,
                                            @NotNull AnimationTiming timing,
                                            @NotNull List<AnimationStep> steps,
                                            @NotNull Map<Integer, Long> revealTimes,
                                            @NotNull Map<Integer, ButtonAnimationSettings> slotSettings,
                                            @NotNull AnimationPlayback playback,
                                            @NotNull AnimationContext context) {

        if (settings.temporaryItems().isEmpty()) return;

        Map<Integer, List<SlotKeyframe>> keyframesBySlot = new TreeMap<>();

        for (int index = 0; index < settings.temporaryItems().size(); index++) {
            TemporaryItem temporaryItem = settings.temporaryItems().get(index);
            if (temporaryItem.frames().isEmpty()) continue;

            boolean perSlot = temporaryItem.chancePerSlot();
            if (!perSlot && !context.roll(temporaryItem.chance())) continue;

            List<ResolvedKeyframe> shared = perSlot ? null : resolveKeyframes(temporaryItem, timing, context);
            if (shared != null && shared.isEmpty()) continue;

            List<Integer> eligible = new ArrayList<>();
            for (int slot : temporaryItem.slots()) {
                if (slot < 0 || slot >= inventorySize) continue;
                if (!slotSettings.getOrDefault(slot, ButtonAnimationSettings.DEFAULT).allowTemporaryItems()) continue;
                eligible.add(slot);
            }

            for (int slot : context.pickSome(eligible, temporaryItem.count())) {
                if (perSlot && !context.roll(temporaryItem.chance())) continue;

                List<ResolvedKeyframe> frames = shared != null ? shared : resolveKeyframes(temporaryItem, timing, context);
                if (frames.isEmpty()) continue;

                List<SlotKeyframe> slotKeyframes = keyframesBySlot.computeIfAbsent(slot, key -> new ArrayList<>());
                for (ResolvedKeyframe frame : frames) {
                    slotKeyframes.add(new SlotKeyframe(frame.time(), index, frame.item(), frame.sound(), temporaryItem.useCache()));
                }
            }
        }
        if (keyframesBySlot.isEmpty()) return;

        dropRepeatedSounds(keyframesBySlot);

        List<AnimationStep> temporarySteps = new ArrayList<>();
        Set<Integer> hiddenReveals = new HashSet<>();

        keyframesBySlot.forEach((slot, keyframes) -> {
            keyframes.sort(Comparator.comparingLong(SlotKeyframe::time));

            TreeMap<Integer, MenuItemStack> shown = new TreeMap<>();
            Map<Integer, Boolean> shownUseCache = new TreeMap<>();
            MenuItemStack displayed = null;
            long coverStart = 0;
            Long reveal = revealTimes.get(slot);

            for (int index = 0; index < keyframes.size(); index++) {
                SlotKeyframe keyframe = keyframes.get(index);

                if (keyframe.item() == null) {
                    shown.remove(keyframe.owner());
                    shownUseCache.remove(keyframe.owner());
                } else {
                    shown.put(keyframe.owner(), keyframe.item());
                    shownUseCache.put(keyframe.owner(), keyframe.useCache());
                }

                if (index + 1 < keyframes.size() && keyframes.get(index + 1).time() == keyframe.time()) continue;

                Integer topOwner = shown.isEmpty() ? null : shown.lastKey();
                MenuItemStack top = topOwner == null ? null : shown.get(topOwner);
                if (top == displayed) continue;

                if (top != null) {
                    if (displayed == null) coverStart = keyframe.time();
                    boolean useCache = shownUseCache.getOrDefault(topOwner, true);
                    temporarySteps.add(new AnimationStep(keyframe.time(), slot, StepAction.SET, top, keyframe.sound(), useCache));
                } else {
                    if (reveal != null && reveal >= coverStart && reveal <= keyframe.time()) hiddenReveals.add(slot);
                    StepAction action = playback == AnimationPlayback.OVERLAY || (reveal != null && reveal <= keyframe.time())
                            ? StepAction.REVEAL
                            : StepAction.CLEAR;
                    temporarySteps.add(new AnimationStep(keyframe.time(), slot, action, null, keyframe.sound()));
                }
                displayed = top;
            }
        });

        if (!hiddenReveals.isEmpty()) {
            steps.removeIf(step -> step.action() == StepAction.REVEAL && hiddenReveals.contains(step.slot()));
        }
        steps.addAll(temporarySteps);
    }

    /**
     * Keeps a keyframe sound on the first slot it lands on only, so an item covering nine slots does
     * not play the same sound nine times.
     */
    private static void dropRepeatedSounds(@NotNull Map<Integer, List<SlotKeyframe>> keyframesBySlot) {
        Set<String> seen = new HashSet<>();

        keyframesBySlot.values().forEach(keyframes -> keyframes.replaceAll(keyframe -> {
            if (keyframe.sound() == null) return keyframe;
            return seen.add(keyframe.owner() + "@" + keyframe.time())
                    ? keyframe
                    : new SlotKeyframe(keyframe.time(), keyframe.owner(), keyframe.item(), null, keyframe.useCache());
        }));
    }

    /**
     * A keyframe of one temporary item, applied to one slot. {@code owner} is the position of the
     * item in the configuration, which decides who wins when two of them cover the same slot.
     */
    private record SlotKeyframe(long time, int owner, MenuItemStack item, SoundOption sound, boolean useCache) {
    }

    /**
     * Resolves the keyframes of a temporary item to times, in the order they happen, and appends the
     * removal of an item that is never removed explicitly.
     */
    @NotNull
    private static List<ResolvedKeyframe> resolveKeyframes(@NotNull TemporaryItem temporaryItem, @NotNull AnimationTiming timing,
                                                           @NotNull AnimationContext context) {
        List<ResolvedKeyframe> frames = new ArrayList<>(temporaryItem.frames().size() + 1);
        for (AnimationKeyframe keyframe : temporaryItem.frames()) {
            if (!keyframe.isRemoval() && !context.roll(keyframe.chance())) continue;

            MenuItemStack item = keyframe.choices().isEmpty() ? keyframe.item() : context.pick(keyframe.choices());
            frames.add(new ResolvedKeyframe(timing.resolve(keyframe.at()), item, keyframe.sound()));
        }
        if (frames.isEmpty()) return frames;

        frames.sort(Comparator.comparingLong(ResolvedKeyframe::time));

        ResolvedKeyframe last = frames.getLast();
        if (last.item() != null) {
            frames.add(new ResolvedKeyframe(Math.max(timing.end(), last.time()), null, null));
        }
        return frames;
    }

    private record ResolvedKeyframe(long time, MenuItemStack item, SoundOption sound) {
    }

    /**
     * Keeps at most one sound per instant.
     *
     * <p>A step revealing nine slots at once, or a keyframe landing on the same instant as a reveal,
     * would otherwise play the same sound several times in the same tick, which is heard as noise
     * rather than as an effect.</p>
     */
    @NotNull
    private static List<AnimationStep> dedupeSounds(@NotNull List<AnimationStep> steps) {
        List<AnimationStep> deduped = new ArrayList<>(steps.size());

        long currentTime = Long.MIN_VALUE;
        boolean playedAtCurrentTime = false;

        for (AnimationStep step : steps) {
            if (step.time() != currentTime) {
                currentTime = step.time();
                playedAtCurrentTime = false;
            }

            if (step.sound() == null) {
                deduped.add(step);
            } else if (playedAtCurrentTime) {
                deduped.add(new AnimationStep(step.time(), step.slot(), step.action(), step.item(), null, step.useCache()));
            } else {
                deduped.add(step);
                playedAtCurrentTime = true;
            }
        }
        return deduped;
    }
}