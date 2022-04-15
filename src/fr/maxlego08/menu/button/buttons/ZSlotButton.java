package fr.maxlego08.menu.button.buttons;

import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.SlotButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.button.ZPlaceholderButton;

public class ZSlotButton extends ZPlaceholderButton implements SlotButton {

	private final List<Integer> slots;

	/**
	 * @param buttonName
	 * @param itemStack
	 * @param slot
	 * @param isPermanent
	 * @param permission
	 * @param elseButton
	 * @param action
	 * @param placeholder
	 * @param value
	 * @param slots
	 */
	public ZSlotButton(String buttonName, ItemStack itemStack, int slot, boolean isPermanent, String permission,
			Button elseButton, PlaceholderAction action, String placeholder, String value, List<Integer> slots) {
		super(buttonName, itemStack, slot, isPermanent, permission, elseButton, action, placeholder, value);
		this.slots = slots;
	}

	@Override
	public Collection<Integer> getSlots() {
		return this.slots;
	}

	@Override
	public boolean isClickable() {
		return false;
	}

}
