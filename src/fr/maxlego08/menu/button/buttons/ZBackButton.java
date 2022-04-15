package fr.maxlego08.menu.button.buttons;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.BackButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public class ZBackButton extends ZPlaceholderButton implements BackButton {

	private final InventoryManager inventoryManager;
	private Inventory inventory;

	/**
	 * @param inventoryManager
	 * @param inventory
	 */
	public ZBackButton(InventoryManager inventoryManager) {
		super();
		this.inventoryManager = inventoryManager;
	}


	@Override
	public void setBackInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Button button) {

		Inventory fromInventory = inventory.getInventory();
		List<Inventory> oldInventories = inventory.getOldInventories();
		System.out.println("A > " + oldInventories);
		oldInventories.remove(fromInventory);
		System.out.println("B > " + oldInventories);

		Inventory toInventory = this.inventory;
		this.inventoryManager.openInventory(player, toInventory, 1, oldInventories);
	}

}
