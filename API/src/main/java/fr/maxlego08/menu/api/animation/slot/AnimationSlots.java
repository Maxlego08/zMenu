package fr.maxlego08.menu.api.animation.slot;

/**
 * Maps the slots of the menu and the slots of the player inventory into the one space an animation
 * is laid out over.
 *
 * <p>An animation should be able to sweep from the menu into the player inventory without a seam, so
 * the two are compiled together: the slots of the menu come first, then the slots of the player
 * inventory as they appear on screen.</p>
 *
 * <p>That last part is the reason this class exists. Bukkit numbers the player inventory with the
 * hotbar first, slots 0 to 8, and the three rows above it from 9 to 35, which is the opposite of the
 * order they are displayed in. Animating over the raw numbers would make a spiral or a snake jump
 * from the bottom row back to the top.</p>
 */
public final class AnimationSlots {

    /**
     * The number of player inventory slots taken part in an animation: the three rows of storage and
     * the hotbar.
     */
    public static final int PLAYER_SIZE = 36;

    /**
     * The first player inventory slot as Bukkit numbers them, above the hotbar.
     */
    private static final int FIRST_STORAGE_SLOT = 9;

    private AnimationSlots() {
    }

    /**
     * The total number of slots an animation is laid out over.
     *
     * @param inventorySize the size of the menu
     * @return the size of the menu plus the player inventory
     */
    public static int totalSize(int inventorySize) {
        return inventorySize + PLAYER_SIZE;
    }

    /**
     * Turns a player inventory slot into the slot the animation uses for it.
     *
     * @param inventorySize the size of the menu
     * @param playerSlot    the slot as Bukkit numbers it, 0 to 35
     * @return the slot in the animation space
     */
    public static int fromPlayer(int inventorySize, int playerSlot) {
        int display = playerSlot >= FIRST_STORAGE_SLOT
                ? playerSlot - FIRST_STORAGE_SLOT
                : (PLAYER_SIZE - FIRST_STORAGE_SLOT) + playerSlot;

        return inventorySize + display;
    }

    /**
     * Turns a slot of the animation space back into a player inventory slot.
     *
     * @param inventorySize the size of the menu
     * @param slot          the slot in the animation space
     * @return the slot as Bukkit numbers it, 0 to 35
     */
    public static int toPlayer(int inventorySize, int slot) {
        int display = slot - inventorySize;

        return display < PLAYER_SIZE - FIRST_STORAGE_SLOT
                ? display + FIRST_STORAGE_SLOT
                : display - (PLAYER_SIZE - FIRST_STORAGE_SLOT);
    }

    /**
     * Returns whether a slot of the animation space belongs to the player inventory.
     *
     * @param inventorySize the size of the menu
     * @param slot          the slot in the animation space
     * @return true if the slot is in the player inventory
     */
    public static boolean isPlayer(int inventorySize, int slot) {
        return slot >= inventorySize;
    }
}
