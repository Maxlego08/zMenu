package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.animation.slot.Animation;
import fr.maxlego08.menu.api.animation.slot.AnimationPlayback;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface InventoryEngine extends BaseInventory {

    default @NotNull PerformanceDebug getPerformanceDebug() {
        return PerformanceDebug.disabled();
    }

    @Contract(pure = true)
    @NotNull
    List<Inventory> getOldInventories();

    @Contract(pure = true)
    @NotNull
    List<Button> getButtons();

    /**
     * @deprecated use buildButton with Placeholders instead
     * @param button the button to build
     */
    @Deprecated
    void buildButton(@Nullable Button button);

    void buildButton(@Nullable Button button, @NotNull Placeholders placeholders);

    /**
     * @deprecated use displayButton with Placeholders instead
     * @param button the button to display
     */
    @Deprecated
    void displayButton(@NotNull Button button);

    void displayButton(@NotNull Button button, @NotNull Placeholders placeholders);

    /**
     * @deprecated use displayFinalButton with Placeholders instead
     * @param button the button to display
     * @param slots the slots to display the button in
     */
    @Deprecated
    void displayFinalButton(@NotNull Button button, int... slots);

    void displayFinalButton(@NotNull Button button, @NotNull Placeholders placeholders, int... slots);

    @Contract(pure = true)
    @Nullable
    Inventory getMenuInventory();

    @Contract(pure = true)
    int getMaxPage();

    /**
     * Sets the number of pages of this inventory.
     *
     * @param maxPage the number of pages
     * @deprecated computed by zMenu from the buttons and patterns when the inventory opens.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void setMaxPage(int maxPage);

    void cancel(int slot);

    /**
     * Plays an animation on this inventory, now.
     *
     * <p>{@link AnimationPlayback#OVERLAY} draws the temporary items of the animation over the menu
     * without hiding anything, which is what an already open inventory usually wants;
     * {@link AnimationPlayback#REVEAL} hides the buttons and reveals them again, as opening the menu
     * does. An overlay does not freeze the inventory, a reveal freezes it if the animation says so.</p>
     *
     * <p>Only one animation plays at a time: starting another finishes the one in progress at once
     * rather than queueing behind it.</p>
     *
     * @param animation the animation to play
     * @param playback  how to play it
     * @return how long the animation lasts, in milliseconds, or 0 if nothing was played
     */
    default long playAnimation(@NotNull Animation animation, @NotNull AnimationPlayback playback) {
        return 0;
    }

    /**
     * Plays an animation on this inventory, deciding whether it freezes the inventory.
     *
     * @param animation the animation to play
     * @param playback  how to play it
     * @param freeze    whether the inventory ignores clicks until the animation is over
     * @return how long the animation lasts, in milliseconds, or 0 if nothing was played
     */
    default long playAnimation(@NotNull Animation animation, @NotNull AnimationPlayback playback, boolean freeze) {
        return 0;
    }

    /**
     * Finishes the animation currently playing, applying everything that is left at once.
     */
    default void stopAnimation() {
    }

    /**
     * Runs something once the closing animation of this inventory is over.
     *
     * <p>This is how a menu closes itself gracefully. The window is not closed while the animation
     * plays, because it cannot be: a client that has closed its inventory cannot be told to wait.
     * Nothing has closed it yet, so the menu simply stays open until the buttons have been taken
     * away, and only then does {@code whenDone} run.</p>
     *
     * <p>It is only for a close the menu decides on, such as a {@code close} action or a switch to
     * another menu. A player pressing escape closes the window immediately, and nothing can hold
     * it open, so {@code whenDone} runs at once when there is no closing animation to play, when
     * the inventory is already closed, or when the animation refuses to play.</p>
     *
     * @param whenDone what to run once the menu is ready to go away. It runs at most once, and is
     *                 skipped when the player closed the window themselves while it played
     */
    default void closeWithAnimation(@NotNull Runnable whenDone) {
        whenDone.run();
    }
}
