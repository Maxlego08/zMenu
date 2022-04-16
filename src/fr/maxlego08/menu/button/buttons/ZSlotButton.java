package fr.maxlego08.menu.button.buttons;

import java.util.Collection;
import java.util.List;

import fr.maxlego08.menu.api.button.buttons.SlotButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;

public class ZSlotButton extends ZPlaceholderButton implements SlotButton {

	private final List<Integer> slots;

	/**
	 * @param slots
	 */
	public ZSlotButton(List<Integer> slots) {
		super();
		this.slots = slots;
	}

	@Override
	public Collection<Integer> getSlots() {
		return this.slots;
	}

	@Override
	public boolean isClickable() {
		return this.closeInventory();
	}

}
