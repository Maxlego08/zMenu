package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.button.ZPlaceholderButton;

public class ZNoneButton extends ZPlaceholderButton {

	@Override
	public boolean isClickable() {
		return this.closeInventory();
	}

}
