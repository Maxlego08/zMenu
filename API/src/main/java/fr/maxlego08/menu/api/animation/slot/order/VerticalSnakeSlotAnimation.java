package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals the slots column by column, alternating the direction of each column, so the reveal snakes
 * from the top left corner across to the right of the inventory.
 */
public class VerticalSnakeSlotAnimation implements SlotAnimation {

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);
        int height = SlotGrid.height(inventorySize);

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> slot % width)
                .thenComparingInt(slot -> {
                    int row = slot / width;
                    return (slot % width) % 2 == 0 ? row : height - 1 - row;
                }));
        return ordered;
    }
}
