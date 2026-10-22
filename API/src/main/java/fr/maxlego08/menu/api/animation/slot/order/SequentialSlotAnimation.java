package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reveals the slots from the first to the last, left to right and row by row.
 */
public class SequentialSlotAnimation implements SlotAnimation {

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "sequential");
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        return List.copyOf(slots);
    }
}
