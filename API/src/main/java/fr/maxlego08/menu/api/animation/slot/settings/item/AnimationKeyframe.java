package fr.maxlego08.menu.api.animation.slot.settings.item;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One moment in the life of a {@link TemporaryItem}: what it looks like from then on, or the moment
 * it goes away.
 *
 * @param at    when the keyframe happens
 * @param item  the item to display from then on, or null to remove the temporary item
 * @param sound  the sound to play, or null for a silent keyframe
 * @param chance  the chance of the keyframe happening, in percent. A keyframe that removes the item
 *                ignores it: an item that might not be removed would be left behind
 * @param choices the items to pick from, one of which is displayed; when it is not empty it is used
 *                instead of {@code item}
 */
public record AnimationKeyframe(@NotNull AnimationAnchor at, @Nullable MenuItemStack item, @Nullable SoundOption sound, float chance, @NotNull List<ItemChoice> choices) {

    public AnimationKeyframe(@NotNull AnimationAnchor at, @Nullable MenuItemStack item, @Nullable SoundOption sound, float chance) {
        this(at, item, sound, chance, List.of());
    }

    public AnimationKeyframe(@NotNull AnimationAnchor at, @Nullable MenuItemStack item, @Nullable SoundOption sound) {
        this(at, item, sound, 100, List.of());
    }

    public AnimationKeyframe {
        choices = List.copyOf(choices);
    }

    /**
     * Returns whether this keyframe removes the temporary item instead of changing it.
     *
     * @return true if the item goes away
     */
    public boolean isRemoval() {
        return this.item == null && this.choices.isEmpty();
    }

    /**
     * Writes this keyframe the way a {@code frames} entry is read back.
     *
     * @param section the section of the keyframe
     */
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("at", this.at.serialize());

        if (this.isRemoval()) {
            section.set("remove", true);
        } else {
            if (this.item != null) this.item.serialize(section.createSection("item"));
            if (!this.choices.isEmpty()) {
                ConfigurationSection choicesSection = section.createSection("choices");
                for (int index = 0; index < this.choices.size(); index++) {
                    this.choices.get(index).serialize(choicesSection.createSection(String.valueOf(index + 1)));
                }
            }
            if (this.chance != 100) section.set("chance", this.chance);
        }

        if (this.sound != null) this.sound.serialize(section.createSection("sound"));
    }
}
