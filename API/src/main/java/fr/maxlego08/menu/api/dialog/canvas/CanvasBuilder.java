package fr.maxlego08.menu.api.dialog.canvas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;

/**
 * Draws images and text at pixel positions, then turns them into the component of a dialog
 * {@code plain_message} body.
 * <p>
 * Positions are in GUI pixels from the top-left corner of the canvas. Elements are drawn in the order
 * they are added, so a later element is drawn over an earlier one.
 */
public interface CanvasBuilder {

    /**
     * @return The width of the canvas.
     */
    int width();

    /**
     * @return The height of the canvas.
     */
    int height();

    /**
     * Sets the text line the next images and texts are put on. They keep their canvas position: only the
     * part of their y inside the line costs a pack variant, so the same image 9 pixels lower can share one.
     * The canvas sets the line of each element before it draws it, from what the pack was generated with.
     *
     * @param line The line, from 0, kept inside the canvas.
     * @return This builder.
     */
    @NotNull
    default CanvasBuilder line(int line) {
        return this;
    }

    /**
     * Draws a glyph. A glyph whose variant is not in the generated pack is skipped and logged.
     *
     * @param glyph The glyph.
     * @param x     The x of its left edge.
     * @param y     The y of its top edge, or {@link Integer#MIN_VALUE} to draw it where its owning plugin draws it.
     * @return This builder.
     */
    @NotNull
    CanvasBuilder glyph(@NotNull ResolvedGlyph glyph, int x, int y);

    /**
     * Draws a text on one line.
     *
     * @param text The text, already resolved for the player.
     * @param font The name of the font in {@code resource-pack/fonts.yml}, or {@code default}.
     * @param x    The x of its left edge.
     * @param y    The y of its top.
     * @return This builder.
     */
    @NotNull
    CanvasBuilder text(@NotNull Component text, @NotNull String font, int x, int y);

    /**
     * Draws a text inside a box, cut with an ellipsis when it is wider than the box.
     *
     * @param text  The text, already resolved for the player.
     * @param font  The name of the font in {@code resource-pack/fonts.yml}, or {@code default}.
     * @param x     The x of the box.
     * @param y     The y of the top of the text.
     * @param width The width of the box.
     * @param align Where the text sits inside the box.
     * @return This builder.
     */
    @NotNull
    CanvasBuilder text(@NotNull Component text, @NotNull String font, int x, int y, int width, @NotNull HAlign align);

    /**
     * Draws a component on one of the text lines of the canvas, which is the only way to place components
     * the client cannot move vertically, such as player heads and sprites (object components). A line is
     * 9 pixels high and starts at canvas y {@code 9 × line}.
     *
     * @param content The component.
     * @param advance How far the component moves the cursor.
     * @param x       The x of its left edge.
     * @param line    The line, from 0.
     * @return This builder.
     */
    @NotNull
    CanvasBuilder inline(@NotNull Component content, double advance, int x, int line);

    /**
     * Adds a click area. Areas are laid over everything drawn, and where two areas overlap the one added
     * last receives the click. Clients older than 1.21.11 only find a click by its line, so on them an area
     * covers every line of 9 pixels it touches.
     *
     * @param x      The x of its left edge.
     * @param y      The y of its top edge.
     * @param width  Its width.
     * @param height Its height, from 1 to 256.
     * @param click  What a click does, usually from {@link CanvasManager#callback}, or null.
     * @param hover  What shows over the area, or null: a text, or any hover source such as an
     *               {@code ItemStack}, which shows the item's own tooltip.
     * @return This builder.
     */
    @NotNull
    CanvasBuilder area(int x, int y, int width, @Range(from = 1, to = 256) int height, @Nullable ClickEvent click, @Nullable HoverEventSource<?> hover);

    /**
     * Breaks a text into lines that fit a width, measured as the client draws them.
     *
     * @param text     The text, already resolved for the player.
     * @param font     The name of the font.
     * @param width    The width of a line.
     * @param words    True to break between words, false between characters.
     * @param maxLines The number of lines kept.
     * @param ellipsis True to end the last kept line with an ellipsis when lines were dropped.
     * @return The lines, at least one.
     */
    @NotNull
    List<Component> wrap(@NotNull Component text, @NotNull String font, int width, boolean words, int maxLines, boolean ellipsis);

    /**
     * Measures a text as the client would draw it.
     *
     * @param text The text.
     * @param font The name of the font.
     * @return Its width in GUI pixels.
     */
    double measure(@NotNull Component text, @NotNull String font);

    /**
     * @return The component to put in a {@code plain_message} body of width {@link #bodyWidth()}.
     */
    @NotNull
    Component build();

    /**
     * @return The width to give the {@code plain_message} body holding this canvas.
     */
    int bodyWidth();
}
