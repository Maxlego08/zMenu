package fr.maxlego08.menu.api.animation.slot.order;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the animation whose order is drawn in a matrix. Reading the matrix option is tested with its loader.
 */
class MatrixSlotAnimationTest {

    private static final int SIZE = 27;
    private static final List<Integer> ALL_SLOTS = IntStream.range(0, SIZE).boxed().toList();

    @Test
    void everyCharacterIsAStepPlayedInCharacterOrder() {
        MatrixSlotAnimation animation = new MatrixSlotAnimation(List.of(
                "111111111",
                "122222221",
                "111111111"));

        List<Integer> ordered = animation.order(SIZE, ALL_SLOTS);

        assertIterableEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26), ordered.subList(0, 20));
        assertIterableEquals(List.of(10, 11, 12, 13, 14, 15, 16), ordered.subList(20, 27));
    }

    @Test
    void charactersSortNaturallySoDigitsComeBeforeLetters() {
        MatrixSlotAnimation animation = new MatrixSlotAnimation(List.of("a1b2"));

        assertIterableEquals(List.of(1, 3, 0, 2), animation.order(SIZE, List.of(0, 1, 2, 3)));
    }

    @Test
    void slotsTheMatrixDoesNotCoverComeLast() {
        MatrixSlotAnimation animation = new MatrixSlotAnimation(List.of("11     11"));

        List<Integer> ordered = animation.order(SIZE, ALL_SLOTS);
        assertIterableEquals(List.of(0, 1, 7, 8), ordered.subList(0, 4));
        assertIterableEquals(List.of(2, 3, 4, 5, 6, 9, 10), ordered.subList(4, 11));
    }

    @Test
    void aRowLongerThanTheGridIsTruncated() {
        MatrixSlotAnimation animation = new MatrixSlotAnimation(List.of("1111111112222"));

        assertIterableEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), animation.order(SIZE, ALL_SLOTS).subList(0, 9));
    }

    @Test
    void everySlotIsReturnedExactlyOnce() {
        MatrixSlotAnimation animation = new MatrixSlotAnimation(List.of("121212121", "212121212"));
        List<Integer> ordered = animation.order(SIZE, ALL_SLOTS);

        assertEquals(SIZE, ordered.size());
        assertEquals(new HashSet<>(ALL_SLOTS), new HashSet<>(ordered));
    }
}
