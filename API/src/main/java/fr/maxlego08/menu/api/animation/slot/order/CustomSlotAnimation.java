package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reveals the slots in the order they are listed.
 *
 * <p>The list uses the same syntax as the slots of a button, so a line may be a single slot or a
 * range:</p>
 *
 * <pre>
 * type: CUSTOM
 * order:
 *   - 13
 *   - 12
 *   - 14
 *   - 4-22
 * </pre>
 *
 * <p>A slot listed twice keeps its first position, and slots the list does not mention are revealed
 * last, in ascending order, so a partial list still describes a complete animation.</p>
 */
public class CustomSlotAnimation implements SlotAnimation {

    private final Map<Integer, Integer> positionBySlot;

    /**
     * @param order the slots in the order they must be revealed
     */
    public CustomSlotAnimation(@NotNull List<Integer> order) {
        Set<Integer> unique = new LinkedHashSet<>(order);

        this.positionBySlot = new HashMap<>(unique.size());
        int position = 0;
        for (int slot : unique) {
            this.positionBySlot.put(slot, position++);
        }
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> this.positionBySlot.getOrDefault(slot, Integer.MAX_VALUE))
                .thenComparingInt(slot -> slot));
        return ordered;
    }
}
