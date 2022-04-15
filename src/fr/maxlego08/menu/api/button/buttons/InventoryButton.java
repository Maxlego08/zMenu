package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.PlaceholderButton;

public interface InventoryButton extends PlaceholderButton {

	/**
	 * Returns the inventory that will be used
	 * 
	 * @return inventory
	 */
	public String getInventory();
	
}
