package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reveals the slots by walking the inventory in an inward spiral, starting from the top left corner
 * and turning clockwise.
 */
public class SpiralSlotAnimation implements SlotAnimation {

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "spiral");
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);
        int height = SlotGrid.height(inventorySize);

        Set<Integer> remaining = new TreeSet<>(slots);
        Set<Integer> ordered = new LinkedHashSet<>(slots.size());

        int top = 0, bottom = height - 1, left = 0, right = width - 1;
        while (top <= bottom && left <= right) {
            for (int column = left; column <= right; column++) this.take(ordered, remaining, top * width + column);
            top++;

            for (int row = top; row <= bottom; row++) this.take(ordered, remaining, row * width + right);
            right--;

            if (top <= bottom) {
                for (int column = right; column >= left; column--) this.take(ordered, remaining, bottom * width + column);
                bottom--;
            }

            if (left <= right) {
                for (int row = bottom; row >= top; row--) this.take(ordered, remaining, row * width + left);
                left++;
            }
        }

        ordered.addAll(remaining);
        return new ArrayList<>(ordered);
    }

    private void take(Set<Integer> ordered, Set<Integer> remaining, int slot) {
        if (remaining.remove(slot)) {
            ordered.add(slot);
        }
    }
}
