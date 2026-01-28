package fr.maxlego08.menu.api.animation;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Immutable settings object for title animations.
 * Encapsulates timing, title list, cycles, and behavior flags for animating inventory titles.
 *
 * @param titles                 List of title strings shown in the animation
 * @param cycles                 Number of times animation will repeat; -1 for infinite
 * @param initialDelay           Initial delay before animation starts, in timeUnit
 * @param interval               Interval between each animation frame, in timeUnit
 * @param timeUnit               Time unit used for delay and interval
 * @param showItemsAfterAnimation Whether to show items after animation completes
 * @param itemUpdateInterval     Interval for updating displayed items (if any)
 */
public record TitleAnimationSettings(
    List<@NotNull String> titles,
    int cycles,
    int initialDelay,
    int interval,
    @NotNull TimeUnit timeUnit,
    boolean showItemsAfterAnimation,
    int itemUpdateInterval
) {
}
