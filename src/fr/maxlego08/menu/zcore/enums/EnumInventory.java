package fr.maxlego08.menu.zcore.enums;

public enum EnumInventory {

	INVENTORY_DEFAULT(1),
	
	;
	
	private final int id;

	private EnumInventory(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
