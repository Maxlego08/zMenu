package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.animation.slot.AnimationContext;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayMode;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the wobble applied to the time of each step, and for the chance of the whole
 * animation.
 */
class AnimationJitterTest {

    private static final int SIZE = 27;

    @Test
    void withoutJitterTheStepsLandExactly() {
        List<AnimationStep> steps = compile(new Random(1), 0);

        assertIterableEquals(List.of(0L, 50L, 100L, 150L), steps.stream().map(AnimationStep::time).toList());
    }

    @Test
    void jitterMovesEachStepWithinItsBounds() {
        List<AnimationStep> steps = compile(new Random(5), 20);

        for (int index = 0; index < steps.size(); index++) {
            long scheduled = index * 50L;
            long actual = steps.get(index).time();
            assertTrue(Math.abs(actual - scheduled) <= 20,
                    "step " + index + " landed at " + actual + ", scheduled for " + scheduled);
        }
    }

    @Test
    void jitterNeverProducesANegativeTime() {
        for (int seed = 0; seed < 20; seed++) {
            compile(new Random(seed), 200).forEach(step -> assertTrue(step.time() >= 0));
        }
    }

    @Test
    void jitterIsClampedToSomethingSensible() {
        assertEquals(0, new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0, null, false, -30).jitter());
    }

    @Test
    void twoPlaysWithJitterDifferFromEachOther() {
        Set<List<Long>> seen = new HashSet<>();
        Random random = new Random(9);

        for (int play = 0; play < 5; play++) {
            seen.add(compile(random, 25).stream().map(AnimationStep::time).toList());
        }
        assertTrue(seen.size() > 1, "every play produced the same times");
    }

    @Test
    void anAnimationThatLosesItsChanceIsStillCompiled() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0),
                Map.of(), true, AnimationPlayMode.ALWAYS, List.of(), null, 0);

        assertEquals(0, settings.chance());
        assertTrue(AnimationCompiler.compile(SIZE, settings, List.of(0, 1), Map.of()).steps().size() > 0);
    }

    private static List<AnimationStep> compile(Random random, long jitter) {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0, null, false, jitter), true);

        return AnimationCompiler.compile(SIZE, settings, List.of(0, 1, 2, 3), Map.of(), AnimationPlayback.REVEAL,
                new AnimationContext(SIZE, List.of(0, 1, 2, 3), null, null, random)).steps();
    }
}
