package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.CanvasLayout;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.GlyphVariant;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.HitboxGlyphs;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.SpaceFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.CanvasTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextFonts;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextMeasurer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * Lays a canvas out on the lines of a {@code plain_message} body.
 * <p>
 * Each element is drawn on the text line set with {@link #line(int)} (the first by default): it moves the
 * cursor to its x with space characters, then draws itself in the font of its y inside that line, which
 * places it vertically. Elements are drawn in the order they were added, line after line. Click areas are
 * always on the first line. Every line ends at exactly the canvas width, so the client, which centres each
 * line on its own, gives every line the same left edge.
 * <p>
 * The body is made wider than the canvas because the client wraps a line as soon as the cursor passes
 * the body width minus 8 pixels, even if a later character moves it back.
 */
public final class ZCanvasBuilder implements CanvasBuilder {

    public static final int MAX_WIDTH = 1014;
    private static final int LINE_HEIGHT = 9;
    private static final int BODY_PADDING = 10;
    private static final int MAX_BODY_WIDTH = 1024;
    private static final int BODY_WIDTH_STEP = 16;

    private final int width;
    private final int height;
    private final CanvasLayout layout;
    private final TextFonts textFonts;
    private final Consumer<String> missing;
    private final boolean rowClicks;
    private final List<Drawing> drawings = new ArrayList<>();
    private final List<Area> areas = new ArrayList<>();
    private final Map<Integer, List<Drawing>> lineDrawings = new TreeMap<>();
    private int line;

    public ZCanvasBuilder(int width, int height, @NotNull CanvasLayout layout, @NotNull TextFonts textFonts, @NotNull Consumer<String> missing) {
        this(width, height, layout, textFonts, missing, false);
    }

    /**
     * @param rowClicks True for a client older than 1.21.11, which finds a click by its line and its
     *                  distance from the start of the line: each line then starts with invisible spaces
     *                  carrying the click events of the areas crossing it.
     */
    public ZCanvasBuilder(int width, int height, @NotNull CanvasLayout layout, @NotNull TextFonts textFonts, @NotNull Consumer<String> missing, boolean rowClicks) {
        this.width = Math.clamp(width, 1, MAX_WIDTH);
        this.height = Math.max(1, height);
        this.layout = layout;
        this.textFonts = textFonts;
        this.missing = missing;
        this.rowClicks = rowClicks;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    @Override
    public @NotNull CanvasBuilder line(int line) {
        this.line = Math.clamp(line, 0, this.lineCount() - 1);
        return this;
    }

    private @NotNull List<Drawing> target() {
        return this.line == 0 ? this.drawings : this.lineDrawings.computeIfAbsent(this.line, key -> new ArrayList<>());
    }

    @Override
    public @NotNull CanvasBuilder glyph(@NotNull ResolvedGlyph glyph, int x, int y) {
        int canvasTop = y == Integer.MIN_VALUE ? (glyph.hasNativeGlyph() ? glyph.nativeY() : 0) : y;
        int top = canvasTop - this.line * LINE_HEIGHT;
        boolean nativeGlyph = glyph.hasNativeGlyph() && top == glyph.nativeY();

        List<List<GlyphCell>> rows = glyph.rows();
        List<Drawing> drawn = new ArrayList<>(rows.size());
        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<GlyphCell> row = rows.get(rowIndex);
            if (row.isEmpty()) continue;
            int rowTop = top + rowIndex * glyph.rowHeight();
            int[] offsets = glyph.cellOffsets(row);

            TextComponent.Builder content = Component.text();
            int cursor = 0;
            for (int index = 0; index < row.size(); index++) {
                GlyphCell cell = row.get(index);
                content.append(space(offsets[index] - cursor));
                if (nativeGlyph) {
                    content.append(Component.text(cell.nativeChar()).font(cell.nativeFont()));
                    content.append(space(-1));
                } else {
                    String character = this.layout.glyph(new GlyphVariant(cell, rowTop));
                    if (character == null) {
                        this.missing.accept("glyph " + glyph.reference() + " at y " + (rowTop + this.line * LINE_HEIGHT));
                        return this;
                    }
                    content.append(Component.text(character + SpaceFont.encode(-1)).font(CanvasLayout.canvasFont(rowTop)));
                }
                cursor = offsets[index] + cell.advance() - 1;
            }
            drawn.add(new Drawing(x, content.build(), cursor));
        }
        this.target().addAll(drawn);
        return this;
    }

    @Override
    public @NotNull CanvasBuilder text(@NotNull Component text, @NotNull String font, int x, int y) {
        int top = y - this.line * LINE_HEIGHT;
        CanvasTextFont textFont = this.textFont(font, top);
        if (textFont == null) return this;
        this.target().add(new Drawing(x, Component.text().font(CanvasTextFont.fontKey(font, top)).append(text).build(), TextMeasurer.measure(text, textFont)));
        return this;
    }

    @Override
    public @NotNull CanvasBuilder text(@NotNull Component text, @NotNull String font, int x, int y, int width, @NotNull HAlign align) {
        int top = y - this.line * LINE_HEIGHT;
        CanvasTextFont textFont = this.textFont(font, top);
        if (textFont == null) return this;
        Component fitted = TextMeasurer.fit(text, textFont, width);
        double textWidth = TextMeasurer.measure(fitted, textFont);
        this.target().add(new Drawing(x + align.offset(width, textWidth), Component.text().font(CanvasTextFont.fontKey(font, top)).append(fitted).build(), textWidth));
        return this;
    }

    @Override
    public @NotNull CanvasBuilder area(int x, int y, int width, int height, @Nullable ClickEvent click, @Nullable HoverEventSource<?> hover) {
        if (width < 1 || height < 1 || (click == null && hover == null)) return this;

        TextComponent.Builder content = Component.text();
        for (GlyphCell cell : HitboxGlyphs.cells(width, height)) {
            String character = this.layout.glyph(new GlyphVariant(cell, y));
            if (character == null) {
                this.missing.accept("click area of " + width + "x" + height + " at y " + y);
                return this;
            }
            content.append(Component.text(character + SpaceFont.encode(-1)).font(CanvasLayout.canvasFont(y)));
        }
        Style style = Style.style().clickEvent(click).hoverEvent(hover).build();
        this.areas.add(new Area(x, y, width, height, content.style(style).build(), style));
        return this;
    }

    private CanvasTextFont textFont(@NotNull String font, int y) {
        CanvasTextFont textFont = this.textFonts.get(font);
        if (textFont == null || !this.layout.hasText(font, y)) {
            this.missing.accept("font " + font + " at y " + y);
            return null;
        }
        return textFont;
    }

    @Override
    public @NotNull List<Component> wrap(@NotNull Component text, @NotNull String font, int width, boolean words, int maxLines, boolean ellipsis) {
        CanvasTextFont textFont = this.textFonts.get(font);
        if (textFont == null) return List.of(text);
        return TextMeasurer.wrap(text, textFont, width, words, maxLines, ellipsis);
    }

    @Override
    public double measure(@NotNull Component text, @NotNull String font) {
        CanvasTextFont textFont = this.textFonts.get(font);
        return textFont == null ? 0 : TextMeasurer.measure(text, textFont);
    }

    @Override
    public @NotNull Component build() {
        TextComponent.Builder root = Component.text().shadowColor(ShadowColor.none());

        if (this.rowClicks) this.appendRowClicks(root, 0);
        int end = this.lineEnd();
        double cursor = 0;
        for (Drawing drawing : this.drawings) {
            root.append(space(drawing.x() - cursor));
            root.append(drawing.content());
            cursor = drawing.x() + drawing.advance();
        }
        for (Area area : this.areas) {
            root.append(space(area.x() - cursor));
            root.append(area.content());
            cursor = area.x() + area.width();
        }
        root.append(space(end - cursor));

        int lines = this.lineCount();
        for (int line = 1; line < lines; line++) {
            root.append(Component.newline());
            boolean filled = this.rowClicks && this.appendRowClicks(root, line);
            List<Drawing> inline = this.lineDrawings.getOrDefault(line, List.of());
            if (inline.isEmpty()) {
                if (!filled) root.append(Component.text(SpaceFont.encode(1) + SpaceFont.encode(-1)).font(SpaceFont.KEY));
                continue;
            }
            double lineCursor = 0;
            for (Drawing drawing : inline) {
                root.append(space(drawing.x() - lineCursor));
                root.append(drawing.content());
                lineCursor = drawing.x() + drawing.advance();
            }
            root.append(space(end - lineCursor));
        }
        return root.build();
    }

    private int lineCount() {
        return (int) Math.ceil(this.height / (double) LINE_HEIGHT);
    }

    @Override
    public @NotNull CanvasBuilder inline(@NotNull Component content, double advance, int x, int line) {
        if (line < 0 || line >= this.lineCount()) {
            this.missing.accept("line " + line + ", outside the " + this.lineCount() + " lines of the canvas");
            return this;
        }
        Drawing drawing = new Drawing(x, content, advance);
        if (line == 0) {
            this.drawings.add(drawing);
        } else {
            this.lineDrawings.computeIfAbsent(line, key -> new ArrayList<>()).add(drawing);
        }
        return this;
    }

    /**
     * Writes, at the start of a line, invisible spaces from the left edge to the right edge of the canvas,
     * each carrying the style of the last area covering it on this line, then moves back to the left edge.
     *
     * @return True if the line received anything.
     */
    private boolean appendRowClicks(@NotNull TextComponent.Builder root, int line) {
        int top = line * LINE_HEIGHT;
        int bottom = top + LINE_HEIGHT;
        List<Area> crossing = this.areas.stream().filter(area -> area.y() < bottom && area.y() + area.height() > top).toList();
        if (crossing.isEmpty()) return false;

        TreeSet<Integer> edges = new TreeSet<>(List.of(0, this.width));
        for (Area area : crossing) {
            edges.add(Math.clamp(area.x(), 0, this.width));
            edges.add(Math.clamp(area.x() + area.width(), 0, this.width));
        }

        Integer start = null;
        for (int edge : edges) {
            if (start != null && edge > start) {
                Style style = Style.empty();
                for (Area area : crossing) {
                    if (area.x() <= start && start < area.x() + area.width()) style = area.style();
                }
                root.append(Component.text(SpaceFont.encode(edge - start), style).font(SpaceFont.KEY));
            }
            start = edge;
        }
        root.append(space(-this.width));
        return true;
    }

    /**
     * @return The furthest any line goes while it is drawn. An image or a click area advances one pixel past
     * its right edge before stepping back, so each counts one more pixel.
     */
    private double peak() {
        double peak = this.width;
        double cursor = 0;
        for (Drawing drawing : this.drawings) {
            peak = Math.max(peak, Math.max(cursor, drawing.x()));
            cursor = drawing.x() + drawing.advance();
            peak = Math.max(peak, cursor + 1);
        }
        for (Area area : this.areas) {
            peak = Math.max(peak, Math.max(cursor, area.x() + area.width() + 1));
            cursor = area.x() + area.width();
        }
        for (List<Drawing> line : this.lineDrawings.values()) {
            double lineCursor = 0;
            for (Drawing drawing : line) {
                peak = Math.max(peak, Math.max(lineCursor, drawing.x()));
                lineCursor = drawing.x() + drawing.advance();
                peak = Math.max(peak, lineCursor + 1);
            }
        }
        return peak;
    }

    /**
     * Where every line ends. The client counts the lines of a body by splitting its text again at the width
     * of its longest line, so a line that goes past where the lines end, even for a pixel, adds an empty line
     * to the body. Every line therefore ends at the furthest any line goes, at least one pixel past the
     * canvas, and all lines keep the same width.
     *
     * @return The x where every line ends.
     */
    private int lineEnd() {
        return Math.max(this.width + 1, (int) Math.ceil(this.peak()));
    }

    @Override
    public int bodyWidth() {
        int overflow = Math.max(0, this.lineEnd() - this.width - 1);
        return bodyWidth(this.width, (overflow + BODY_WIDTH_STEP - 1) / BODY_WIDTH_STEP);
    }

    /**
     * The width of the {@code plain_message} body of a canvas, which is also the width of the outline the
     * client draws around it after a click. What is drawn past the right edge widens it by steps of
     * {@value #BODY_WIDTH_STEP} pixels, so the widths a canvas can have are few and known in advance.
     *
     * @param width The width of the canvas.
     * @param steps The number of steps past its right edge.
     * @return The width of the body.
     */
    public static int bodyWidth(int width, int steps) {
        return Math.min(MAX_BODY_WIDTH, width + BODY_PADDING + steps * BODY_WIDTH_STEP);
    }

    private static @NotNull Component space(double pixels) {
        String characters = SpaceFont.encode(pixels);
        return characters.isEmpty() ? Component.empty() : Component.text(characters).font(SpaceFont.KEY);
    }

    private record Drawing(double x, @NotNull Component content, double advance) {
    }

    private record Area(int x, int y, int width, int height, @NotNull Component content, @NotNull Style style) {
    }
}
