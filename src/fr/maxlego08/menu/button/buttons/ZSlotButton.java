package fr.maxlego08.menu.button.buttons;

import java.util.Collection;
import java.util.List;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.button.buttons.SlotButton;
import fr.maxlego08.menu.button.ZPlaceholderButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

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
	public boolean hasSpecialRender() {
		return true;
	}

	@Override
	public void onRender(Player player, InventoryDefault inventory) {
		this.slots.forEach(slot -> inventory.displayFinalButton(this, slot));
	}

}
