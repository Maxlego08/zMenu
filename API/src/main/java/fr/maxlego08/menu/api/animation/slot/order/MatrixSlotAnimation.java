package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Reveals the slots in the order drawn in a matrix.
 *
 * <p>Each row of the matrix describes a row of the inventory, and each character a slot. Every
 * distinct character is a step, and the steps are played in the order the characters sort in, so
 * {@code 1} comes before {@code 2} and digits come before letters. A space means the slot is not
 * part of the drawing.</p>
 *
 * <pre>
 * matrix:
 *   - "111111111"
 *   - "122222221"
 *   - "111111111"
 * </pre>
 *
 * <p>Slots the matrix does not cover are revealed last, in ascending order, so a matrix shorter than
 * the inventory still describes a complete animation.</p>
 */
public class MatrixSlotAnimation implements SlotAnimation {

    private final Map<Integer, Integer> stepBySlot;

    /**
     * @param rows the rows of the matrix, at most 9 characters each
     */
    public MatrixSlotAnimation(@NotNull List<String> rows) {
        Map<Integer, Character> charBySlot = new HashMap<>();
        TreeSet<Character> characters = new TreeSet<>();

        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            for (int column = 0; column < Math.min(line.length(), SlotGrid.CHEST_WIDTH); column++) {
                char character = line.charAt(column);
                if (character == ' ') continue;

                charBySlot.put((row * SlotGrid.CHEST_WIDTH) + column, character);
                characters.add(character);
            }
        }

        List<Character> ordered = new ArrayList<>(characters);
        this.stepBySlot = new HashMap<>(charBySlot.size());
        charBySlot.forEach((slot, character) -> this.stepBySlot.put(slot, ordered.indexOf(character)));
    }

    @Override
    public @NotNull List<Integer> order(int inventorySize, @NotNull List<Integer> slots) {
        List<Integer> ordered = new ArrayList<>(slots);
        ordered.sort(Comparator
                .comparingInt((Integer slot) -> this.stepBySlot.getOrDefault(slot, Integer.MAX_VALUE))
                .thenComparingInt(slot -> slot));
        return ordered;
    }
}
