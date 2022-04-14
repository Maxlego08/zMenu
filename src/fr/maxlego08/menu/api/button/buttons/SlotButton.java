package fr.maxlego08.menu.api.button.buttons;

import java.util.Collection;

import fr.maxlego08.menu.api.button.PlaceholderButton;

public interface SlotButton extends PlaceholderButton{

	/**
	 * Return the list of slots
	 * 
	 * @return slots
	 */
	public Collection<Integer> getSlots();
	
}
