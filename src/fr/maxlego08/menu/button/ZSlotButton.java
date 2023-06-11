package fr.maxlego08.menu.button;

import java.util.Collection;
import java.util.List;

import fr.maxlego08.menu.api.button.SlotButton;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public abstract class ZSlotButton extends ZUtils implements SlotButton {

	protected List<Integer> slots;

	@Override
	public Collection<Integer> getSlots() {
		return this.slots;
	}

	/**
	 * @param slots
	 *            the slots to set
	 */
	public void setSlots(List<Integer> slots) {
		this.slots = slots;
	}

}
