package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the second compiler pass, which places the items that only exist while the
 * animation plays.
 */
class TemporaryItemCompilerTest {

    private static final int SIZE = 27;
    private static final MenuItemStack ITEM = TestMenuItemStacks.named("redstone");

    @Test
    void aTemporaryItemAppearsAndIsRemovedAtItsAnchors() {
        List<AnimationStep> steps = compile(List.of(0, 1, 2),
                temporary(List.of(20), AnimationAnchor.at(20), AnimationAnchor.at(80)));

        assertEquals(new AnimationStep(20, 20, StepAction.SET, ITEM, null), stepAt(steps, 20, 20));
        assertEquals(StepAction.CLEAR, stepAt(steps, 80, 20).action());
    }

    @Test
    void theCacheSettingOfTheItemReachesItsStep() {
        List<AnimationStep> cached = compile(List.of(0), temporary(List.of(20), AnimationAnchor.at(20), AnimationAnchor.at(80), true));
        List<AnimationStep> uncached = compile(List.of(0), temporary(List.of(20), AnimationAnchor.at(20), AnimationAnchor.at(80), false));

        assertTrue(stepAt(cached, 20, 20).useCache());
        assertFalse(stepAt(uncached, 20, 20).useCache());
    }

    @Test
    void theItemOnTopDecidesTheCacheSetting() {
        TemporaryItem cachedItem = temporary(List.of(20), AnimationAnchor.at(20), AnimationAnchor.at(80), true);
        MenuItemStack other = TestMenuItemStacks.named("emerald");
        TemporaryItem uncachedItem = new TemporaryItem("top", List.of(20), List.of(
                new AnimationKeyframe(AnimationAnchor.at(40), other, null),
                new AnimationKeyframe(AnimationAnchor.at(60), null, null)), 100, false, 0, false);

        List<AnimationStep> steps = compile(List.of(0), cachedItem, uncachedItem);

        assertTrue(stepAt(steps, 20, 20).useCache());
        assertFalse(stepAt(steps, 40, 20).useCache());
    }

    @Test
    void theItemInstanceIsCarriedThrough() {
        List<AnimationStep> steps = compile(List.of(0), temporary(List.of(20), AnimationAnchor.START, AnimationAnchor.END));

        assertSame(ITEM, stepAt(steps, 0, 20).item());
    }

    @Test
    void aButtonRevealedBehindTheItemIsRevealedWhenTheItemGoesAway() {
        List<AnimationStep> steps = compile(List.of(0, 1, 2),
                temporary(List.of(1), AnimationAnchor.at(0), AnimationAnchor.at(200)));

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 1 && step.time() == 50));
        assertEquals(StepAction.SET, stepAt(steps, 0, 1).action());
        assertEquals(StepAction.REVEAL, stepAt(steps, 200, 1).action());
    }

    @Test
    void aButtonAlreadyVisibleIsPutBackWhenTheItemGoesAway() {
        List<AnimationStep> steps = compile(List.of(0, 1, 2),
                temporary(List.of(0), AnimationAnchor.at(100), AnimationAnchor.at(200)));

        assertEquals(StepAction.REVEAL, stepAt(steps, 0, 0).action());
        assertEquals(StepAction.SET, stepAt(steps, 100, 0).action());
        assertEquals(StepAction.REVEAL, stepAt(steps, 200, 0).action());
    }

    @Test
    void aButtonRevealedAfterTheItemGoesAwayKeepsItsOwnReveal() {
        List<AnimationStep> steps = compile(List.of(0, 1, 2),
                temporary(List.of(2), AnimationAnchor.at(0), AnimationAnchor.at(60)));

        assertEquals(StepAction.CLEAR, stepAt(steps, 60, 2).action());
        assertEquals(StepAction.REVEAL, stepAt(steps, 100, 2).action());
    }

    @Test
    void byDefaultAnItemLastsForTheWholeAnimation() {
        CompiledAnimation compiled = compileAll(List.of(0, 1, 2),
                temporary(List.of(20), AnimationAnchor.START, AnimationAnchor.END));

        assertEquals(StepAction.SET, stepAt(compiled.steps(), 0, 20).action());
        assertEquals(StepAction.CLEAR, stepAt(compiled.steps(), compiled.timing().end(), 20).action());
    }

    @Test
    void slotsOutsideTheInventoryAreIgnored() {
        List<AnimationStep> steps = compile(List.of(0),
                temporary(List.of(-1, 5, 900), AnimationAnchor.START, AnimationAnchor.at(10)));

        assertIterableEquals(List.of(5, 5), steps.stream().filter(step -> step.action() != StepAction.REVEAL).map(AnimationStep::slot).toList());
    }

    @Test
    void anItemShownAndRemovedAtTheSameInstantIsNotDisplayed() {
        List<AnimationStep> steps = compile(List.of(0),
                temporary(List.of(20), AnimationAnchor.at(30), AnimationAnchor.at(30)));

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 20));
    }

    /**
     * The show and hide pair of the configuration is loaded as the two keyframes it stands for.
     */
    @Test
    void aButtonRefusingTemporaryItemsIsNeverCovered() {
        List<AnimationStep> steps = compileWith(List.of(0, 1, 2),
                Map.of(1, new ButtonAnimationSettings(true, 1, 0, null, false)),
                temporary(List.of(0, 1, 2), AnimationAnchor.at(0), AnimationAnchor.at(200)));

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 1 && step.action() == StepAction.SET));
        assertEquals(StepAction.SET, stepAt(steps, 0, 0).action());
        assertEquals(StepAction.SET, stepAt(steps, 0, 2).action());
    }

    @Test
    void aRefusedButtonKeepsItsOwnReveal() {
        List<AnimationStep> steps = compileWith(List.of(0, 1, 2),
                Map.of(1, new ButtonAnimationSettings(true, 1, 0, null, false)),
                temporary(List.of(1), AnimationAnchor.at(0), AnimationAnchor.at(200)));

        assertEquals(StepAction.REVEAL, stepAt(steps, 50, 1).action());
        assertTrue(steps.stream().noneMatch(step -> step.time() == 200));
    }

    @Test
    void anItemLandingOnlyOnRefusedSlotsDoesNothing() {
        List<AnimationStep> steps = compileWith(List.of(0, 1),
                Map.of(1, new ButtonAnimationSettings(true, 1, 0, null, false)),
                temporary(List.of(1), AnimationAnchor.START, AnimationAnchor.END));

        assertEquals(2, steps.size());
        assertTrue(steps.stream().allMatch(step -> step.action() == StepAction.REVEAL));
    }

    @Test
    void twoItemsOnTheSameSlotAreResolvedByDeclarationOrder() {
        MenuItemStack second = TestMenuItemStacks.named("second");

        List<AnimationStep> steps = compileWith(List.of(), Map.of(),
                temporary(List.of(20), AnimationAnchor.at(0), AnimationAnchor.at(300)),
                new TemporaryItem("above", List.of(20), List.of(
                        new AnimationKeyframe(AnimationAnchor.at(50), second, null),
                        new AnimationKeyframe(AnimationAnchor.at(150), null, null))));

        assertIterableEquals(List.of(0L, 50L, 150L, 300L), steps.stream().map(AnimationStep::time).toList());
        assertSame(ITEM, steps.get(0).item());
        assertSame(second, steps.get(1).item());
        assertSame(ITEM, steps.get(2).item());
        assertEquals(StepAction.CLEAR, steps.get(3).action());
    }

    @Test
    void anItemRemovedUnderAnotherOneChangesNothingOnScreen() {
        MenuItemStack second = TestMenuItemStacks.named("second");

        List<AnimationStep> steps = compileWith(List.of(), Map.of(),
                temporary(List.of(20), AnimationAnchor.at(0), AnimationAnchor.at(100)),
                new TemporaryItem("above", List.of(20), List.of(
                        new AnimationKeyframe(AnimationAnchor.at(50), second, null),
                        new AnimationKeyframe(AnimationAnchor.at(200), null, null))));

        assertIterableEquals(List.of(0L, 50L, 200L), steps.stream().map(AnimationStep::time).toList());
    }

    private static List<AnimationStep> compileWith(List<Integer> filledSlots, Map<Integer, ButtonAnimationSettings> slotSettings, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));
        return AnimationCompiler.compile(SIZE, settings, filledSlots, slotSettings).steps();
    }

    private static TemporaryItem temporary(List<Integer> slots, AnimationAnchor show, AnimationAnchor hide) {
        return temporary(slots, show, hide, true);
    }

    private static TemporaryItem temporary(List<Integer> slots, AnimationAnchor show, AnimationAnchor hide, boolean useCache) {
        return new TemporaryItem("test", slots, List.of(
                new AnimationKeyframe(show, ITEM, null),
                new AnimationKeyframe(hide, null, null)), 100, false, 0, useCache);
    }

    private static List<AnimationStep> compile(List<Integer> filledSlots, TemporaryItem... items) {
        return compileAll(filledSlots, items).steps();
    }

    private static CompiledAnimation compileAll(List<Integer> filledSlots, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));
        return AnimationCompiler.compile(SIZE, settings, filledSlots, Map.of());
    }

    private static AnimationStep stepAt(List<AnimationStep> steps, long time, int slot) {
        return steps.stream()
                .filter(step -> step.time() == time && step.slot() == slot)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No step at " + time + "ms on slot " + slot + " in " + steps));
    }
}
