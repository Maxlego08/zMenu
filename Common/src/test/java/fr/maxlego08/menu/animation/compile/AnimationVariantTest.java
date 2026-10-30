package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.animation.slot.AnimationContext;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationAnchor;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationVariant;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.animation.slot.settings.item.AnimationKeyframe;
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for picking one animation out of several, and for picking a number of slots at random.
 */
class AnimationVariantTest {

    private static final int SIZE = 27;

    @Test
    void aWeightIsAtLeastOne() {
        assertEquals(1, variant("a", 0).weight());
        assertEquals(1, variant("a", -2).weight());
    }

    @Test
    void pickingFromNothingGivesNothing() {
        assertNull(context(new Random(1)).pickVariant(List.of()));
    }

    @Test
    void aSingleVariantIsAlwaysPicked() {
        AnimationContext context = context(new Random(1));
        AnimationVariant only = variant("only", 1);

        for (int attempt = 0; attempt < 20; attempt++) {
            assertSame(only, context.pickVariant(List.of(only)));
        }
    }

    @Test
    void weightsDecideHowOftenEachVariantIsPicked() {
        AnimationContext context = context(new Random(42));
        List<AnimationVariant> variants = List.of(variant("rare", 1), variant("common", 3));

        int common = 0;
        for (int attempt = 0; attempt < 10_000; attempt++) {
            if (context.pickVariant(variants).name().equals("common")) common++;
        }
        assertTrue(common > 7200 && common < 7800, "picked the common one " + common + " times out of 10000");
    }

    @Test
    void aCountPicksExactlyThatManySlots() {
        AnimationContext context = context(new Random(3));
        List<Integer> slots = List.of(10, 11, 12, 13, 14, 15);

        for (int attempt = 0; attempt < 20; attempt++) {
            assertEquals(3, context.pickSome(slots, 3).size());
        }
    }

    @Test
    void aCountKeepsTheOrderTheSlotsWereWrittenIn() {
        AnimationContext context = context(new Random(3));
        List<Integer> slots = List.of(10, 11, 12, 13, 14, 15);

        List<Integer> picked = context.pickSome(slots, 4);
        assertIterableEquals(picked.stream().sorted().toList(), picked);
    }

    @Test
    void aCountOfZeroOrMoreThanThereAreTakesThemAll() {
        AnimationContext context = context(new Random(3));
        List<Integer> slots = List.of(10, 11, 12);

        assertIterableEquals(slots, context.pickSome(slots, 0));
        assertIterableEquals(slots, context.pickSome(slots, 3));
        assertIterableEquals(slots, context.pickSome(slots, 99));
    }

    @Test
    void aCountLandsOnDifferentSlotsFromOneTimeToTheNext() {
        AnimationContext context = context(new Random(3));
        List<Integer> slots = List.of(10, 11, 12, 13, 14, 15, 16, 17);

        Set<List<Integer>> seen = new HashSet<>();
        for (int attempt = 0; attempt < 10; attempt++) {
            seen.add(context.pickSome(slots, 2));
        }
        assertTrue(seen.size() > 1, "every draw picked the same slots");
    }

    private static AnimationVariant variant(String name, int weight) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        return new AnimationVariant(name, settings, weight);
    }

    private static AnimationContext context(Random random) {
        return new AnimationContext(SIZE, List.of(), null, null, random);
    }

    @Test
    void settingsHaveNoVariantsByDefault() {
        assertTrue(new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true).variants().isEmpty());
    }

    @Test
    void aCountLimitsHowManySlotsAnItemUses() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS,
                List.of(new TemporaryItem("test", List.of(10, 11, 12, 13, 14, 15),
                        List.of(new AnimationKeyframe(AnimationAnchor.at(0), TestMenuItemStacks.named("x"), null),
                                new AnimationKeyframe(AnimationAnchor.at(100), null, null)), 100, false, 2)));

        List<AnimationStep> steps = AnimationCompiler.compile(SIZE, settings, List.of(0), Map.of(),
                AnimationPlayback.REVEAL, context(new Random(4))).steps();

        assertEquals(2, steps.stream().filter(step -> step.action() == StepAction.SET).count());
    }
}
