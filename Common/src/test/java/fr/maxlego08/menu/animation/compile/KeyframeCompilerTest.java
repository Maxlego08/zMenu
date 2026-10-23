package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import fr.maxlego08.menu.api.animation.slot.TestSounds;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for temporary items described by several keyframes.
 */
class KeyframeCompilerTest {

    private static final int SIZE = 27;
    private static final MenuItemStack WHITE = TestMenuItemStacks.named("white");
    private static final MenuItemStack RED = TestMenuItemStacks.named("red");

    @Test
    void everyKeyframeChangesTheDisplayedItem() {
        List<AnimationStep> steps = onSlot(20, compile(List.of(0),
                frame(AnimationAnchor.at(0), WHITE),
                frame(AnimationAnchor.at(100), RED),
                removal(AnimationAnchor.at(200))));

        assertIterableEquals(List.of(0L, 100L, 200L), steps.stream().map(AnimationStep::time).toList());
        assertSame(WHITE, steps.get(0).item());
        assertSame(RED, steps.get(1).item());
        assertEquals(StepAction.CLEAR, steps.get(2).action());
    }

    @Test
    void keyframesAreSortedByTimeWhateverTheOrderTheyWereWrittenIn() {
        List<AnimationStep> steps = onSlot(20, compile(List.of(0),
                frame(AnimationAnchor.at(300), RED),
                frame(AnimationAnchor.at(100), WHITE),
                removal(AnimationAnchor.at(400))));

        assertIterableEquals(List.of(100L, 300L, 400L), steps.stream().map(AnimationStep::time).toList());
        assertSame(WHITE, steps.get(0).item());
        assertSame(RED, steps.get(1).item());
    }

    @Test
    void anItemThatIsNeverRemovedGoesAwayWhenTheAnimationIsOver() {
        CompiledAnimation compiled = compile(List.of(0, 1), frame(AnimationAnchor.at(0), WHITE));
        List<AnimationStep> steps = onSlot(20, compiled);

        assertEquals(2, steps.size());
        assertEquals(StepAction.CLEAR, steps.get(1).action());
        assertEquals(compiled.timing().end(), steps.get(1).time());
    }

    @Test
    void anItemPlacedAfterTheEndWithoutARemovalIsNotDisplayed() {
        CompiledAnimation compiled = compile(List.of(0, 1), frame(AnimationAnchor.at(5000), WHITE));

        assertTrue(onSlot(20, compiled).isEmpty());
    }

    @Test
    void anItemPlacedAfterTheEndIsDisplayedWhenItSaysWhenToGo() {
        CompiledAnimation compiled = compile(List.of(0, 1),
                frame(AnimationAnchor.at(5000), WHITE),
                removal(AnimationAnchor.at(5400)));
        List<AnimationStep> steps = onSlot(20, compiled);

        assertEquals(5000, steps.get(0).time());
        assertEquals(StepAction.CLEAR, steps.get(1).action());
        assertEquals(5400, steps.get(1).time());
    }

    @Test
    void aButtonCoveredAcrossSeveralKeyframesIsRevealedOnlyAtTheEnd() {
        List<AnimationStep> steps = onSlot(1, compileOn(1, List.of(0, 1, 2),
                frame(AnimationAnchor.at(0), WHITE),
                frame(AnimationAnchor.at(100), RED),
                removal(AnimationAnchor.at(300))));

        assertIterableEquals(List.of(0L, 100L, 300L), steps.stream().map(AnimationStep::time).toList());
        assertEquals(StepAction.REVEAL, steps.get(2).action());
    }

    @Test
    void anItemShownAgainAfterARemovalCoversTheButtonOnceMore() {
        List<AnimationStep> steps = onSlot(1, compileOn(1, List.of(0, 1, 2),
                frame(AnimationAnchor.at(0), WHITE),
                removal(AnimationAnchor.at(80)),
                frame(AnimationAnchor.at(200), RED),
                removal(AnimationAnchor.at(400))));

        assertIterableEquals(List.of(0L, 80L, 200L, 400L), steps.stream().map(AnimationStep::time).toList());
        assertEquals(StepAction.REVEAL, steps.get(1).action());
        assertEquals(StepAction.REVEAL, steps.get(3).action());
    }

    @Test
    void aKeyframeSoundIsPlayedOnceWhateverTheNumberOfSlots() {
        TemporaryItem item = new TemporaryItem("test", List.of(20, 21, 22),
                List.of(new AnimationKeyframe(AnimationAnchor.at(0), WHITE, TestSounds.SILENT),
                        new AnimationKeyframe(AnimationAnchor.at(100), null, TestSounds.SILENT)));

        List<AnimationStep> steps = compileItems(List.of(0), item).steps();

        assertEquals(2, steps.stream().filter(step -> step.sound() != null).count());
        assertSame(TestSounds.SILENT, stepOn(steps, 0, 20).sound());
        assertNull(stepOn(steps, 0, 21).sound());
        assertNull(stepOn(steps, 0, 22).sound());
    }

    private static AnimationKeyframe frame(AnimationAnchor at, MenuItemStack item) {
        return new AnimationKeyframe(at, item, null);
    }

    private static AnimationKeyframe removal(AnimationAnchor at) {
        return new AnimationKeyframe(at, null, null);
    }

    private static CompiledAnimation compile(List<Integer> filledSlots, AnimationKeyframe... frames) {
        return compileOn(20, filledSlots, frames);
    }

    private static CompiledAnimation compileOn(int slot, List<Integer> filledSlots, AnimationKeyframe... frames) {
        return compileItems(filledSlots, new TemporaryItem("test", List.of(slot), List.of(frames)));
    }

    private static CompiledAnimation compileItems(List<Integer> filledSlots, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));
        return AnimationCompiler.compile(SIZE, settings, filledSlots, Map.of());
    }

    private static List<AnimationStep> onSlot(int slot, CompiledAnimation compiled) {
        return compiled.steps().stream().filter(step -> step.slot() == slot).toList();
    }

    private static AnimationStep stepOn(List<AnimationStep> steps, long time, int slot) {
        return steps.stream()
                .filter(step -> step.time() == time && step.slot() == slot)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No step at " + time + "ms on slot " + slot));
    }
}
