package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;

public abstract class ZPlaceholderButton extends ZPermissibleButton implements PlaceholderButton {

	private final PlaceholderAction action;
	private final String placeholder;
	private final String value;

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
	 */
	public ZPlaceholderButton(String buttonName, ItemStack itemStack, int slot, boolean isPermanent, String permission,
			Button elseButton, PlaceholderAction action, String placeholder, String value) {
		super(buttonName, itemStack, slot, isPermanent, permission, elseButton);
		this.action = action;
		this.placeholder = placeholder;
		this.value = value;
	}

	@Override
	public String getPlaceHolder() {
		return this.placeholder;
	}

	@Override
	public PlaceholderAction getAction() {
		return this.getAction();
	}

	@Override
	public boolean hasPlaceHolder() {
		return this.placeholder != null && this.action != null;
	}

	@Override
	public boolean hasPermission() {
		return super.hasPermission() || this.hasPlaceHolder();
	}
	
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public boolean checkPermission(Player player) {
		if (!this.hasPlaceHolder()) {

			return super.checkPermission(player);

		} else {

			String valueAsString = papi(this.placeholder, player);

			if (this.action.equals(PlaceholderAction.BOOLEAN)) {

				try {
					return Boolean.valueOf(valueAsString);
				} catch (Exception exception) {
				}

			} else if (this.action.isString()) {
				
				switch (action) {
				case EQUALS_STRING:
					return valueAsString.equals(this.value);
				case EQUALSIGNORECASE_STRING:
					return valueAsString.equalsIgnoreCase(this.value);
				case CONTAINS_STRING:
					return valueAsString.contains(this.value);
				default:
					return super.checkPermission(player);
				}
				
			} else {
				
				try {

					double value = Double.valueOf(valueAsString);
					double currentValue = Double.valueOf(this.value);

					switch (action) {
					case LOWER:
						return value < currentValue;
					case LOWER_OR_EQUAL:
						return value <= currentValue;
					case SUPERIOR:
						return value > currentValue;
					case SUPERIOR_OR_EQUAL:
						return value >= currentValue;
					default:
						return super.checkPermission(player);
					}

				} catch (Exception exception) {
					return super.checkPermission(player);
				}
				
			}
			return super.checkPermission(player);
		}
	}

}
