package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * <p>Represents the item that can be interacted with to open a menu.</p>
 */
/**
 * Represents an item and associated player actions that, when interacted with, trigger opening a specific menu.
 */
public class OpenWithItem {
    private final MenuItemStack menuItemStack;
    private final List<Action> actions;
    private final ItemStackSimilar itemStackSimilar;

    public OpenWithItem(@NotNull MenuItemStack menuItemStack,@NotNull List<Action> actions,@NotNull ItemStackSimilar itemStackSimilar) {
        this.menuItemStack = menuItemStack;
        this.actions = actions;
        this.itemStackSimilar = itemStackSimilar;
    }

    @NotNull
    public MenuItemStack getItemStack() {
        return menuItemStack;
    }

    @NotNull
    public List<Action> getActions() {
        return actions;
    }

    public boolean shouldTrigger(@NotNull PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return false;
        }

        if (!this.actions.contains(event.getAction())) {
            return false;
        }

        ItemStack itemStackA = this.menuItemStack.build(event.getPlayer());
        if (itemStackA == null) {
            return false;
        }
        return this.itemStackSimilar.isSimilar(itemStackA, item);
    }
}