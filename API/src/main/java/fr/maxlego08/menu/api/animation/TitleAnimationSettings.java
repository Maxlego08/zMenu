package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Immutable settings of a title animation: its frames, compiled once, its timing and its behaviour.
 */
public final class TitleAnimationSettings {

    private final TemplateList frames;
    private final int cycles;
    private final int initialDelay;
    private final int interval;
    private final TimeUnit timeUnit;
    private final boolean showItemsAfterAnimation;
    private final int itemUpdateInterval;

    /**
     * @param titles                  the frames as written, compiled once
     * @param cycles                  how many times the animation runs; -1 for no end
     * @param initialDelay            the delay before the first frame, in {@code timeUnit}
     * @param interval                the time between two frames, in {@code timeUnit}
     * @param timeUnit                the unit of the delay and the interval
     * @param showItemsAfterAnimation whether the items are shown once the animation ends
     * @param itemUpdateInterval      every how many frames the items are sent again
     */
    public TitleAnimationSettings(@NotNull List<@NotNull String> titles, int cycles, int initialDelay, int interval, @NotNull TimeUnit timeUnit,
                                  boolean showItemsAfterAnimation, int itemUpdateInterval) {
        this.frames = TextService.get().flow().compileList(Objects.requireNonNull(titles, "titles cannot be null"));
        this.cycles = cycles;
        this.initialDelay = initialDelay;
        this.interval = interval;
        this.timeUnit = Objects.requireNonNull(timeUnit, "timeUnit cannot be null");
        this.showItemsAfterAnimation = showItemsAfterAnimation;
        this.itemUpdateInterval = itemUpdateInterval;
    }

    /**
     * @return the frames, compiled once, rendered for the player at each frame
     */
    public @NotNull TemplateList frames() {
        return this.frames;
    }

    /**
     * @return the frames as written
     * @deprecated use {@link #frames()}; each frame's {@code source()} is its text as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull List<@NotNull String> titles() {
        List<String> titles = new ArrayList<>(this.frames.size());
        for (Template frame : this.frames.templates()) {
            titles.add(frame.source());
        }
        return titles;
    }

    public int cycles() {
        return this.cycles;
    }

    public int initialDelay() {
        return this.initialDelay;
    }

    public int interval() {
        return this.interval;
    }

    public @NotNull TimeUnit timeUnit() {
        return this.timeUnit;
    }

    public boolean showItemsAfterAnimation() {
        return this.showItemsAfterAnimation;
    }

    public int itemUpdateInterval() {
        return this.itemUpdateInterval;
    }

    @SuppressWarnings("deprecation")
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("titles", this.titles());
        if (this.cycles != -1) section.set("cycles", this.cycles);
        if (this.initialDelay != 20) section.set("initial-delay", this.initialDelay);
        if (this.interval != 20) section.set("interval", this.interval);
        if (this.timeUnit != TimeUnit.SECONDS) section.set("time-unit", this.timeUnit.name());
        if (this.showItemsAfterAnimation) section.set("show-items-after-animation", true);
        if (this.itemUpdateInterval != 1) section.set("item-update-interval", this.itemUpdateInterval);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean equals(Object other) {
        return this == other || other instanceof TitleAnimationSettings settings && this.cycles == settings.cycles
                && this.initialDelay == settings.initialDelay && this.interval == settings.interval && this.timeUnit == settings.timeUnit
                && this.showItemsAfterAnimation == settings.showItemsAfterAnimation && this.itemUpdateInterval == settings.itemUpdateInterval
                && this.titles().equals(settings.titles());
    }

    @Override
    @SuppressWarnings("deprecation")
    public int hashCode() {
        return Objects.hash(this.titles(), this.cycles, this.initialDelay, this.interval, this.timeUnit, this.showItemsAfterAnimation, this.itemUpdateInterval);
    }

    @Override
    @SuppressWarnings("deprecation")
    public String toString() {
        return "TitleAnimationSettings[titles=" + this.titles() + ", cycles=" + this.cycles + ", initialDelay=" + this.initialDelay + ", interval="
                + this.interval + ", timeUnit=" + this.timeUnit + ", showItemsAfterAnimation=" + this.showItemsAfterAnimation
                + ", itemUpdateInterval=" + this.itemUpdateInterval + "]";
    }
}
