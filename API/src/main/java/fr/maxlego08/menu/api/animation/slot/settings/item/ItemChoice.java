package fr.maxlego08.menu.api.animation.slot.settings.item;

import fr.maxlego08.menu.api.MenuItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * One of the items a keyframe may display, and how likely it is compared to the others.
 *
 * @param item   the item
 * @param weight how likely it is; an item of weight 3 is picked three times as often as one of
 *               weight 1
 */
public record ItemChoice(@NotNull MenuItemStack item, int weight) {

    public ItemChoice(@NotNull MenuItemStack item) {
        this(item, 1);
    }

    public ItemChoice {
        weight = Math.max(1, weight);
    }

    /**
     * Writes the item and its weight into the section of the choice.
     *
     * @param section the section of the choice
     */
    public void serialize(@NotNull ConfigurationSection section) {
        this.item.serialize(section);
        if (this.weight != 1) section.set("weight", this.weight);
    }
}
