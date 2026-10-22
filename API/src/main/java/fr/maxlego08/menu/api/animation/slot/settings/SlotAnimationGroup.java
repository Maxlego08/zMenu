package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * How one group of slots is revealed.
 *
 * <p>Ordering is a property of the group, not of a single button: a group is revealed as a whole, so
 * an animation such as a spiral can span several buttons. A button only chooses the group it belongs
 * to, and may add a delay of its own.</p>
 *
 * @param animation    the order in which the slots of the group are revealed
 * @param interval     the delay between two steps, in milliseconds
 * @param slotsPerStep the number of slots revealed on each step
 * @param delay        the delay before the group starts, in milliseconds, counted from the end of
 *                     the previous group
 * @param sound        the sound played when a slot of the group is revealed, or null for a silent
 *                     group
 * @param reverse      whether the order produced by the animation is played backwards
 * @param jitter       how far a step may land from its scheduled time, in milliseconds, so a reveal
 *                     reads as organic rather than mechanical; 0 keeps it exact
 */
public record SlotAnimationGroup(@NotNull SlotAnimation animation, long interval, int slotsPerStep, long delay, @Nullable SoundOption sound, boolean reverse, long jitter) {

    public SlotAnimationGroup(@NotNull SlotAnimation animation, long interval, int slotsPerStep, long delay,
                              @Nullable SoundOption sound, boolean reverse) {
        this(animation, interval, slotsPerStep, delay, sound, reverse, 0);
    }

    public SlotAnimationGroup(@NotNull SlotAnimation animation, long interval, int slotsPerStep, long delay, @Nullable SoundOption sound) {
        this(animation, interval, slotsPerStep, delay, sound, false, 0);
    }

    public SlotAnimationGroup(@NotNull SlotAnimation animation, long interval, int slotsPerStep, long delay) {
        this(animation, interval, slotsPerStep, delay, null, false, 0);
    }

    public SlotAnimationGroup {
        interval = Math.max(1, interval);
        slotsPerStep = Math.max(1, slotsPerStep);
        delay = Math.max(0, delay);
        jitter = Math.max(0, jitter);
    }
}
