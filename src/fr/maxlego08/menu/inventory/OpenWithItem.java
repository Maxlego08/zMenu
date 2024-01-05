package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.itemstack.FullSimilar;
import fr.maxlego08.menu.zcore.enums.ItemStackVerification;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Objects;


/**
 * <p>Represents the item that can be interacted with to open a menu.</p>
 */
public class OpenWithItem {
    private final MenuItemStack menuItemStack;
    private final boolean leftClick;
    private final boolean rightClick;
    private final EnumSet<ItemStackVerification> itemStackVerifications;

    public OpenWithItem(MenuItemStack menuItemStack, boolean leftClick, boolean rightClick) {
        this.menuItemStack = menuItemStack;
        this.leftClick = leftClick;
        this.rightClick = rightClick;

        itemStackVerifications = EnumSet.noneOf(ItemStackVerification.class);
        if (menuItemStack.getMaterial() != null) itemStackVerifications.add(ItemStackVerification.MATERIAL);
        if (menuItemStack.getDisplayName() != null) itemStackVerifications.add(ItemStackVerification.NAME);
        if (!menuItemStack.getLore().isEmpty()) itemStackVerifications.add(ItemStackVerification.LORE);
        if (!menuItemStack.getModelID().equals("0")) itemStackVerifications.add(ItemStackVerification.MODEL_ID);
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

        for (ItemStackVerification verification : itemStackVerifications) {
            if (!verification.getItemStackSimilar().isSimilar(itemStack, event.getItem())) {
                return false;
            }
        }

        return (isLeftClick() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                || isRightClick() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK));
    }
}