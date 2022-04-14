package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PermissibleButton;

public abstract class ZPermissibleButton extends ZButton implements PermissibleButton {

	private final String permission;
	private final Button elseButton;

	/**
	 * @param buttonName
	 * @param itemStack
	 * @param slot
	 * @param isPermanent
	 * @param permission
	 * @param elseButton
	 */
	public ZPermissibleButton(String buttonName, ItemStack itemStack, int slot, boolean isPermanent, String permission,
			Button elseButton) {
		super(buttonName, itemStack, slot, isPermanent);
		this.permission = permission;
		this.elseButton = elseButton;
	}

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
	public boolean checkPermission(Player player) {
		return this.permission == null || player.hasPermission(this.permission);
	}

}
