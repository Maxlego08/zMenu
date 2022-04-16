package fr.maxlego08.menu.button.buttons;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class ZNextButton extends ZPlaceholderButton implements HomeButton {

	private final InventoryManager inventoryManager;

	/**
	 * @param inventoryManager
	 * @param inventory
	 */
	public ZNextButton(InventoryManager inventoryManager) {
		super();
		this.inventoryManager = inventoryManager;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
		if (inventory.getPage() != inventory.getMaxPage()) {
			Inventory toInventory = inventory.getInventory();
			this.inventoryManager.openInventory(player, toInventory, inventory.getPage() + 1,
					new ArrayList<Inventory>());
		}
		super.onClick(player, event, inventory, slot);
	}

	@Override
	public boolean hasPermission() {
		return true;
	}

	@Override
	public boolean checkPermission(Player player, InventoryDefault inventory) {
		return inventory.getPage() < inventory.getMaxPage() && this.hasElseButton();
	}

}
