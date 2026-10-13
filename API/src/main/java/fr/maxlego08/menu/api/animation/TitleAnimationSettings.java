package fr.maxlego08.menu.api.animation;

import org.bukkit.configuration.ConfigurationSection;
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

    public void serialize(@NotNull ConfigurationSection section) {
        section.set("titles", this.titles);
        if (this.cycles != -1) section.set("cycles", this.cycles);
        if (this.initialDelay != 20) section.set("initial-delay", this.initialDelay);
        if (this.interval != 20) section.set("interval", this.interval);
        if (this.timeUnit != TimeUnit.SECONDS) section.set("time-unit", this.timeUnit.name());
        if (this.showItemsAfterAnimation) section.set("show-items-after-animation", true);
        if (this.itemUpdateInterval != 1) section.set("item-update-interval", this.itemUpdateInterval);
    }
}
