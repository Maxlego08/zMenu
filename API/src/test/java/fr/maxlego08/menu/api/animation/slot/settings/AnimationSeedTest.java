package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Unit tests for where the randomness of an animation comes from.
 */
class AnimationSeedTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final UUID OTHER = UUID.fromString("00000000-0000-0000-0000-00000000000b");

    @Test
    void theFormsAreParsed() {
        assertEquals(AnimationSeed.RANDOM, AnimationSeed.parse(null));
        assertEquals(AnimationSeed.RANDOM, AnimationSeed.parse("random"));
        assertEquals(AnimationSeed.RANDOM, AnimationSeed.parse("  "));
        assertEquals(AnimationSeed.Type.PLAYER, AnimationSeed.parse("PLAYER").type());
        assertEquals(new AnimationSeed(AnimationSeed.Type.FIXED, 42), AnimationSeed.parse("42"));
    }

    @Test
    void anythingElseRollsAfresh() {
        assertEquals(AnimationSeed.RANDOM, AnimationSeed.parse("sometimes"));
    }

    @Test
    void aPlayerAlwaysGetsTheSameAnimation() {
        AnimationSeed seed = AnimationSeed.parse("player");

        assertEquals(draw(seed.create(PLAYER, "pulse")), draw(seed.create(PLAYER, "pulse")));
    }

    @Test
    void twoPlayersGetDifferentOnes() {
        AnimationSeed seed = AnimationSeed.parse("player");

        assertNotEquals(draw(seed.create(PLAYER, "pulse")), draw(seed.create(OTHER, "pulse")));
    }

    @Test
    void twoAnimationsDoNotEndUpWithTheSamePattern() {
        AnimationSeed seed = AnimationSeed.parse("player");

        assertNotEquals(draw(seed.create(PLAYER, "pulse")), draw(seed.create(PLAYER, "sparkle")));
    }

    @Test
    void aFixedSeedIsTheSameForEveryone() {
        AnimationSeed seed = AnimationSeed.parse("7");

        assertEquals(draw(seed.create(PLAYER, "pulse")), draw(seed.create(OTHER, "pulse")));
    }

    @Test
    void aPlayerSeedFallsBackWhenThereIsNoPlayer() {
        AnimationSeed seed = AnimationSeed.parse("player");
        assertEquals(10, draw(seed.create(null, "pulse")).size());
    }

    @Test
    void anAnimationRollsAfreshByDefault() {
        SlotAnimationSettings settings = new SlotAnimationSettings(
                new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0), true);

        assertEquals(AnimationSeed.RANDOM, settings.seed());
    }

    private static List<Integer> draw(Random random) {
        return java.util.stream.IntStream.range(0, 10).map(index -> random.nextInt(1000)).boxed().toList();
    }
}
