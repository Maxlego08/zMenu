package fr.maxlego08.menu.button.buttons;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.HomeButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class ZHomeButton extends ZPlaceholderButton implements HomeButton {

	private final InventoryManager inventoryManager;
	private Inventory inventory;

	/**
	 * @param inventoryManager
	 * @param inventory
	 */
	public ZHomeButton(InventoryManager inventoryManager) {
		super();
		this.inventoryManager = inventoryManager;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		if (this.inventory == null) {
			return;
		}

		Inventory toInventory = this.inventory;
		this.inventoryManager.openInventory(player, toInventory, 1, new ArrayList<Inventory>());
	}

	@Override
	public void onInventoryOpen(Player player, InventoryDefault inventory) {

		List<Inventory> oldInventories = inventory.getOldInventories();
		if (oldInventories.size() >= 1) {
			this.inventory = oldInventories.get(0);
		}

	}

}
