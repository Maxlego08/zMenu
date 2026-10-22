package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.animation.slot.SlotAnimation;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.configuration.ConfigurationSection;
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

    /**
     * The delay between two steps when nothing sets one, in milliseconds.
     */
    public static final long DEFAULT_INTERVAL = 50;

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

    /**
     * Writes this group, leaving out what it inherits: a group of {@code groups} only overrides the
     * default group of its animation, so only what differs from {@code defaults} is written.
     *
     * @param section  the section of the group
     * @param defaults the group this one inherits from, or null for the default group itself
     * @throws UnsupportedOperationException if the animation of the group cannot be serialized
     */
    public void serialize(@NotNull ConfigurationSection section, @Nullable SlotAnimationGroup defaults) {
        if (defaults == null || this.animation != defaults.animation) this.animation.serialize(section);

        long inheritedInterval = defaults == null ? DEFAULT_INTERVAL : defaults.interval;
        if (this.interval != inheritedInterval) section.set("interval", this.interval);

        int inheritedSlotsPerStep = defaults == null ? 1 : defaults.slotsPerStep;
        if (this.slotsPerStep != inheritedSlotsPerStep) section.set("slots-per-step", this.slotsPerStep);

        if (this.delay != 0) section.set("delay", this.delay);

        SoundOption inheritedSound = defaults == null ? null : defaults.sound;
        if (this.sound != null && this.sound != inheritedSound) this.sound.serialize(section.createSection("sound"));

        boolean inheritedReverse = defaults != null && defaults.reverse;
        if (this.reverse != inheritedReverse) section.set("reverse", this.reverse);

        long inheritedJitter = defaults == null ? 0 : defaults.jitter;
        if (this.jitter != inheritedJitter) section.set("interval-jitter", this.jitter);
    }
}
