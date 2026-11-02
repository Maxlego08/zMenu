package fr.maxlego08.menu.hooks.packetevents.feature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HiddenInventoryFeatureTest {

    @Test
    void containerWindowPutsStorageThenHotbarAfterTheTopInventory() {
        assertEquals(54, HiddenInventoryFeature.toWindowIndex(1, 54, 9));
        assertEquals(80, HiddenInventoryFeature.toWindowIndex(1, 54, 35));
        assertEquals(81, HiddenInventoryFeature.toWindowIndex(1, 54, 0));
        assertEquals(89, HiddenInventoryFeature.toWindowIndex(1, 54, 8));
        assertEquals(-1, HiddenInventoryFeature.toWindowIndex(1, 54, 40));
    }

    @Test
    void playerWindowUsesItsOwnLayout() {
        assertEquals(36, HiddenInventoryFeature.toWindowIndex(0, 0, 0));
        assertEquals(44, HiddenInventoryFeature.toWindowIndex(0, 0, 8));
        assertEquals(9, HiddenInventoryFeature.toWindowIndex(0, 0, 9));
        assertEquals(8, HiddenInventoryFeature.toWindowIndex(0, 0, 36));
        assertEquals(5, HiddenInventoryFeature.toWindowIndex(0, 0, 39));
        assertEquals(45, HiddenInventoryFeature.toWindowIndex(0, 0, 40));
    }

    @Test
    void windowIndexMapsBackToTheSamePlayerSlot() {
        for (int slot = 0; slot < 36; slot++) {
            assertEquals(slot, HiddenInventoryFeature.fromWindowIndex(27, HiddenInventoryFeature.toWindowIndex(2, 27, slot)));
            assertEquals(slot, HiddenInventoryFeature.fromWindowIndex(0, HiddenInventoryFeature.toWindowIndex(0, 0, slot)));
        }
        assertEquals(40, HiddenInventoryFeature.fromWindowIndex(0, 45));
        assertEquals(-1, HiddenInventoryFeature.fromWindowIndex(54, 10));
    }
}
