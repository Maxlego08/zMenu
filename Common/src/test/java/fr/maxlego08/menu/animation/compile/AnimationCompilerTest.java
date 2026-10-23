package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.animation.slot.order.HorizontalSnakeSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the animation timeline. The compiler decides every time before anything is
 * displayed, so it is testable without a server.
 */
class AnimationCompilerTest {

    private static final int SIZE = 27;

    @Test
    void slotsAreRevealedOneIntervalApart() {
        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1, 2), Map.of());

        assertIterableEquals(List.of(0L, 50L, 100L), times(steps));
        assertIterableEquals(List.of(0, 1, 2), slots(steps));
    }

    @Test
    void slotsOfTheSameStepShareTheSameTime() {
        List<AnimationStep> steps = compile(group(50, 2), List.of(0, 1, 2, 3), Map.of());

        assertIterableEquals(List.of(0L, 0L, 50L, 50L), times(steps));
    }

    @Test
    void aDisabledButtonIsNotAnimatedAtAll() {
        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1, 2),
                Map.of(1, ButtonAnimationSettings.DISABLED));

        assertIterableEquals(List.of(0, 2), slots(steps));
        assertIterableEquals(List.of(0L, 50L), times(steps));
    }

    @Test
    void everythingDisabledCompilesToNothing() {
        CompiledAnimation compiled = compileAll(group(50, 1), List.of(0, 1),
                Map.of(0, ButtonAnimationSettings.DISABLED, 1, ButtonAnimationSettings.DISABLED));

        assertTrue(compiled.isEmpty());
        assertEquals(0, compiled.duration());
    }

    @Test
    void aGroupStartsOnceThePreviousOneIsOver() {
        Map<Integer, ButtonAnimationSettings> slotSettings = Map.of(
                9, new ButtonAnimationSettings(true, 2, 0));

        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1, 9), slotSettings);

        assertIterableEquals(List.of(0L, 50L, 100L), times(steps));
        assertIterableEquals(List.of(0, 1, 9), slots(steps));
    }

    @Test
    void aGroupCanDelayItsOwnStart() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                group(50, 1),
                Map.of(2, new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 20)),
                true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 9),
                Map.of(9, new ButtonAnimationSettings(true, 2, 0))).steps();

        assertIterableEquals(List.of(0L, 70L), times(steps));
    }

    @Test
    void aGroupCanOverrideItsInterval() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                group(50, 1),
                Map.of(2, new SlotAnimationGroup(new SequentialSlotAnimation(), 10, 1, 0)),
                true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 9, 10),
                Map.of(9, new ButtonAnimationSettings(true, 2, 0),
                        10, new ButtonAnimationSettings(true, 2, 0))).steps();

        assertIterableEquals(List.of(0L, 50L, 60L), times(steps));
    }

    @Test
    void aButtonDelayShiftsOnlyItsOwnSlots() {
        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1, 2),
                Map.of(1, new ButtonAnimationSettings(true, 1, 30)));

        assertIterableEquals(List.of(0, 1, 2), slots(steps));
        assertIterableEquals(List.of(0L, 80L, 100L), times(steps));
    }

    @Test
    void aDelayedSlotStillHoldsBackTheNextGroup() {
        Map<Integer, ButtonAnimationSettings> slotSettings = Map.of(
                1, new ButtonAnimationSettings(true, 1, 500),
                9, new ButtonAnimationSettings(true, 2, 0));

        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1, 9), slotSettings);

        assertIterableEquals(List.of(0L, 550L, 600L), times(steps));
    }

    @Test
    void theGroupAnimationOrdersEveryButtonOfTheGroupTogether() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new HorizontalSnakeSlotAnimation(), 50, 1, 0), true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(9, 10, 11), Map.of()).steps();
        assertIterableEquals(List.of(11, 10, 9), slots(steps));
    }

    @Test
    void stepsAreSortedByTimeThenSlot() {
        CompiledAnimation compiled = compileAll(group(50, 3), List.of(2, 0, 1), Map.of());

        assertIterableEquals(List.of(0, 1, 2), slots(compiled.steps()));
        assertEquals(0, compiled.duration());
    }

    @Test
    void everyStepRevealsItsButtonByDefault() {
        List<AnimationStep> steps = compile(group(50, 1), List.of(0, 1), Map.of());

        assertTrue(steps.stream().allMatch(step -> step.action() == StepAction.REVEAL));
    }

    @Test
    void theBoundariesOfEveryGroupAreRecorded() {
        CompiledAnimation compiled = compileAll(group(50, 1), List.of(0, 1, 9),
                Map.of(9, new ButtonAnimationSettings(true, 2, 0)));

        AnimationTiming timing = compiled.timing();
        assertEquals(new GroupTiming(0, 100), timing.groups().get(1));
        assertEquals(new GroupTiming(100, 150), timing.groups().get(2));
        assertEquals(150, timing.end());
    }

    @Test
    void anchorsResolveAgainstTheGroupBoundaries() {
        AnimationTiming timing = compileAll(group(50, 1), List.of(0, 1, 9),
                Map.of(9, new ButtonAnimationSettings(true, 2, 0))).timing();

        assertEquals(0, timing.resolve(AnimationAnchor.START));
        assertEquals(150, timing.resolve(AnimationAnchor.END));
        assertEquals(250, timing.resolve(AnimationAnchor.at(250)));
        assertEquals(0, timing.resolve(AnimationAnchor.groupStart(1)));
        assertEquals(100, timing.resolve(AnimationAnchor.groupEnd(1)));
        assertEquals(100, timing.resolve(AnimationAnchor.groupStart(2)));
        assertEquals(150, timing.resolve(AnimationAnchor.groupEnd(2)));
    }

    @Test
    void anAnchorOnAnEmptyGroupResolvesToTheEnd() {
        AnimationTiming timing = compileAll(group(50, 1), List.of(0, 1), Map.of()).timing();

        assertFalse(timing.hasGroup(7));
        assertEquals(timing.end(), timing.resolve(AnimationAnchor.groupStart(7)));
        assertEquals(timing.end(), timing.resolve(AnimationAnchor.groupEnd(7)));
    }

    @Test
    void anAnimationWithoutAnyGroupHasNoDuration() {
        assertEquals(0, AnimationTiming.EMPTY.end());
        assertEquals(0, AnimationTiming.EMPTY.resolve(AnimationAnchor.END));
        assertEquals(0, AnimationTiming.EMPTY.resolve(null));
    }

    @Test
    void reverseFlipsWhateverOrderTheAnimationProduced() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0, null, true), true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2), Map.of()).steps();

        assertIterableEquals(List.of(2, 1, 0), steps.stream()
                .sorted(java.util.Comparator.comparingLong(AnimationStep::time))
                .map(AnimationStep::slot).toList());
    }

    private static SlotAnimationGroup group(long interval, int slotsPerStep) {
        return new SlotAnimationGroup(new SequentialSlotAnimation(), interval, slotsPerStep, 0);
    }

    private static List<AnimationStep> compile(SlotAnimationGroup defaults, List<Integer> slots, Map<Integer, ButtonAnimationSettings> slotSettings) {
        return compileAll(defaults, slots, slotSettings).steps();
    }

    private static CompiledAnimation compileAll(SlotAnimationGroup defaults, List<Integer> slots, Map<Integer, ButtonAnimationSettings> slotSettings) {
        return AnimationCompiler.compile(SIZE, new SlotAnimationSettings(defaults, true), slots, slotSettings);
    }

    private static List<Long> times(List<AnimationStep> steps) {
        return steps.stream().map(AnimationStep::time).toList();
    }

    private static List<Integer> slots(List<AnimationStep> steps) {
        return steps.stream().map(AnimationStep::slot).toList();
    }
}
