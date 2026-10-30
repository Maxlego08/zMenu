package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.SectionSerializable;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
 * @param files          the files of the animations folder these settings were read from, when the
 *                       configuration only named them with {@code file}, {@code files} or
 *                       {@code extends}; empty when anything was written next to them, or when the
 *                       settings were written in place
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
        long cooldown,
        @NotNull List<String> files
) implements SectionSerializable {

    public SlotAnimationSettings(@NotNull SlotAnimationGroup defaults, @NotNull Map<Integer, SlotAnimationGroup> groups, boolean freeze, @NotNull AnimationPlayMode play, @NotNull List<TemporaryItem> temporaryItems, @Nullable Requirement requirement, float chance, @NotNull List<AnimationVariant> variants, @NotNull AnimationSeed seed, @NotNull List<Action> startActions, @NotNull List<Action> endActions, long cooldown) {
        this(defaults, groups, freeze, play, temporaryItems, requirement, chance, variants, seed, startActions, endActions, cooldown, List.of());
    }

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
        files = List.copyOf(files);
    }

    /**
     * Returns these settings, remembering that they were read from files of the animations folder,
     * so that they are written back as a reference rather than in full.
     *
     * @param files the names of the files
     * @return the same settings, with their files
     */
    @NotNull
    public SlotAnimationSettings withFiles(@NotNull List<String> files) {
        return new SlotAnimationSettings(this.defaults, this.groups, this.freeze, this.play, this.temporaryItems, this.requirement,
                this.chance, this.variants, this.seed, this.startActions, this.endActions, this.cooldown, files);
    }

    /**
     * Writes these settings the way an animation section is read back: a reference to their files
     * when they were only read from files, otherwise every value that differs from its default.
     *
     * @param section the animation section
     * @throws UnsupportedOperationException if a group uses an animation that cannot be serialized
     */
    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        if (!this.files.isEmpty()) {
            section.set("file", this.files.size() == 1 ? this.files.getFirst() : this.files);
            return;
        }

        this.defaults.serialize(section, null);

        if (!this.groups.isEmpty()) {
            ConfigurationSection groupsSection = section.createSection("groups");
            new TreeMap<>(this.groups).forEach((number, group) -> group.serialize(groupsSection.createSection(String.valueOf(number)), this.defaults));
        }

        if (!this.freeze) section.set("freeze", false);
        if (this.play != AnimationPlayMode.ALWAYS) section.set("play", this.play.name());

        if (!this.temporaryItems.isEmpty()) {
            ConfigurationSection itemsSection = section.createSection("temporary-items");
            this.temporaryItems.forEach(temporaryItem -> temporaryItem.serialize(itemsSection.createSection(temporaryItem.name())));
        }

        if (this.requirement != null) this.requirement.serialize(section.createSection("requirement"));
        if (this.chance != 100) section.set("chance", this.chance);

        if (!this.variants.isEmpty()) {
            ConfigurationSection variantsSection = section.createSection("variants");
            this.variants.forEach(variant -> variant.serialize(variantsSection.createSection(variant.name())));
        }

        if (!this.seed.equals(AnimationSeed.RANDOM)) section.set("seed", this.seed.serialize());
        if (!this.startActions.isEmpty()) section.set("start-actions", Permissible.serializeActions(this.startActions));
        if (!this.endActions.isEmpty()) section.set("end-actions", Permissible.serializeActions(this.endActions));
        if (this.cooldown > 0) section.set("cooldown", this.cooldown);
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
