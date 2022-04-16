package fr.maxlego08.menu.zcore.enums;

public enum Permission {
	ZMENU_RELAOD

	;

	private String permission;

	private Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
