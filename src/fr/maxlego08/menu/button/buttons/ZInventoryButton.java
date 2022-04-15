package fr.maxlego08.menu.button.buttons;

import java.util.List;
import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.InventoryButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.enums.Message;

public class ZInventoryButton extends ZPlaceholderButton implements InventoryButton {

	private final InventoryManager inventoryManager;
	private final String inventoryName;

	/**
	 * @param inventoryManager
	 * @param inventoryName
	 */
	public ZInventoryButton(InventoryManager inventoryManager, String inventoryName) {
		super();
		this.inventoryManager = inventoryManager;
		this.inventoryName = inventoryName;
	}

	@Override
	public String getInventory() {
		return this.inventoryName;
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Button button) {
		
		Inventory fromInventory = inventory.getInventory();
		List<Inventory> oldInventories = inventory.getOldInventories();
		oldInventories.add(fromInventory);
				
		Optional<Inventory> optional = this.inventoryManager.getInventory(this.inventoryName);
		if (!optional.isPresent()){
			player.closeInventory();
			message(player, Message.INVENTORY_NOT_FOUND, "%name%", fromInventory.getFileName(), "%toName%", this.inventoryName);
			return;
		}
		
		Inventory toInventory = optional.get();
		this.inventoryManager.openInventory(player, toInventory, 1, oldInventories);
	}
	
}
