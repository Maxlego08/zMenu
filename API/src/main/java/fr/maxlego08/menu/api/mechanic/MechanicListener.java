package fr.maxlego08.menu.api.mechanic;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class MechanicListener implements Listener {

    /*
    * Called when an item with the mechanic is given to a player.
    * Return true to cancel the item giving, false to allow it.
     */
    public boolean onItemGive(Player player, ItemStack item, String itemId) {
        return false;
    }
}
