package fr.maxlego08.menu.api.animation.slot.settings.idle;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.slot.TestMenuItemStacks;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the animation a button plays on its own slots while the menu stays open.
 */
class ButtonIdleAnimationTest {

    private static final MenuItemStack FIRST = TestMenuItemStacks.named("first");
    private static final MenuItemStack SECOND = TestMenuItemStacks.named("second");

    @Test
    void aLoopingCycleNeverEnds() {
        ButtonIdleAnimation animation = ButtonIdleAnimation.of(List.of(FIRST, SECOND), 200, true);

        assertSame(FIRST, animation.item(0));
        assertSame(SECOND, animation.item(1));
        assertSame(FIRST, animation.item(2));
        assertSame(SECOND, animation.item(101));

        assertFalse(animation.isOver(0));
        assertFalse(animation.isOver(1000));
    }

    @Test
    void aCycleThatDoesNotLoopIsOverAfterItsLastItem() {
        ButtonIdleAnimation animation = ButtonIdleAnimation.of(List.of(FIRST, SECOND), 200, false);

        assertFalse(animation.isOver(0));
        assertFalse(animation.isOver(1));
        assertTrue(animation.isOver(2));
    }

    @Test
    void theIntervalIsClamped() {
        assertEquals(1, ButtonIdleAnimation.of(List.of(FIRST), 0, true).interval());
        assertEquals(1, ButtonIdleAnimation.of(List.of(FIRST), -50, true).interval());
    }

    @Test
    void aRandomCycleIgnoresTheTurnAndPicks() {
        ButtonIdleAnimation animation = ButtonIdleAnimation.of(List.of(FIRST, SECOND), 200, true, true);
        Random random = new Random(4);

        Set<MenuItemStack> seen = new HashSet<>();
        for (int turn = 0; turn < 50; turn++) {
            seen.add(animation.item(0, random));
        }
        assertEquals(2, seen.size());
    }

    @Test
    void aCycleThatIsNotRandomStillFollowsItsOrder() {
        ButtonIdleAnimation animation = ButtonIdleAnimation.of(List.of(FIRST, SECOND), 200, true, false);
        Random random = new Random(4);

        assertSame(FIRST, animation.item(0, random));
        assertSame(SECOND, animation.item(1, random));
        assertSame(FIRST, animation.item(2, random));
    }

    @Test
    void aFrameWithoutADurationFallsBackToTheInterval() {
        IdleFrame held = new IdleFrame(FIRST, 1000, null);
        IdleFrame plain = new IdleFrame(SECOND);

        assertEquals(1000, held.duration(200));
        assertEquals(200, plain.duration(200));
    }

    @Test
    void aNegativeDurationIsTreatedAsNone() {
        assertEquals(0, new IdleFrame(FIRST, -50, null).duration());
        assertEquals(200, new IdleFrame(FIRST, -50, null).duration(200));
    }

    @Test
    void framesKeepTheirOwnDurationAndSound() {
        IdleFrame held = new IdleFrame(FIRST, 1000, null);
        IdleFrame flash = new IdleFrame(SECOND, 50, null);
        ButtonIdleAnimation animation = new ButtonIdleAnimation(List.of(held, flash), 200, true);

        assertSame(held, animation.frame(0));
        assertSame(flash, animation.frame(1));
        assertSame(held, animation.frame(2));
        assertSame(FIRST, animation.item(0));
        assertIterableEquals(List.of(FIRST, SECOND), animation.items());
    }

    @Test
    void aSingleItemCycleAlwaysShowsIt() {
        ButtonIdleAnimation animation = ButtonIdleAnimation.of(List.of(FIRST), 200, true);

        assertSame(FIRST, animation.item(0));
        assertSame(FIRST, animation.item(7));
    }
}
