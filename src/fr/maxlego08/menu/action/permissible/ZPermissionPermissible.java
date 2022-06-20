package fr.maxlego08.menu.action.permissible;

import org.bukkit.entity.Player;

import fr.maxlego08.menu.api.action.permissible.PermissionPermissible;

public class ZPermissionPermissible implements PermissionPermissible {

	private final String permission;
	private final boolean isReverse;

	/**
	 * @param permission
	 * @param isReverse
	 */
	public ZPermissionPermissible(String permission, boolean isReverse) {
		super();
		this.permission = permission;
		this.isReverse = isReverse;
	}

	@Override
	public boolean hasPermission(Player player) {
		return this.permission == null
				|| (this.isReverse ? !player.hasPermission(this.permission) : player.hasPermission(this.permission));
	}

	@Override
	public String getPermission() {
		return this.permission;
	}

	@Override
	public boolean isReverse() {
		return this.isReverse;
	}

}
