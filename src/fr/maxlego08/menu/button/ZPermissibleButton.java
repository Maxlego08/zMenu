package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public abstract class ZPermissibleButton extends ZButton implements PermissibleButton {

	private String permission;
	private Button elseButton;

	@Override
	public Button getElseButton() {
		return this.elseButton;
	}

	@Override
	public String getPermission() {
		return this.permission;
	}

	@Override
	public boolean hasPermission() {
		return this.permission != null;
	}

	@Override
	public boolean hasElseButton() {
		return this.elseButton != null;
	}

	@Override
	public boolean checkPermission(Player player, InventoryDefault inventory) {
		return this.permission == null || player.hasPermission(this.permission);
	}

	/**
	 * @param permission
	 *            the permission to set
	 */
	public ZPermissibleButton setPermission(String permission) {
		this.permission = permission;
		return this;
	}

	/**
	 * @param elseButton
	 *            the elseButton to set
	 */
	public ZPermissibleButton setElseButton(Button elseButton) {
		this.elseButton = elseButton;
		return this;
	}

}
