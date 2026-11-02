package fr.maxlego08.menu.api.dialog.canvas;

import fr.maxlego08.menu.api.utils.SectionSerializable;
import org.jetbrains.annotations.NotNull;

/**
 * One thing drawn on a canvas: an image, a text, a button...
 * <p>
 * Elements are created by a {@link CanvasElementLoader} from the {@code elements} section of a
 * {@code dialog-canvas} body, and write themselves back in the same format.
 */
public interface CanvasElement extends SectionSerializable {

    /**
     * @return The name of the element, which is its key in the {@code elements} section.
     */
    @NotNull
    String name();

    /**
     * Declares every vertical position this element can be drawn at, so the resource pack contains
     * them.
     *
     * @param variants The collector.
     */
    void collectVariants(@NotNull CanvasVariants variants);

    /**
     * @return True if the element reacts to clicks. A dialog whose canvas has such an element stays open
     * after a click unless its file sets {@code after-action}.
     */
    default boolean isClickable() {
        return false;
    }

    /**
     * Tells whether this element draws the same thing for every player, until the resource pack is generated
     * again: no placeholder, no click, nothing that depends on the client. A canvas made only of such elements
     * is drawn once and reused for every player.
     *
     * @return True if the element is the same for every player.
     */
    default boolean isStatic() {
        return false;
    }

    /**
     * Draws this element for a player.
     *
     * @param context The player and the tools to format text.
     * @param builder The canvas to draw on.
     */
    void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder);

    /**
     * Draws what this element shows over every other element, such as an open list or a window: the canvas
     * draws every element, then every overlay, so an overlay is on top and its click areas win. Its pack
     * variants are declared by {@link #collectVariants(CanvasVariants)} like the rest.
     *
     * @param context The player and the tools to format text.
     * @param builder The canvas to draw on.
     */
    default void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
    }

    /**
     * Tells whether {@link #renderOverlay(CanvasRenderContext, CanvasBuilder)} may draw something, for this
     * element or one it holds. The canvas skips the overlay pass of the elements that answer false, so an
     * element that overrides {@code renderOverlay} must answer true.
     *
     * @return True if the element draws an overlay.
     */
    default boolean hasOverlay() {
        return false;
    }
}
