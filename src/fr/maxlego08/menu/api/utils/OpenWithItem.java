package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


/**
 * <p>Represents the item that can be interacted with to open a menu.</p>
 */
public class OpenWithItem {
    private final MenuItemStack menuItemStack;
    private final List<Action> actions;
    private final ItemStackSimilar itemStackSimilar;

    public OpenWithItem(MenuItemStack menuItemStack, List<Action> actions, ItemStackSimilar itemStackSimilar) {
        this.menuItemStack = menuItemStack;
        this.actions = actions;
        this.itemStackSimilar = itemStackSimilar;
    }

    public MenuItemStack getItemStack() {
        return menuItemStack;
    }

    public List<Action> getActions() {
        return actions;
    }

    public boolean shouldTrigger(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return false;
        }

        if (!this.actions.contains(event.getAction())) {
            return false;
        }

        ItemStack itemStack = this.menuItemStack.build(event.getPlayer());
        return this.itemStackSimilar.isSimilar(itemStack, event.getItem());
    }
}