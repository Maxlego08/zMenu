package fr.maxlego08.menu.api.animation.slot.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * One of the animations a menu may play, and how likely it is compared to the others.
 *
 * <p>A variant is picked when the animation is about to play, so a menu can spiral one time and
 * sweep the next.</p>
 *
 * @param name     the name of the variant in the configuration, used to report errors
 * @param settings what that variant does
 * @param weight   how likely it is; a variant of weight 3 is picked three times as often as one of
 *                 weight 1
 */
public record AnimationVariant(@NotNull String name, @NotNull SlotAnimationSettings settings, int weight) {

    public AnimationVariant {
        weight = Math.max(1, weight);
    }

    /**
     * Writes this variant into its section of {@code variants}: its settings, and its weight.
     *
     * @param section the section named after the variant
     */
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section);
        if (this.weight != 1) section.set("weight", this.weight);
    }
}
