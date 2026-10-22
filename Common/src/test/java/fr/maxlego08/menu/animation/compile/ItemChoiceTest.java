package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.AnimationContext;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.ItemChoice;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for picking one item out of several, and for rolling once per slot.
 */
class ItemChoiceTest {

    private static final int SIZE = 27;
    private static final MenuItemStack REDSTONE = TestMenuItemStacks.named("redstone");
    private static final MenuItemStack EMERALD = TestMenuItemStacks.named("emerald");

    @Test
    void aWeightIsAtLeastOne() {
        assertEquals(1, new ItemChoice(REDSTONE, 0).weight());
        assertEquals(1, new ItemChoice(REDSTONE, -3).weight());
        assertEquals(1, new ItemChoice(REDSTONE).weight());
    }

    @Test
    void pickingFromNothingGivesNothing() {
        assertNull(context(new Random(1)).pick(List.of()));
    }

    @Test
    void aSingleChoiceIsAlwaysPicked() {
        AnimationContext context = context(new Random(1));

        for (int attempt = 0; attempt < 20; attempt++) {
            assertSame(REDSTONE, context.pick(List.of(new ItemChoice(REDSTONE))));
        }
    }

    @Test
    void weightsDecideHowOftenEachOneIsPicked() {
        AnimationContext context = context(new Random(42));
        List<ItemChoice> choices = List.of(new ItemChoice(REDSTONE, 1), new ItemChoice(EMERALD, 3));

        int emeralds = 0;
        for (int attempt = 0; attempt < 10_000; attempt++) {
            if (context.pick(choices) == EMERALD) emeralds++;
        }
        assertTrue(emeralds > 7200 && emeralds < 7800, "picked the emerald " + emeralds + " times out of 10000");
    }

    @Test
    void aKeyframeWithChoicesDisplaysOneOfThem() {
        List<AnimationStep> steps = compile(new Random(3), item(100, false));

        MenuItemStack shown = steps.stream()
                .filter(step -> step.action() == StepAction.SET)
                .findFirst().orElseThrow().item();

        assertTrue(shown == REDSTONE || shown == EMERALD);
    }

    @Test
    void oneRollForTheItemMeansEverySlotShowsTheSameThing() {
        List<AnimationStep> steps = compile(new Random(3), item(100, false));

        Set<MenuItemStack> shown = new HashSet<>();
        steps.stream().filter(step -> step.action() == StepAction.SET).forEach(step -> shown.add(step.item()));

        assertEquals(1, shown.size());
    }

    @Test
    void aRollPerSlotLetsTheSlotsDifferFromEachOther() {
        List<AnimationStep> steps = compile(new Random(3), item(100, true));

        Set<MenuItemStack> shown = new HashSet<>();
        steps.stream().filter(step -> step.action() == StepAction.SET).forEach(step -> shown.add(step.item()));

        assertEquals(2, shown.size());
    }

    @Test
    void aRollPerSlotScattersWhichSlotsAppearAtAll() {
        List<AnimationStep> steps = compile(new Random(3), item(50, true));

        long slots = steps.stream().filter(step -> step.action() == StepAction.SET).count();
        assertTrue(slots > 3 && slots < 18, "showed on " + slots + " slots out of 20");
    }

    private static TemporaryItem item(float chance, boolean perSlot) {
        List<ItemChoice> choices = List.of(new ItemChoice(REDSTONE), new ItemChoice(EMERALD));

        return new TemporaryItem("test", slots(), List.of(
                new AnimationKeyframe(AnimationAnchor.at(0), null, null, 100, choices),
                new AnimationKeyframe(AnimationAnchor.at(100), null, null)), chance, perSlot);
    }

    private static List<Integer> slots() {
        return java.util.stream.IntStream.rangeClosed(1, 20).boxed().toList();
    }

    private static AnimationContext context(Random random) {
        return new AnimationContext(SIZE, List.of(), null, null, random);
    }

    private static List<AnimationStep> compile(Random random, TemporaryItem... items) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(items));

        return AnimationCompiler.compile(SIZE, settings, List.of(0), Map.of(), AnimationPlayback.REVEAL,
                new AnimationContext(SIZE, List.of(0), null, null, random)).steps();
    }
}
