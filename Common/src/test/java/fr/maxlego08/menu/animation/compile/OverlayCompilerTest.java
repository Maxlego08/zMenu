package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.ButtonAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AnimationPlayback#OVERLAY}, where the buttons are never hidden and only the
 * temporary items play.
 */
class OverlayCompilerTest {

    private static final int SIZE = 27;
    private static final MenuItemStack ITEM = TestMenuItemStacks.named("overlay");

    @Test
    void anOverlayNeverHidesAButton() {
        List<AnimationStep> steps = overlay(List.of(0, 1, 2),
                temporary(List.of(20), AnimationAnchor.at(0), AnimationAnchor.at(100)));

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 0 || step.slot() == 1 || step.slot() == 2));
        assertIterableEquals(List.of(0L, 100L), steps.stream().map(AnimationStep::time).toList());
    }

    @Test
    void removingAnOverlayPutsBackWhateverWasUnderIt() {
        List<AnimationStep> revealed = compile(AnimationPlayback.REVEAL, List.of(0, 1, 2),
                temporary(List.of(1), AnimationAnchor.at(0), AnimationAnchor.at(20)));
        assertEquals(StepAction.CLEAR, at(revealed, 20).action());

        List<AnimationStep> overlaid = compile(AnimationPlayback.OVERLAY, List.of(0, 1, 2),
                temporary(List.of(1), AnimationAnchor.at(0), AnimationAnchor.at(20)));
        assertEquals(StepAction.REVEAL, at(overlaid, 20).action());
    }

    @Test
    void groupAnchorsKeepTheirMeaningInAnOverlay() {
        List<AnimationStep> steps = overlay(List.of(0, 1),
                temporary(List.of(20), AnimationAnchor.groupEnd(1), AnimationAnchor.at(500)));

        assertEquals(100, steps.getFirst().time());
    }

    @Test
    void anOverlayWithoutTemporaryItemsPlaysNothing() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        CompiledAnimation compiled = AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2), Map.of(), AnimationPlayback.OVERLAY);
        assertTrue(compiled.isEmpty());
    }

    @Test
    void anOverlayWorksOnAnInventoryWithNoButtonAtAll() {
        List<AnimationStep> steps = overlay(List.of(),
                temporary(List.of(20), AnimationAnchor.at(0), AnimationAnchor.at(100)));

        assertIterableEquals(List.of(StepAction.SET, StepAction.REVEAL), steps.stream().map(AnimationStep::action).toList());
    }

    private static TemporaryItem temporary(List<Integer> slots, AnimationAnchor show, AnimationAnchor hide) {
        return new TemporaryItem("test", slots, List.of(
                new AnimationKeyframe(show, ITEM, null),
                new AnimationKeyframe(hide, null, null)));
    }

    private static List<AnimationStep> overlay(List<Integer> filledSlots, TemporaryItem... items) {
        return compile(AnimationPlayback.OVERLAY, filledSlots, items);
    }

    private static List<AnimationStep> compile(AnimationPlayback playback, List<Integer> filledSlots, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));

        Map<Integer, ButtonAnimationSettings> slotSettings = new java.util.HashMap<>();
        for (int slot : filledSlots) {
            slotSettings.put(slot, ButtonAnimationSettings.DEFAULT);
        }
        return AnimationCompiler.compile(SIZE, settings, filledSlots, slotSettings, playback).steps();
    }

    private static AnimationStep at(List<AnimationStep> steps, long time) {
        return steps.stream()
                .filter(step -> step.time() == time)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No step at " + time + "ms in " + steps));
    }
}
