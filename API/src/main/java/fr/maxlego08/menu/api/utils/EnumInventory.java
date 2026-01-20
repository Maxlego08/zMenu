package fr.maxlego08.menu.api.utils;

/**
 * Enum representing different types of inventory presets, each associated with a unique ID.
 */
public enum EnumInventory {

    /**
     * Standard/default inventory.
     */
    INVENTORY_DEFAULT(1),

    /**
     * Inventory specifically for marketplace interfaces.
     */
    INVENTORY_MARKETPLACE(2),

	;
	
	private final int id;

	EnumInventory(int id) {
		this.id = id;
	}

    /**
     * Returns the identifier associated with this inventory type.
     *
     * @return the numerical ID of the inventory.
     */
    public int getId() {
        return id;
    }

}
