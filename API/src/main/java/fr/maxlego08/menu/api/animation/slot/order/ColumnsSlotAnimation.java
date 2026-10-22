package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals the slots column by column, from left to right, each column top to bottom.
 *
 * <p>Unlike {@link VerticalSnakeSlotAnimation} every column is walked in the same direction.</p>
 */
public class ColumnsSlotAnimation implements SlotAnimation {

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> slot % width)
                .thenComparingInt(slot -> slot / width));
        return ordered;
    }
}
