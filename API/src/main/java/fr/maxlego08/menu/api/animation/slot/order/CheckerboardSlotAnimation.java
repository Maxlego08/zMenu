package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals one slot out of two first, then fills the gaps, so the inventory appears as a chequered
 * pattern before it completes.
 */
public class CheckerboardSlotAnimation implements SlotAnimation {

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> ((slot / width) + (slot % width)) % 2)
                .thenComparingInt(slot -> slot));
        return ordered;
    }
}
