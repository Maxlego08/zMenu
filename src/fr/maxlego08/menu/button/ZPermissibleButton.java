package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;

public abstract class ZPermissibleButton extends ZPerformButton implements PermissibleButton{

	private String permission;
	private Button elseButton;
	private Button parentButton;
	private boolean isReverse;

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
		return this.permission == null
				|| (this.isReverse ? !player.hasPermission(this.permission) : player.hasPermission(this.permission));
	}

	/**
	 * @param permission
	 *            the permission to set
	 */
	public ZPermissibleButton setPermission(String permission) {
		this.permission = permission;
		this.isReverse = permission != null && permission.startsWith("!");
		if (this.isReverse){
			this.permission = permission.substring(1, permission.length());
		}
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

	/**
	 * 
	 * @param parentButton
	 * @return button
	 */
	public ZPermissibleButton setParentButton(Button parentButton) {
		this.parentButton = parentButton;
		return this;
	}

	@Override
	public boolean isReverse() {
		return this.isReverse;
	}

	@Override
	public Button getParentButton() {
		return this.parentButton;
	}

	@Override
	public Button getMasterParentButton() {
		Button button = this.getParentButton();
		return button == null ? (Button) this : button.getMasterParentButton();
	}

}
