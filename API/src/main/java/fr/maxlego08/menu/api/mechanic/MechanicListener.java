package fr.maxlego08.menu.api.mechanic;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class MechanicListener implements Listener {

    /**
    * Called when an item with the mechanic is given to a player.
    * Return true to cancel the item giving, false to allow it.
     */
    public boolean onItemGive(@NotNull Player player, @NotNull ItemStack item,@NotNull String itemId) {
        return false;
    }
}
