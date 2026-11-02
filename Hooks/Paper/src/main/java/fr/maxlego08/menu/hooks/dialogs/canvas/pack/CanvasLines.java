package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * Chooses the text line each element of a canvas is drawn on.
 * <p>
 * A bitmap glyph has one vertical position per font, so the pack holds a copy of an image for each y it is
 * drawn at. Drawn on the text line its y falls on, an image only needs the y inside that line: the same
 * image 9 pixels lower shares its copy. But the client draws the lines one after the other, so an element on
 * a later line is drawn over the elements of the earlier lines whatever their order. To keep the order of the
 * elements, the lines never go back: each element (its first pass, then the overlay passes of every element)
 * takes the line of its highest drawing, but never a line after the line of what is drawn next.
 * <p>
 * Click areas stay on the first line, where their order decides which one takes a click.
 *
 * @param base    The line of each element.
 * @param overlay The line of the overlay of each element (its line when it has none).
 */
public record CanvasLines(int @NotNull [] base, int @NotNull [] overlay) {

    /**
     * The height of a text line.
     */
    public static final int LINE_HEIGHT = 9;

    /**
     * @param elements     The elements of a canvas, in drawing order.
     * @param resolver     Resolves glyph references, as the pack does.
     * @param drawnWidth   The width the variants are collected for, see {@link CanvasVariants#canvasWidth()}.
     * @param drawnHeight  The height the variants are collected for, see {@link CanvasVariants#canvasHeight()}.
     * @param canvasHeight The height of the canvas, or a negative number when it is a placeholder (then every
     *                     element stays on the first line).
     * @return The lines.
     */
    public static @NotNull CanvasLines of(@NotNull List<CanvasElement> elements, @NotNull Function<String, ResolvedGlyph> resolver, int drawnWidth,
                                          int drawnHeight, int canvasHeight) {
        int count = elements.size();
        int[] baseTop = new int[count];
        int[] overlayTop = new int[count];
        boolean[] overlays = new boolean[count];
        for (int index = 0; index < count; index++) {
            Recorder recorder = new Recorder(resolver, drawnWidth, drawnHeight);
            elements.get(index).collectVariants(recorder);
            baseTop[index] = recorder.base;
            overlayTop[index] = Math.min(recorder.base, recorder.overlay);
            overlays[index] = elements.get(index).hasOverlay();
        }
        int lastLine = canvasHeight < 1 ? 0 : (canvasHeight + LINE_HEIGHT - 1) / LINE_HEIGHT - 1;
        return assign(baseTop, overlayTop, overlays, lastLine);
    }

    /**
     * @param baseTop    The highest y drawn by each element, {@link Integer#MAX_VALUE} when it draws nothing.
     * @param overlayTop The highest y drawn by the overlay of each element.
     * @param overlays   Whether each element has an overlay.
     * @param lastLine   The last line of the canvas.
     * @return The lines: every line is at most the line of what is drawn after it.
     */
    public static @NotNull CanvasLines assign(int @NotNull [] baseTop, int @NotNull [] overlayTop, boolean @NotNull [] overlays, int lastLine) {
        int count = baseTop.length;
        int[] base = new int[count];
        int[] overlay = new int[count];
        int next = Math.max(0, lastLine);
        for (int index = count - 1; index >= 0; index--) {
            if (!overlays[index]) continue;
            next = Math.min(next, lineOf(overlayTop[index]));
            overlay[index] = next;
        }
        for (int index = count - 1; index >= 0; index--) {
            next = Math.min(next, lineOf(baseTop[index]));
            base[index] = next;
            if (!overlays[index]) overlay[index] = next;
        }
        return new CanvasLines(base, overlay);
    }

    private static int lineOf(int top) {
        return top == Integer.MAX_VALUE ? Integer.MAX_VALUE : Math.max(0, Math.floorDiv(top, LINE_HEIGHT));
    }

    /**
     * @param y    A canvas y.
     * @param line A line.
     * @return The y inside the line.
     */
    public static int local(int y, int line) {
        return y - line * LINE_HEIGHT;
    }

    /**
     * Records the highest y an element draws at, in its first pass and in its overlay.
     */
    private static final class Recorder implements CanvasVariants {

        private final Function<String, ResolvedGlyph> resolver;
        private final int width;
        private final int height;
        private int base = Integer.MAX_VALUE;
        private int overlay = Integer.MAX_VALUE;
        private int depth;

        private Recorder(@NotNull Function<String, ResolvedGlyph> resolver, int width, int height) {
            this.resolver = resolver;
            this.width = width;
            this.height = height;
        }

        @Override
        public int canvasWidth() {
            return this.width;
        }

        @Override
        public int canvasHeight() {
            return this.height;
        }

        private void record(int y) {
            if (this.depth > 0) this.overlay = Math.min(this.overlay, y);
            else this.base = Math.min(this.base, y);
        }

        @Override
        public void glyph(@NotNull ResolvedGlyph glyph, int y) {
            this.record(y);
        }

        @Override
        public void text(@NotNull String font, int y) {
            this.record(y);
        }

        @Override
        public void area(int y, int width, int height) {
        }

        @Override
        public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
            return this.resolver.apply(reference);
        }

        @Override
        public void beginOverlay() {
            this.depth++;
        }

        @Override
        public void endOverlay() {
            this.depth = Math.max(0, this.depth - 1);
        }
    }
}
