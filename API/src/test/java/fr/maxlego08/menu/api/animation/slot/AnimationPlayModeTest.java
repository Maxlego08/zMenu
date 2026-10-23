package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.animation.slot.order.SequentialSlotAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.AnimationSeed;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationGroup;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the parsing of the {@code play} option, for its default, and for deciding whether
 * an animation may play from what the player has already seen.
 */
class AnimationPlayModeTest {

    private static final long HOUR = 3_600_000L;
    private static final long NOW = 1_000_000_000L;

    @Test
    void modesAreParsedIgnoringCaseAndSeparators() {
        assertEquals(AnimationPlayMode.ALWAYS, AnimationPlayMode.parse("ALWAYS", AnimationPlayMode.ONCE));
        assertEquals(AnimationPlayMode.ONCE, AnimationPlayMode.parse("once", AnimationPlayMode.ALWAYS));
        assertEquals(AnimationPlayMode.ONCE, AnimationPlayMode.parse("First-Open", AnimationPlayMode.ALWAYS));
        assertEquals(AnimationPlayMode.ONCE, AnimationPlayMode.parse("FIRST_OPEN", AnimationPlayMode.ALWAYS));
    }

    @Test
    void anUnknownOrMissingModeFallsBackToTheDefault() {
        assertEquals(AnimationPlayMode.ALWAYS, AnimationPlayMode.parse(null, AnimationPlayMode.ALWAYS));
        assertEquals(AnimationPlayMode.ALWAYS, AnimationPlayMode.parse("sometimes", AnimationPlayMode.ALWAYS));
    }

    @Test
    void settingsPlayOnEveryOpenByDefault() {
        SlotAnimationGroup defaults = new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0);

        assertEquals(AnimationPlayMode.ALWAYS, new SlotAnimationSettings(defaults, true).play());
        assertEquals(AnimationPlayMode.ALWAYS, new SlotAnimationSettings(defaults, java.util.Map.of(), true).play());
    }

    @Test
    void theModeThatSurvivesAReconnectIsParsedToo() {
        assertEquals(AnimationPlayMode.ONCE_FOREVER, AnimationPlayMode.parse("ONCE_FOREVER", AnimationPlayMode.ALWAYS));
        assertEquals(AnimationPlayMode.ONCE_FOREVER, AnimationPlayMode.parse("once-forever", AnimationPlayMode.ALWAYS));
    }

    @Test
    void anAnimationNeverSeenAlwaysPlays() {
        for (AnimationPlayMode mode : AnimationPlayMode.values()) {
            assertTrue(mode.canPlay(null, NOW, 0), mode + " should play the first time");
            assertTrue(mode.canPlay(null, NOW, HOUR), mode + " should play the first time even under a cooldown");
        }
    }

    @Test
    void aOnceModeNeverPlaysTwice() {
        assertFalse(AnimationPlayMode.ONCE.canPlay(NOW - 10 * HOUR, NOW, 0));
        assertFalse(AnimationPlayMode.ONCE_FOREVER.canPlay(NOW - 10 * HOUR, NOW, 0));
        assertFalse(AnimationPlayMode.ONCE_FOREVER.canPlay(NOW - 10 * HOUR, NOW, HOUR));
    }

    @Test
    void alwaysReplaysUnlessItIsStillCoolingDown() {
        AnimationPlayMode always = AnimationPlayMode.ALWAYS;

        assertTrue(always.canPlay(NOW - 10 * HOUR, NOW, 0), "no cooldown means no restriction");
        assertFalse(always.canPlay(NOW - HOUR / 2, NOW, HOUR), "half an hour into an hour long cooldown");
        assertTrue(always.canPlay(NOW - HOUR, NOW, HOUR), "the cooldown is over at exactly its length");
        assertTrue(always.canPlay(NOW - 2 * HOUR, NOW, HOUR));
    }

    @Test
    void theModesKnowWhatTheyAre() {
        assertTrue(AnimationPlayMode.ONCE_FOREVER.isPersistent());
        assertFalse(AnimationPlayMode.ONCE.isPersistent());
        assertFalse(AnimationPlayMode.ALWAYS.isPersistent());

        assertTrue(AnimationPlayMode.ONCE.isOnce());
        assertTrue(AnimationPlayMode.ONCE_FOREVER.isOnce());
        assertFalse(AnimationPlayMode.ALWAYS.isOnce());
    }

    @Test
    void settingsKnowWhenTheyNeedTheDatabase() {
        SlotAnimationGroup defaults = new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0);

        assertFalse(settings(defaults, AnimationPlayMode.ALWAYS, 0).needsPersistence());
        assertFalse(settings(defaults, AnimationPlayMode.ONCE, 0).needsPersistence());
        assertTrue(settings(defaults, AnimationPlayMode.ONCE_FOREVER, 0).needsPersistence());
        assertTrue(settings(defaults, AnimationPlayMode.ALWAYS, HOUR).needsPersistence());
    }

    @Test
    void aNegativeCooldownIsNoCooldown() {
        SlotAnimationGroup defaults = new SlotAnimationGroup(new SequentialSlotAnimation(), 50, 1, 0);

        assertEquals(0, settings(defaults, AnimationPlayMode.ALWAYS, -5).cooldown());
    }

    private static SlotAnimationSettings settings(SlotAnimationGroup defaults, AnimationPlayMode play, long cooldown) {
        return new SlotAnimationSettings(defaults, java.util.Map.of(), true, play, java.util.List.of(), null, 100,
                java.util.List.of(), AnimationSeed.RANDOM, java.util.List.of(), java.util.List.of(), cooldown);
    }
}
