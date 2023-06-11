package fr.maxlego08.menu.action.permissible;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.action.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public class ZPlaceholderPermissible extends ZUtils implements PlaceholderPermissible {

	private final PlaceholderAction action;
	private final String placeholder;
	private final String value;

	/**
	 * @param action
	 * @param placeholder
	 * @param value
	 */
	public ZPlaceholderPermissible(PlaceholderAction action, String placeholder, String value) {
		super();
		this.action = action;
		this.placeholder = placeholder;
		this.value = value;
	}

	@Override
	public boolean hasPermission(Player player) {

		String valueAsString = papi(this.placeholder, player);

		if (this.action.equals(PlaceholderAction.BOOLEAN)) {

			try {
				return Boolean.valueOf(valueAsString) == Boolean.valueOf(this.value);
			} catch (Exception exception) {
				return true;
			}
			
		} else if (this.action.isString()) {

			switch (this.action) {
			case EQUALS_STRING:
				return valueAsString.equals(this.value);
			case EQUALSIGNORECASE_STRING:
				return valueAsString.equalsIgnoreCase(this.value);
			case CONTAINS_STRING:
				return valueAsString.contains(this.value);
			default:
				return true;
			}

		} else {

			try {

				double value = Double.valueOf(valueAsString);
				double currentValue = Double.valueOf(this.value);

				switch (this.action) {
				case EQUAL_TO:
					return value == currentValue;
				case LOWER:
					return value < currentValue;
				case LOWER_OR_EQUAL:
					return value <= currentValue;
				case SUPERIOR:
					return value > currentValue;
				case SUPERIOR_OR_EQUAL:
					return value >= currentValue;
				default:
					return true;
				}

			} catch (Exception exception) {
				return true;
			}

		}
	}

	@Override
	public PlaceholderAction getPlaceholderAction() {
		return this.action;
	}

	@Override
	public String getPlaceholder() {
		return this.placeholder;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
