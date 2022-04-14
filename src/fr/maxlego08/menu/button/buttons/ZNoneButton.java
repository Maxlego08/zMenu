package fr.maxlego08.menu.button.buttons;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.button.ZPlaceholderButton;

public class ZNoneButton extends ZPlaceholderButton {

	public ZNoneButton(String buttonName, ItemStack itemStack, int slot, boolean isPermanent, String permission,
			Button elseButton, PlaceholderAction action, String placeholder, String value) {
		super(buttonName, itemStack, slot, isPermanent, permission, elseButton, action, placeholder, value);
	}

	@Override
	public void onRightClick(Player player, InventoryClickEvent event, Inventory inventory) {
	}

	@Override
	public void onLeftClick(Player player, InventoryClickEvent event, Inventory inventory) {
	}

	@Override
	public void onMiddleClick(Player player, InventoryClickEvent event, Inventory inventory) {
	}

	@Override
	public boolean isClickable() {
		return false;
	}

}
