package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;

/**
 * Helpers shared by the grid based {@link SlotAnimation} implementations.
 *
 * <p>Inventories whose size is a multiple of 9 are treated as a 9 wide grid, which covers every
 * chest. Any other size is treated as a single row, so the grid based animations degrade to a plain
 * left to right reveal instead of computing meaningless coordinates.</p>
 */
public final class SlotGrid {

    public static final int CHEST_WIDTH = 9;

    private SlotGrid() {
    }

    /**
     * Gets the width of the grid for an inventory of the given size.
     *
     * @param inventorySize the inventory size
     * @return the number of columns
     */
    public static int width(int inventorySize) {
        return inventorySize > 0 && inventorySize % CHEST_WIDTH == 0 ? CHEST_WIDTH : Math.max(1, inventorySize);
    }

    /**
     * Gets the number of rows of the grid for an inventory of the given size.
     *
     * @param inventorySize the inventory size
     * @return the number of rows
     */
    public static int height(int inventorySize) {
        int width = width(inventorySize);
        return Math.max(1, (int) Math.ceil((double) inventorySize / width));
    }
}
