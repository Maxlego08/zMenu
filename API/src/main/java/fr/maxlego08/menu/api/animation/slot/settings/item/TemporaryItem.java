package fr.maxlego08.menu.api.animation.slot.settings.item;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An item that exists only while the opening animation plays.
 *
 * <p>A temporary item is not a button: it has no click, no requirement, and it is gone once the
 * animation is over. It may be placed on a slot a button owns, in which case it hides that button
 * until it is removed, and it may also be placed on a slot no button will ever occupy.</p>
 *
 * <p>Its life is described by its keyframes. The common case of an item appearing once and going
 * away once is written with {@code show} and {@code hide} in the configuration, and loaded as the
 * two keyframes it stands for, so there is a single behaviour to reason about.</p>
 *
 * @param name   the name of the item in the configuration, used to report errors
 * @param slots  the slots the item occupies
 * @param frames what the item does over time, in the order it was written
 * @param chance        the chance of the item appearing at all, in percent; rolled once when the
 *                      animation is compiled, so an item that wins appears for all of its keyframes
 * @param chancePerSlot whether the chance, and the choice of item, are rolled once for the whole
 *                      item or separately for each of its slots
 * @param count         how many of its slots the item uses, picked at random; 0 uses all of them
 * @param useCache      whether the item may be served from its build cache, like the
 *                      {@code use-cache} option of a button; false rebuilds it on every frame,
 *                      which is what an item whose content changes while the animation plays needs
 */
public record TemporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull List<AnimationKeyframe> frames, float chance, boolean chancePerSlot, int count, boolean useCache) {

    public TemporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull List<AnimationKeyframe> frames, float chance, boolean chancePerSlot, int count) {
        this(name, slots, frames, chance, chancePerSlot, count, true);
    }

    public TemporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull List<AnimationKeyframe> frames, float chance, boolean chancePerSlot) {
        this(name, slots, frames, chance, chancePerSlot, 0, true);
    }

    public TemporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull List<AnimationKeyframe> frames, float chance) {
        this(name, slots, frames, chance, false, 0, true);
    }

    public TemporaryItem(@NotNull String name, @NotNull List<Integer> slots, @NotNull List<AnimationKeyframe> frames) {
        this(name, slots, frames, 100, false, 0, true);
    }

    public TemporaryItem {
        slots = List.copyOf(slots);
        frames = List.copyOf(frames);
        count = Math.max(0, count);
    }
}
