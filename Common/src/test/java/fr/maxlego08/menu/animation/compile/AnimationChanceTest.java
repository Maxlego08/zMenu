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
import fr.maxlego08.menu.api.animation.slot.settings.item.TemporaryItem;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for everything an animation leaves to chance. The context carries the source of
 * randomness, so a seeded one makes each of these deterministic.
 */
class AnimationChanceTest {

    private static final int SIZE = 27;
    private static final MenuItemStack ITEM = TestMenuItemStacks.named("maybe");

    @Test
    void theBoundsAreAbsolute() {
        AnimationContext context = context(new Random(1));

        for (int attempt = 0; attempt < 100; attempt++) {
            assertTrue(context.roll(100));
            assertFalse(context.roll(0));
        }
    }

    @Test
    void aRollFollowsItsChance() {
        AnimationContext context = context(new Random(42));

        int passed = 0;
        for (int attempt = 0; attempt < 10_000; attempt++) {
            if (context.roll(30)) passed++;
        }
        assertTrue(passed > 2700 && passed < 3300, "passed " + passed + " times out of 10000");
    }

    @Test
    void anItemThatLosesItsRollContributesNothing() {
        List<AnimationStep> steps = compile(new Random(1), item(0));

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 20));
    }

    @Test
    void anItemThatWinsItsRollBehavesNormally() {
        List<AnimationStep> steps = compile(new Random(1), item(100));

        assertEquals(2, steps.stream().filter(step -> step.slot() == 20).count());
    }

    @Test
    void aKeyframeThatLosesItsRollIsSkipped() {
        TemporaryItem temporaryItem = new TemporaryItem("test", List.of(20), List.of(
                new AnimationKeyframe(AnimationAnchor.at(0), ITEM, null),
                new AnimationKeyframe(AnimationAnchor.at(100), ITEM, null, 0),
                new AnimationKeyframe(AnimationAnchor.at(200), null, null)));

        List<AnimationStep> steps = compile(new Random(1), temporaryItem);

        assertEquals(List.of(0L, 200L), steps.stream().filter(step -> step.slot() == 20).map(AnimationStep::time).toList());
    }

    @Test
    void aRemovalAlwaysHappensWhateverItsChance() {
        TemporaryItem temporaryItem = new TemporaryItem("test", List.of(20), List.of(
                new AnimationKeyframe(AnimationAnchor.at(0), ITEM, null),
                new AnimationKeyframe(AnimationAnchor.at(100), null, null, 0)));

        List<AnimationStep> steps = compile(new Random(1), temporaryItem);

        assertEquals(StepAction.CLEAR, steps.stream()
                .filter(step -> step.slot() == 20 && step.time() == 100)
                .findFirst().orElseThrow().action());
    }

    @Test
    void anItemWhoseEveryFrameIsSkippedContributesNothing() {
        TemporaryItem temporaryItem = new TemporaryItem("test", List.of(20), List.of(
                new AnimationKeyframe(AnimationAnchor.at(0), ITEM, null, 0),
                new AnimationKeyframe(AnimationAnchor.at(100), ITEM, null, 0)));

        List<AnimationStep> steps = compile(new Random(1), temporaryItem);

        assertTrue(steps.stream().noneMatch(step -> step.slot() == 20));
    }

    @Test
    void theSameSeedGivesTheSameAnimation() {
        List<AnimationStep> first = compile(new Random(7), item(50), other(50));
        List<AnimationStep> second = compile(new Random(7), item(50), other(50));

        assertEquals(first, second);
    }

    private static TemporaryItem item(float chance) {
        return new TemporaryItem("test", List.of(20), frames(), chance);
    }

    private static TemporaryItem other(float chance) {
        return new TemporaryItem("other", List.of(21), frames(), chance);
    }

    private static List<AnimationKeyframe> frames() {
        return List.of(
                new AnimationKeyframe(AnimationAnchor.at(0), ITEM, null),
                new AnimationKeyframe(AnimationAnchor.at(100), null, null));
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
