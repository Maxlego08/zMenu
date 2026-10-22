package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals the slots row by row, alternating the direction of each row, so the reveal snakes from the
 * top left corner down to the bottom of the inventory.
 */
public class HorizontalSnakeSlotAnimation implements SlotAnimation {

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> slot / width)
                .thenComparingInt(slot -> {
                    int column = slot % width;
                    return (slot / width) % 2 == 0 ? column : width - 1 - column;
                }));
        return ordered;
    }
}
