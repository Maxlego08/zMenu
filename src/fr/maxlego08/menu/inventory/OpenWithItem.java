package fr.maxlego08.menu.inventory;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


/**
 * <p>The OpenWithItem class represents the item that can be interacted with to open a menu.</p>
 */
public class OpenWithItem {
	private final ItemStack itemStack;
	private final boolean leftClick;
	private final boolean rightClick;

	public OpenWithItem(ItemStack itemStack, boolean leftClick, boolean rightClick) {
		this.itemStack = itemStack;
		this.leftClick = leftClick;
		this.rightClick = rightClick;
	}

	public ItemStack getItemStack() {
		return itemStack;
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
		boolean isSimilarItemStack = getItemStack().getType() == event.getItem().getType();
		return isSimilarItemStack
				&& (isLeftClick() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
				|| isRightClick() && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK));
	}
}