package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.order.SlotGrid;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the mapping between the menu, the player inventory, and the single space an
 * animation is laid out over.
 */
class AnimationSlotsTest {

    private static final int SIZE = 54;

    @Test
    void theMenuComesFirstAndThePlayerInventoryAfterIt() {
        assertEquals(90, AnimationSlots.totalSize(SIZE));
        assertFalse(AnimationSlots.isPlayer(SIZE, 53));
        assertTrue(AnimationSlots.isPlayer(SIZE, 54));
    }

    @Test
    void theStorageRowsAreLaidOutBeforeTheHotbar() {
        assertEquals(54, AnimationSlots.fromPlayer(SIZE, 9));
        assertEquals(62, AnimationSlots.fromPlayer(SIZE, 17));
        assertEquals(80, AnimationSlots.fromPlayer(SIZE, 35));

        assertEquals(81, AnimationSlots.fromPlayer(SIZE, 0));
        assertEquals(89, AnimationSlots.fromPlayer(SIZE, 8));
    }

    @Test
    void theMappingGoesBothWays() {
        for (int playerSlot = 0; playerSlot < AnimationSlots.PLAYER_SIZE; playerSlot++) {
            int slot = AnimationSlots.fromPlayer(SIZE, playerSlot);
            assertEquals(playerSlot, AnimationSlots.toPlayer(SIZE, slot));
        }
    }

    @Test
    void everyPlayerSlotGetsItsOwnPlaceInTheAnimation() {
        Set<Integer> slots = new HashSet<>();
        for (int playerSlot = 0; playerSlot < AnimationSlots.PLAYER_SIZE; playerSlot++) {
            assertTrue(slots.add(AnimationSlots.fromPlayer(SIZE, playerSlot)));
        }
        assertEquals(AnimationSlots.PLAYER_SIZE, slots.size());
    }

    @Test
    void theCombinedSpaceStaysAGridSoTheGridOrdersKeepWorking() {
        assertEquals(9, SlotGrid.width(AnimationSlots.totalSize(SIZE)));
        assertEquals(10, SlotGrid.height(AnimationSlots.totalSize(SIZE)));

        assertEquals(9, SlotGrid.width(AnimationSlots.totalSize(27)));
        assertEquals(7, SlotGrid.height(AnimationSlots.totalSize(27)));
    }
}
