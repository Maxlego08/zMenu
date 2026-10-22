package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationSeed;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An animation written in code rather than in a file.
 *
 * <p>Extend it, override only what you care about, and register the result:</p>
 *
 * <pre>
 * public class GlowAnimation extends CustomAnimation {
 *
 *     public GlowAnimation() {
 *         super("glow");
 *     }
 *
 *     &#64;Override
 *     public SlotAnimation getAnimation() {
 *         return new SpiralSlotAnimation();
 *     }
 *
 *     &#64;Override
 *     public long getInterval() {
 *         return 30;
 *     }
 *
 *     &#64;Override
 *     public List&lt;TemporaryItem&gt; getTemporaryItems() {
 *         return List.of(temporaryItem("flash", slots(10, 16), item, AnimationAnchor.START, AnimationAnchor.at(400)));
 *     }
 * }
 *
 * inventoryManager.getAnimationManager().registerAnimation(new GlowAnimation());
 * </pre>
 *
 * <p>Every hook has a default, so an animation may be as small as an ordering. Nothing here is
 * required beyond the name.</p>
 */
public abstract class CustomAnimation {

    private final String name;

    /**
     * @param name the name the animation is registered under, and used by {@code file} in a menu
     */
    protected CustomAnimation(@NotNull String name) {
        this.name = name;
    }

    /**
     * Gets the name of this animation.
     *
     * @return the name
     */
    @Contract(pure = true)
    @NotNull
    public final String getName() {
        return this.name;
    }

    /**
     * The order the slots are revealed in.
     *
     * @return the ordering, sequential by default
     */
    @NotNull
    public SlotAnimation getAnimation() {
        return new SequentialSlotAnimation();
    }

    /**
     * Whether the order is played backwards.
     *
     * @return true to reverse it, false by default
     */
    public boolean isReverse() {
        return false;
    }

    /**
     * The delay between two steps, in milliseconds.
     *
     * @return the interval, 50 by default
     */
    public long getInterval() {
        return 50;
    }

    /**
     * The number of slots revealed on each step.
     *
     * @return the number of slots, 1 by default
     */
    public int getSlotsPerStep() {
        return 1;
    }

    /**
     * The delay before the first group starts, in milliseconds.
     *
     * @return the delay, none by default
     */
    public long getDelay() {
        return 0;
    }

    /**
     * The sound played when a slot is revealed.
     *
     * @return the sound, none by default
     */
    @Nullable
    public SoundOption getSound() {
        return null;
    }

    /**
     * Whether the inventory ignores clicks while the animation plays.
     *
     * @return true to freeze it, which is the default
     */
    public boolean isFreeze() {
        return true;
    }

    /**
     * When the animation is allowed to play.
     *
     * @return the mode, every open by default
     */
    @NotNull
    public AnimationPlayMode getPlayMode() {
        return AnimationPlayMode.ALWAYS;
    }

    /**
     * The groups that differ from the defaults above, keyed by group number.
     *
     * <p>Buttons pick their group with {@code animation.group}; a group missing from this map uses
     * everything this class returns.</p>
     *
     * @return the groups, none by default
     */
    @NotNull
    public Map<Integer, SlotAnimationGroup> getGroups() {
        return Map.of();
    }

    /**
     * Where the randomness of this animation comes from.
     *
     * @return the seed, a fresh roll on every play by default
     */
    @NotNull
    public AnimationSeed getSeed() {
        return AnimationSeed.RANDOM;
    }

    /**
     * What runs when this animation starts.
     *
     * @return the actions, none by default
     */
    @NotNull
    public List<Action> getStartActions() {
        return List.of();
    }

    /**
     * What runs when this animation is over, however it ended.
     *
     * @return the actions, none by default
     */
    @NotNull
    public List<Action> getEndActions() {
        return List.of();
    }

    /**
     * What the player must satisfy for this animation to play.
     *
     * @return the requirement, none by default
     */
    @Nullable
    public Requirement getRequirement() {
        return null;
    }

    /**
     * The items that exist only while the animation plays.
     *
     * @return the temporary items, none by default
     */
    @NotNull
    public List<TemporaryItem> getTemporaryItems() {
        return List.of();
    }

    /**
     * Builds the settings this class describes.
     *
     * @return the settings
     */
    @Contract(pure = true)
    @NotNull
    public final SlotAnimationSettings toSettings() {
        SlotAnimationGroup defaults = new SlotAnimationGroup(this.getAnimation(), this.getInterval(), this.getSlotsPerStep(), this.getDelay(), this.getSound(), this.isReverse());

        return new SlotAnimationSettings(defaults, this.getGroups(), this.isFreeze(), this.getPlayMode(),
                this.getTemporaryItems(), this.getRequirement(), 100, List.of(), this.getSeed(),
                this.getStartActions(), this.getEndActions());
    }

    /**
     * Builds the animation this class describes, ready to be registered or played.
     *
     * @return the animation
     */
    @Contract(pure = true)
    @NotNull
    public final Animation toAnimation() {
        return new Animation(this.name, this.toSettings());
    }

    /**
     * A group overriding some of the defaults of this class.
     *
     * @param animation    the ordering, null to keep the one of this class
     * @param interval     the delay between two steps, in milliseconds
     * @param slotsPerStep the number of slots revealed on each step
     * @param delay        the delay before the group starts
     * @return the group
     */
    @Contract(pure = true)
    @NotNull
    protected final SlotAnimationGroup group(@Nullable SlotAnimation animation, long interval, int slotsPerStep, long delay) {
        return new SlotAnimationGroup(animation == null ? this.getAnimation() : animation, interval, slotsPerStep, delay, this.getSound(), this.isReverse());
    }

    /**
     * A map of groups, in the order they are given.
     *
     * @param first  the number of the first group
     * @param groups the groups, starting at {@code first}
     * @return the groups, keyed by number
     */
    @Contract(pure = true)
    @NotNull
    protected final Map<Integer, SlotAnimationGroup> groups(int first, @NotNull SlotAnimationGroup... groups) {
        Map<Integer, SlotAnimationGroup> map = new LinkedHashMap<>();
        for (int index = 0; index < groups.length; index++) {
            map.put(first + index, groups[index]);
        }
        return map;
    }

    /**
     * A temporary item appearing once and going away once.
     *
     * @param name  the name of the item, used to report errors
     * @param slots the slots it occupies
     * @param item  the item to display
     * @param show  when it appears
     * @param hide  when it is removed
     * @return the temporary item
     */
    @Contract(pure = true)
    @NotNull
    protected final TemporaryItem temporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull MenuItemStack item, @NotNull AnimationAnchor show, @NotNull AnimationAnchor hide) {
        return new TemporaryItem(name, slots, List.of(
                new AnimationKeyframe(show, item, null),
                new AnimationKeyframe(hide, null, null)));
    }

    /**
     * A temporary item described by its keyframes.
     *
     * @param name   the name of the item, used to report errors
     * @param slots  the slots it occupies
     * @param frames what it does over time
     * @return the temporary item
     */
    @Contract(pure = true)
    @NotNull
    protected final TemporaryItem temporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull AnimationKeyframe... frames) {
        return new TemporaryItem(name, slots, List.of(frames));
    }

    /**
     * A keyframe displaying an item from that moment on.
     *
     * @param at    when it happens
     * @param item  the item to display
     * @param sound the sound to play, or null
     * @return the keyframe
     */
    @Contract(pure = true)
    @NotNull
    protected final AnimationKeyframe frame(@NotNull AnimationAnchor at, @NotNull MenuItemStack item, @Nullable SoundOption sound) {
        return new AnimationKeyframe(at, item, sound);
    }

    /**
     * A keyframe removing the temporary item.
     *
     * @param at    when it happens
     * @param sound the sound to play, or null
     * @return the keyframe
     */
    @Contract(pure = true)
    @NotNull
    protected final AnimationKeyframe removal(@NotNull AnimationAnchor at, @Nullable SoundOption sound) {
        return new AnimationKeyframe(at, null, sound);
    }

    /**
     * The slots from one to another, both included.
     *
     * @param from the first slot
     * @param to   the last slot
     * @return the slots
     */
    @Contract(pure = true)
    @NotNull
    protected final List<Integer> slots(int from, int to) {
        List<Integer> slots = new ArrayList<>(Math.max(0, to - from + 1));
        for (int slot = Math.min(from, to); slot <= Math.max(from, to); slot++) {
            slots.add(slot);
        }
        return slots;
    }
}
