package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.button.Button;

public interface InventoryButton extends Button {

	/**
	 * Returns the inventory that will be used
	 * 
	 * @return inventory
	 */
	public String getInventory();
	
}
