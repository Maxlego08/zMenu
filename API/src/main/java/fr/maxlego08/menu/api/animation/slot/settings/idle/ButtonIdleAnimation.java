package fr.maxlego08.menu.api.animation.slot.settings.idle;

import fr.maxlego08.menu.api.MenuItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * An animation a button plays on its own slots for as long as the menu stays open.
 *
 * <p>This is not the opening animation: there is no timeline, no group and no freeze. The frames
 * are simply cycled on the slots of the button, which keeps working normally throughout.</p>
 *
 * <p>The cycle only starts once the opening animation has revealed the slot, so the two never fight
 * over it.</p>
 *
 * @param frames   the frames to cycle through, in order
 * @param interval the delay between two frames, in milliseconds, used by every frame that does not
 *                 declare a duration of its own
 * @param loop     whether the cycle restarts once it reaches the end; when false the button goes
 *                 back to its real item and the cycle stops
 * @param random   whether the next frame is picked at random rather than taken in order
 */
public record ButtonIdleAnimation(@NotNull List<IdleFrame> frames, long interval, boolean loop, boolean random) {

    public ButtonIdleAnimation(@NotNull List<IdleFrame> frames, long interval, boolean loop) {
        this(frames, interval, loop, false);
    }

    public ButtonIdleAnimation {
        frames = List.copyOf(frames);
        interval = Math.max(1, interval);
    }

    /**
     * An animation cycling plain items, each lasting the interval and playing no sound.
     *
     * @param items    the items to cycle through, in order
     * @param interval the delay between two items, in milliseconds
     * @param loop     whether the cycle restarts once it reaches the end
     * @param random   whether the next item is picked at random rather than taken in order
     * @return the animation
     */
    @NotNull
    public static ButtonIdleAnimation of(@NotNull List<MenuItemStack> items, long interval, boolean loop, boolean random) {
        return new ButtonIdleAnimation(items.stream().map(IdleFrame::new).toList(), interval, loop, random);
    }

    /**
     * An animation cycling plain items in order.
     *
     * @param items    the items to cycle through, in order
     * @param interval the delay between two items, in milliseconds
     * @param loop     whether the cycle restarts once it reaches the end
     * @return the animation
     */
    @NotNull
    public static ButtonIdleAnimation of(@NotNull List<MenuItemStack> items, long interval, boolean loop) {
        return of(items, interval, loop, false);
    }

    /**
     * The items of the frames, in order.
     *
     * @return the items
     */
    @NotNull
    public List<MenuItemStack> items() {
        return this.frames.stream().map(IdleFrame::item).toList();
    }

    /**
     * Gets the frame to display on a given turn.
     *
     * @param index the turn, starting at 0
     * @return the frame
     */
    @NotNull
    public IdleFrame frame(int index) {
        return this.frames.get(Math.floorMod(index, this.frames.size()));
    }

    /**
     * Gets the frame to display on a given turn, picking at random when the cycle asks for it.
     *
     * @param index  the turn, starting at 0
     * @param random the source to pick from
     * @return the frame
     */
    @NotNull
    public IdleFrame frame(int index, @NotNull java.util.Random random) {
        return this.random ? this.frames.get(random.nextInt(this.frames.size())) : this.frame(index);
    }

    /**
     * Gets the item to display on a given turn.
     *
     * @param index the turn, starting at 0
     * @return the item
     */
    @NotNull
    public MenuItemStack item(int index) {
        return this.frame(index).item();
    }

    /**
     * Gets the item to display on a given turn, picking at random when the cycle asks for it.
     *
     * @param index  the turn, starting at 0
     * @param random the source to pick from
     * @return the item
     */
    @NotNull
    public MenuItemStack item(int index, @NotNull java.util.Random random) {
        return this.frame(index, random).item();
    }

    /**
     * Returns whether the cycle is over.
     *
     * @param index the turn, starting at 0
     * @return true if there is nothing left to display
     */
    public boolean isOver(int index) {
        return !this.loop && index >= this.frames.size();
    }
}
