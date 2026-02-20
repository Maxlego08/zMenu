package fr.maxlego08.menu.common.enums;

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
	ZMENU_MARKETPLACE,
	ZMENU_INVENTORIES,
	ZMENU_DESCRIPTION,
    ZMENU_OPEN_DIALOG,
    ZMENU_RELOAD_DIALOG,
    ZMENU_OPEN_BEDROCK,
    ZMENU_RELOAD_BEDROCK,
    ZMENU_DUMPLOG,
    ZMENU_CONTRIBUTORS,
    ZMENU_GIVE_ITEM,

    ZMENU_ADDONS,
    ZMENU_DOCUMENTATION;

	private final String permission;

	Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
