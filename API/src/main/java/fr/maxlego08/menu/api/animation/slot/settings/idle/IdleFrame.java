package fr.maxlego08.menu.api.animation.slot.settings.idle;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * One step of a {@link ButtonIdleAnimation}.
 *
 * <p>A frame holds how long it stays on screen and what it sounds like, so a cycle can hold a
 * picture for a while and then flash through two others, rather than giving every picture the same
 * share of the time.</p>
 *
 * @param item     the item to display
 * @param duration how long the frame stays, in milliseconds; 0 uses the interval of the animation
 * @param sound    the sound played when the frame is displayed, or null for a silent frame
 */
public record IdleFrame(@NotNull MenuItemStack item, long duration, @Nullable SoundOption sound) {

    /**
     * A frame lasting the interval of its animation, with no sound of its own.
     *
     * @param item the item to display
     */
    public IdleFrame(@NotNull MenuItemStack item) {
        this(item, 0, null);
    }

    public IdleFrame {
        duration = Math.max(0, duration);
    }

    /**
     * How long the frame lasts, falling back to the interval of the animation when it declares no
     * duration of its own.
     *
     * @param interval the interval of the animation, in milliseconds
     * @return the duration to use, in milliseconds
     */
    public long duration(long interval) {
        return this.duration > 0 ? this.duration : interval;
    }

    /**
     * Writes this frame into its section of the idle animation's {@code items}.
     *
     * @param section the section of the frame
     * @deprecated use {@link #serialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public void serialize(@NotNull ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }

    public void serialize(@NotNull Section section) {
        this.item.serialize(section);
        if (this.duration != 0) section.set("duration", this.duration);
        if (this.sound != null) this.sound.serialize(section.createSection("sound"));
    }
}
