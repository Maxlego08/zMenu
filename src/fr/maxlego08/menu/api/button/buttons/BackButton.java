package fr.maxlego08.menu.api.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.PlaceholderButton;

public interface BackButton extends PlaceholderButton {

	/**
	 * Allows you to set the inventory that the button will target
	 * 
	 * @param inventory
	 */
	public void setBackInventory(Inventory inventory);
	
}
