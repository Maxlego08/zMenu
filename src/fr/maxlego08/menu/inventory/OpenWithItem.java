package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.itemstack.FullSimilar;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


/**
 * <p>Represents the item that can be interacted with to open a menu.</p>
 */
public class OpenWithItem {
    private final MenuItemStack menuItemStack;
    private final boolean leftClick;
    private final boolean rightClick;

    public OpenWithItem(MenuItemStack menuItemStack, boolean leftClick, boolean rightClick) {
        this.menuItemStack = menuItemStack;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
    }

    public MenuItemStack getItemStack() {
        return menuItemStack;
    }

    public boolean isLeftClick() {
        return leftClick;
    }

    public boolean isRightClick() {
        return rightClick;
    }

    public boolean shouldTrigger(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return false;
        }
        ItemStack itemStack = this.menuItemStack.build(event.getPlayer());
        ItemStackSimilar similar = new FullSimilar();
        return similar.isSimilar(itemStack, event.getItem());
    }
}