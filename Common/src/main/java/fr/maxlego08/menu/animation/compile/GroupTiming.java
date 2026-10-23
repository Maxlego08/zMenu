package fr.maxlego08.menu.animation.compile;

/**
 * When a group of the animation starts and ends.
 *
 * @param start the time at which the first slot of the group is revealed, in milliseconds
 * @param end   the time at which the group is over, in milliseconds
 */
public record GroupTiming(long start, long end) {
}
