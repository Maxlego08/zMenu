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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AnimationPlayback#CONCEAL}, where the buttons are taken away rather than
 * put back. This is what a menu closing itself plays.
 */
class ConcealCompilerTest {

    private static final int SIZE = 27;
    private static final MenuItemStack ITEM = TestMenuItemStacks.named("conceal");

    @Test
    void aConcealEmptiesEverySlotARevealWouldHaveFilled() {
        List<AnimationStep> revealed = compile(AnimationPlayback.REVEAL, List.of(0, 1, 2));
        List<AnimationStep> concealed = compile(AnimationPlayback.CONCEAL, List.of(0, 1, 2));

        assertIterableEquals(List.of(StepAction.REVEAL, StepAction.REVEAL, StepAction.REVEAL),
                revealed.stream().map(AnimationStep::action).toList());
        assertIterableEquals(List.of(StepAction.CLEAR, StepAction.CLEAR, StepAction.CLEAR),
                concealed.stream().map(AnimationStep::action).toList());
    }

    @Test
    void theOrderAndTheTimesAreTheOnesTheAnimationGives() {
        List<AnimationStep> revealed = compile(AnimationPlayback.REVEAL, List.of(0, 1, 2));
        List<AnimationStep> concealed = compile(AnimationPlayback.CONCEAL, List.of(0, 1, 2));

        assertIterableEquals(revealed.stream().map(AnimationStep::slot).toList(),
                concealed.stream().map(AnimationStep::slot).toList());
        assertIterableEquals(revealed.stream().map(AnimationStep::time).toList(),
                concealed.stream().map(AnimationStep::time).toList());
    }

    @Test
    void reverseGivesTheOppositeOrderOfTheOpening() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0, null, true), Map.of(), true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2), Map.of(),
                AnimationPlayback.CONCEAL).steps();

        assertIterableEquals(List.of(2, 1, 0), steps.stream().map(AnimationStep::slot).toList());
    }

    @Test
    void aTemporaryItemLeavesAnEmptySlotBehindIt() {
        List<AnimationStep> steps = compile(AnimationPlayback.CONCEAL, List.of(0, 1, 2),
                temporary(List.of(1), AnimationAnchor.at(0), AnimationAnchor.at(200)));

        AnimationStep removal = steps.stream()
                .filter(step -> step.slot() == 1 && step.time() == 200)
                .findFirst()
                .orElseThrow();
        assertEquals(StepAction.CLEAR, removal.action());
    }

    @Test
    void aConcealOfAnEmptyMenuDoesNothing() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        assertTrue(AnimationCompiler.compile(SIZE, settings, List.of(), Map.of(), AnimationPlayback.CONCEAL).isEmpty());
    }

    @Test
    void aButtonThatDoesNotAnimateInIsStillTakenAway() {
        Map<Integer, ButtonAnimationSettings> slotSettings = Map.of(1, ButtonAnimationSettings.DISABLED);
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), Map.of(), true);

        List<AnimationStep> revealed = AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2), slotSettings,
                AnimationPlayback.REVEAL).steps();
        List<AnimationStep> concealed = AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2), slotSettings,
                AnimationPlayback.CONCEAL).steps();

        assertIterableEquals(List.of(0, 2), revealed.stream().map(AnimationStep::slot).toList());
        assertIterableEquals(List.of(0, 1, 2), concealed.stream().map(AnimationStep::slot).toList());
    }

    @Test
    void aConcealStillObeysTheGroupOfAButtonThatDoesNotAnimateIn() {
        Map<Integer, ButtonAnimationSettings> slotSettings = Map.of(
                0, new ButtonAnimationSettings(false, 2, 0),
                1, new ButtonAnimationSettings(true, 1, 0));
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), Map.of(), true);

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0, 1), slotSettings,
                AnimationPlayback.CONCEAL).steps();

        assertIterableEquals(List.of(1, 0), steps.stream().map(AnimationStep::slot).toList());
    }

    @Test
    void onlyARevealHidesTheButtonsUpFront() {
        assertTrue(AnimationPlayback.REVEAL.hidesButtons());
        assertFalse(AnimationPlayback.CONCEAL.hidesButtons());
        assertFalse(AnimationPlayback.OVERLAY.hidesButtons());
    }

    @Test
    void theNewPlaybackIsParsed() {
        assertEquals(AnimationPlayback.CONCEAL, AnimationPlayback.parse("CONCEAL", AnimationPlayback.REVEAL));
        assertEquals(AnimationPlayback.CONCEAL, AnimationPlayback.parse("conceal", AnimationPlayback.REVEAL));
        assertEquals(AnimationPlayback.REVEAL, AnimationPlayback.parse("nonsense", AnimationPlayback.REVEAL));
    }

    private static TemporaryItem temporary(List<Integer> slots, AnimationAnchor show, AnimationAnchor hide) {
        return new TemporaryItem("test", slots, List.of(
                new AnimationKeyframe(show, ITEM, null),
                new AnimationKeyframe(hide, null, null)));
    }

    private static List<AnimationStep> compile(AnimationPlayback playback, List<Integer> filledSlots, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));

        return AnimationCompiler.compile(SIZE, settings, filledSlots, Map.of(), playback).steps();
    }
}
