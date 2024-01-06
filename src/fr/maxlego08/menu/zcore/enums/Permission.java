package fr.maxlego08.menu.zcore.enums;

public enum Permission {
	
	ZMENU_RELOAD,
	ZMENU_OPEN, 
	ZMENU_SAVE,
	ZMENU_USE,
	ZMENU_PLAYERS,
	ZMENU_CONVERT,
	ZMENU_LIST,
	ZMENU_TEST_DUPE,
	ZMENU_OPEN_ITEM,
	ZMENU_CREATE,
	ZMENU_DOWNLOAD,
	ZMENU_LOGIN,
	ZMENU_DESCRIPTION,

	;

	private String permission;

	Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
