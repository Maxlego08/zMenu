package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Reveals the slots closest to the middle of the inventory first, expanding outwards.
 */
public class FromCenterSlotAnimation implements SlotAnimation {

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "from_center");
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        int width = SlotGrid.width(inventorySize);
        int height = SlotGrid.height(inventorySize);

        double centerColumn = (width - 1) / 2.0;
        double centerRow = (height - 1) / 2.0;

        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingDouble((Integer slot) -> {
                    double columnDistance = (slot % width) - centerColumn;
                    double rowDistance = (slot / width) - centerRow;
                    return (columnDistance * columnDistance) + (rowDistance * rowDistance);
                })
                .thenComparingInt(slot -> slot));
        return ordered;
    }
}
