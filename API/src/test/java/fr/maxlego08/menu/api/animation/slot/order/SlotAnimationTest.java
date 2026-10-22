package fr.maxlego08.menu.api.animation.slot.order;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Unit tests for the slot orderings used by the opening animation. They are pure arithmetic over a
 * grid, so nothing here needs a server.
 */
class SlotAnimationTest {

    private static final int SIZE = 27;
    private static final List<Integer> ALL_SLOTS = IntStream.range(0, SIZE).boxed().toList();

    @Test
    void gridIsNineWideForEveryChestSize() {
        assertEquals(9, SlotGrid.width(54));
        assertEquals(6, SlotGrid.height(54));
        assertEquals(9, SlotGrid.width(9));
        assertEquals(1, SlotGrid.height(9));
    }

    @Test
    void gridFallsBackToASingleRowForOtherSizes() {
        assertEquals(5, SlotGrid.width(5));
        assertEquals(1, SlotGrid.height(5));
    }

    @Test
    void sequentialKeepsTheSlotOrder() {
        assertIterableEquals(ALL_SLOTS, new SequentialSlotAnimation().order(SIZE, ALL_SLOTS));
    }

    @Test
    void horizontalSnakeReversesEveryOtherRow() {
        List<Integer> expected = List.of(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                17, 16, 15, 14, 13, 12, 11, 10, 9,
                18, 19, 20, 21, 22, 23, 24, 25, 26);
        assertIterableEquals(expected, new HorizontalSnakeSlotAnimation().order(SIZE, ALL_SLOTS));
    }

    @Test
    void horizontalSnakeHandlesSparseSlots() {
        assertIterableEquals(List.of(17, 9), new HorizontalSnakeSlotAnimation().order(SIZE, List.of(9, 17)));
    }

    @Test
    void verticalSnakeReversesEveryOtherColumn() {
        List<Integer> ordered = new VerticalSnakeSlotAnimation().order(SIZE, ALL_SLOTS);
        assertIterableEquals(List.of(0, 9, 18, 19, 10, 1, 2, 11, 20), ordered.subList(0, 9));
        assertEquals(SIZE, ordered.size());
    }

    @Test
    void spiralWalksTheGridClockwiseAndInwards() {
        List<Integer> expected = List.of(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                17, 26,
                25, 24, 23, 22, 21, 20, 19, 18,
                9,
                10, 11, 12, 13, 14, 15, 16);
        assertIterableEquals(expected, new SpiralSlotAnimation().order(SIZE, ALL_SLOTS));
    }

    @Test
    void fromCenterStartsAtTheMiddleSlot() {
        List<Integer> ordered = new FromCenterSlotAnimation().order(SIZE, ALL_SLOTS);
        assertEquals(13, ordered.getFirst());
        assertEquals(new HashSet<>(List.of(0, 8, 18, 26)), new HashSet<>(ordered.subList(ordered.size() - 4, ordered.size())));
    }

    @Test
    void waveSweepsAlongTheDiagonals() {
        List<Integer> ordered = new WaveSlotAnimation().order(SIZE, ALL_SLOTS);

        assertIterableEquals(List.of(0, 1, 9, 2, 10, 18), ordered.subList(0, 6));
        assertEquals(26, ordered.getLast());
    }

    @Test
    void checkerboardFillsOneSlotOutOfTwoFirst() {
        List<Integer> ordered = new CheckerboardSlotAnimation().order(SIZE, ALL_SLOTS);

        assertIterableEquals(List.of(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26), ordered.subList(0, 14));
        assertIterableEquals(List.of(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25), ordered.subList(14, 27));
    }

    @Test
    void columnsWalkEveryColumnInTheSameDirection() {
        List<Integer> ordered = new ColumnsSlotAnimation().order(SIZE, ALL_SLOTS);

        assertIterableEquals(List.of(0, 9, 18, 1, 10, 19, 2, 11, 20), ordered.subList(0, 9));
    }

    @Test
    void rowsIsSequentialUnderAnotherName() {
        assertIterableEquals(new SequentialSlotAnimation().order(SIZE, ALL_SLOTS), ALL_SLOTS);
    }

    @Test
    void everyAnimationReturnsEachSlotExactlyOnce() {
        List<Integer> sparse = List.of(0, 4, 8, 13, 18, 22, 26);
        List<SlotAnimation> animations = List.of(
                new SequentialSlotAnimation(),
                new HorizontalSnakeSlotAnimation(),
                new VerticalSnakeSlotAnimation(),
                new SpiralSlotAnimation(),
                new FromCenterSlotAnimation(),
                new WaveSlotAnimation(),
                new CheckerboardSlotAnimation(),
                new ColumnsSlotAnimation(),
                new RandomSlotAnimation(new Random(42)));

        for (SlotAnimation animation : animations) {
            List<Integer> ordered = animation.order(SIZE, sparse);
            assertEquals(sparse.size(), ordered.size(), animation.getClass().getSimpleName());
            assertEquals(new HashSet<>(sparse), new HashSet<>(ordered), animation.getClass().getSimpleName());
        }
    }

    @Test
    void groupSettingsClampNonSensicalValues() {
        SlotAnimationGroup group = new SlotAnimationGroup(new SequentialSlotAnimation(), 0, 0, -5);
        assertEquals(1, group.interval());
        assertEquals(1, group.slotsPerStep());
        assertEquals(0, group.delay());
    }

    @Test
    void aGroupWithoutAnOverrideUsesTheDefaults() {
        SlotAnimationGroup defaults = new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0);
        SlotAnimationGroup second = new SlotAnimationGroup(new SpiralSlotAnimation(), 10, 1, 0);
        SlotAnimationSettings settings = new SlotAnimationSettings(defaults, java.util.Map.of(2, second), true);

        assertEquals(defaults, settings.group(1));
        assertEquals(second, settings.group(2));
        assertEquals(defaults, settings.group(7));
    }
}
