package fr.maxlego08.menu.api.animation.slot.settings;

import fr.maxlego08.menu.api.sound.SoundOption;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * How a single button takes part in the opening animation of its inventory.
 *
 * @param enabled whether the slots of the button are animated at all; a disabled button is displayed
 *                immediately, even while the rest of the inventory is still being revealed
 * @param group   the group the button belongs to, groups are played in ascending order
 * @param delay   an extra delay for the slots of this button, in milliseconds, counted from the
 *                start of its group
 * @param sound   the sound played when a slot of this button is revealed, overriding the one of its
 *                group, or null to use the group one
 * @param allowTemporaryItems whether a temporary item may be drawn over the slots of this button; a
 *                            button holding items a player can take never allows it, so that nothing
 *                            decorative can be picked up
 */
public record ButtonAnimationSettings(boolean enabled, int group, long delay, @Nullable SoundOption sound, boolean allowTemporaryItems) {

    public ButtonAnimationSettings(boolean enabled, int group, long delay, @Nullable SoundOption sound) {
        this(enabled, group, delay, sound, true);
    }

    public ButtonAnimationSettings(boolean enabled, int group, long delay) {
        this(enabled, group, delay, null, true);
    }

    /**
     * Used by every button that does not configure its animation.
     */
    public static final ButtonAnimationSettings DEFAULT = new ButtonAnimationSettings(true, 1, 0, null, true);

    /**
     * Used by a button configured with {@code animation: false}.
     */
    public static final ButtonAnimationSettings DISABLED = new ButtonAnimationSettings(false, 1, 0, null, true);

    /**
     * Used by a button that takes part in the animation but refuses to be covered by a temporary
     * item, and that configured nothing else.
     */
    public static final ButtonAnimationSettings NOT_COVERABLE = new ButtonAnimationSettings(true, 1, 0, null, false);

    public ButtonAnimationSettings {
        delay = Math.max(0, delay);
    }

    /**
     * Writes the {@code animation} key of a button: nothing when the button keeps the settings of
     * its class, {@code false} when it does not animate, otherwise a section with what differs.
     *
     * @param buttonSection                the section of the button
     * @param allowTemporaryItemsByDefault whether the class of the button lets temporary items cover it
     */
    public void serialize(@NotNull ConfigurationSection buttonSection, boolean allowTemporaryItemsByDefault) {
        if (this.equals(allowTemporaryItemsByDefault ? DEFAULT : NOT_COVERABLE)) return;

        if (!this.enabled) {
            buttonSection.set("animation", false);
            return;
        }

        ConfigurationSection section = buttonSection.createSection("animation");
        if (this.group != 1) section.set("group", this.group);
        if (this.delay != 0) section.set("delay", this.delay);
        if (this.sound != null) this.sound.serialize(section.createSection("sound"));
        if (this.allowTemporaryItems != allowTemporaryItemsByDefault) section.set("allow-temporary-items", this.allowTemporaryItems);
    }
}
