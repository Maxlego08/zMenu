package fr.maxlego08.menu.api.dupe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DupeItem {

    private final ItemStack itemStack;
    private final Player player;

    public DupeItem(ItemStack itemStack, Player player) {
        this.itemStack = itemStack;
        this.player = player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Player getPlayer() {
        return player;
    }
}
