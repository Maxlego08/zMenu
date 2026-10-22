package fr.maxlego08.menu.animation.compile;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.sound.SoundOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * One thing the opening animation does, at one point in time.
 *
 * <p>Steps carry an absolute time rather than a delay from the previous step, so that the
 * contributions of the groups, of the per button delays and, later, of the intermediary frames can
 * simply be merged and sorted.</p>
 *
 * @param time     the time at which the step happens, in milliseconds since the animation started
 * @param slot     the slot the step applies to
 * @param action   what the step does to the slot
 * @param item     the item to display, only used by {@link StepAction#SET}
 * @param sound    the sound to play, or null for a silent step
 * @param useCache whether the item may be served from its build cache, the {@code use-cache}
 *                 option of the temporary item the step comes from
 */
public record AnimationStep(long time, int slot, @NotNull StepAction action, @Nullable MenuItemStack item,
                            @Nullable SoundOption sound, boolean useCache) {

    /**
     * A step whose item uses the build cache, as items do by default.
     */
    public AnimationStep(long time, int slot, @NotNull StepAction action, @Nullable MenuItemStack item, @Nullable SoundOption sound) {
        this(time, slot, action, item, sound, true);
    }

    /**
     * A step revealing the real item of the button owning the slot.
     *
     * @param time the time of the step
     * @param slot the slot to reveal
     */
    public AnimationStep(long time, int slot) {
        this(time, slot, StepAction.REVEAL, null, null, true);
    }
}
