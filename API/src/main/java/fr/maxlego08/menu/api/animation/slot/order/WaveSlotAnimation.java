package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals the slots as a diagonal sweep running from the top left corner to the bottom right one.
 */
public class WaveSlotAnimation implements SlotAnimation {

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> (slot / width) + (slot % width))
                .thenComparingInt(slot -> slot));
        return ordered;
    }
}
