package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;

public abstract class ZPlaceholderButton extends ZPermissibleButton implements PlaceholderButton {

	private PlaceholderAction action;
	private String placeholder;
	private String value;

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

	/**
	 * @param action
	 *            the action to set
	 */
	public void setAction(PlaceholderAction action) {
		this.action = action;
	}

	/**
	 * @param placeholder
	 *            the placeholder to set
	 */
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
