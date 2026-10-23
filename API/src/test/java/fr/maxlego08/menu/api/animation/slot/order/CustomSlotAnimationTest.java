package fr.maxlego08.menu.api.animation.slot.order;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the animation whose order is listed slot by slot.
 */
class CustomSlotAnimationTest {

    private static final int SIZE = 27;
    private static final List<Integer> ALL_SLOTS = IntStream.range(0, SIZE).boxed().toList();

    @Test
    void slotsAreRevealedInTheOrderTheyAreListed() {
        CustomSlotAnimation animation = new CustomSlotAnimation(List.of(13, 4, 22, 12, 14));

        assertIterableEquals(List.of(13, 4, 22, 12, 14), animation.order(SIZE, List.of(4, 12, 13, 14, 22)));
    }

    @Test
    void slotsThatAreNotListedComeLast() {
        CustomSlotAnimation animation = new CustomSlotAnimation(List.of(13, 4));
        List<Integer> ordered = animation.order(SIZE, ALL_SLOTS);

        assertIterableEquals(List.of(13, 4), ordered.subList(0, 2));
        assertIterableEquals(List.of(0, 1, 2, 3, 5, 6), ordered.subList(2, 8));
    }

    @Test
    void aSlotListedTwiceKeepsItsFirstPlace() {
        CustomSlotAnimation animation = new CustomSlotAnimation(List.of(5, 3, 5, 1));

        assertIterableEquals(List.of(5, 3, 1), animation.order(SIZE, List.of(1, 3, 5)));
    }

    @Test
    void everySlotIsReturnedExactlyOnce() {
        CustomSlotAnimation animation = new CustomSlotAnimation(List.of(26, 0, 13));
        List<Integer> ordered = animation.order(SIZE, ALL_SLOTS);

        assertEquals(SIZE, ordered.size());
        assertEquals(new HashSet<>(ALL_SLOTS), new HashSet<>(ordered));
    }
}
