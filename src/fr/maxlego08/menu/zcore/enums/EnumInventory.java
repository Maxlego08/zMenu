package fr.maxlego08.menu.zcore.enums;

public enum EnumInventory {

	INVENTORY_DEFAULT(1),
	INVENTORY_MARKETPLACE(2),

	;
	
	private final int id;

	EnumInventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
