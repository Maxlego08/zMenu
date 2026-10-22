package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Settings of the animation played when an inventory opens.
 *
 * <p>Every animated slot belongs to a group, group 1 by default. Groups are played one after the
 * other, in ascending order, and each of them may override how it is revealed. A group without an
 * explicit configuration uses {@code defaults}.</p>
 *
 * @param defaults the configuration used by the groups that do not declare one
 * @param groups   the per group overrides, keyed by group number
 * @param freeze         whether the inventory ignores clicks while the animation plays
 * @param play           when the animation is allowed to play
 * @param temporaryItems the items that exist only while the animation plays
 * @param requirement    what the player must satisfy for the animation to play, null for no
 *                       condition
 * @param chance         the chance of the animation playing at all, in percent
 * @param variants       the alternatives to pick one from when the animation plays; when it is not
 *                       empty the picked one describes what happens, and only the chance and the
 *                       requirement of this one still apply
 * @param seed           where the randomness of the animation comes from
 * @param startActions   what runs when the animation starts, after it is known that it will play
 * @param endActions     what runs when it is over, however it ended
 * @param cooldown       the minimum time between two plays for one player, in milliseconds; 0 for
 *                       none. Remembered in the database, so it survives a reconnect
 */
public record SlotAnimationSettings(
        @NotNull SlotAnimationGroup defaults,
        @NotNull Map<Integer, SlotAnimationGroup> groups,
        boolean freeze,
        @NotNull AnimationPlayMode play,
        @NotNull List<TemporaryItem> temporaryItems,
        @Nullable Requirement requirement,
        float chance,
        @NotNull List<AnimationVariant> variants,
        @NotNull AnimationSeed seed,
        @NotNull List<Action> startActions,
        @NotNull List<Action> endActions,
        long cooldown
) {

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement, float chance, @NotNull List<AnimationVariant> variants, @NotNull AnimationSeed seed, @NotNull List<Action> startActions, @NotNull List<Action> endActions) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, chance, variants, seed, startActions, endActions, 0);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement, float chance, @NotNull List<AnimationVariant> variants, @NotNull AnimationSeed seed) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, chance, variants, seed, List.of(), List.of(), 0);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement, float chance, @NotNull List<AnimationVariant> variants) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, chance, variants, AnimationSeed.RANDOM, List.of(), List.of());
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement, float chance) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, chance, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, 100, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups,
                                 boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems) {
        this(defaults, groups, freeze, play, temporaryItems, null, 100, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play) {
        this(defaults, groups, freeze, play, List.of(), null, 100, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze) {
        this(defaults, groups, freeze, AnimationPlayMode.ALWAYS, List.of(), null, 100, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, boolean freeze) {
        this(defaults, Map.of(), freeze, AnimationPlayMode.ALWAYS, List.of(), null, 100, List.of(), AnimationSeed.RANDOM);
    }

    public SlotAnimationSettings {
        groups = Map.copyOf(groups);
        temporaryItems = List.copyOf(temporaryItems);
        variants = List.copyOf(variants);
        startActions = List.copyOf(startActions);
        endActions = List.copyOf(endActions);
        cooldown = Math.max(0, cooldown);
    }

    /**
     * Gets the configuration of a group.
     *
     * @param group the group number
     * @return the configuration of that group, or {@link #defaults()} if it declares none
     */
    @NotNull
    public SlotAnimationGroup group(int group) {
        return this.groups.getOrDefault(group, this.defaults);
    }

    /**
     * Whether the player having seen this animation has to be remembered beyond the session.
     *
     * @return true when the mode or the cooldown needs the database
     */
    public boolean needsPersistence() {
        return this.play.isPersistent() || this.cooldown > 0;
    }
}
