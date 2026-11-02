package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Draws text, with zMenu placeholders, PlaceholderAPI and MiniMessage.
 *
 * <pre>
 * title:
 *   type: text
 *   text: "&lt;gold&gt;Shop - %player_name%"   # or a list, one line each
 *   font: default                       # optional, a font of resource-pack/fonts.yml
 *   x: 0
 *   y: 6
 *   width: 320                          # optional: cut the text with ... to fit, and align it
 *   align: center                       # optional: left, center or right, needs width
 *   line-height: 10                     # optional: the distance between two lines, 9 by default
 * </pre>
 * With {@code max-lines} (or {@code height}), the text becomes a box: it is broken into lines that fit
 * {@code width}, whatever its placeholders give.
 * <pre>
 *   max-lines: 4                        # or height: 40, divided by line-height
 *   wrap: word                          # word, char, or none (each line of text is cut with ...)
 *   vertical-align: top                 # top, center or bottom inside the box
 *   overflow: ellipsis                  # ellipsis (... on the last line) or hide
 * </pre>
 * {@code x} and {@code y} accept placeholders, see {@link PositionValue}; {@code width} and {@code align}
 * accept any placeholder.
 * <p>
 * The MiniMessage tags a placeholder gives are shown as text, so a value a player writes, such as a nickname,
 * cannot change colors, fonts or clicks. {@code escape-placeholders: false} reads them as tags, for
 * placeholders that give formatted text on purpose.
 *
 * @param name       The name of the element.
 * @param lines      The lines of text.
 * @param font       The font, which may be a placeholder with {@code font-values}, see {@link PackValue}.
 * @param x          The x of the text, or of its box.
 * @param y          The y of the top of the first line.
 * @param width      The width of the box, or null to draw the text as it is.
 * @param align      Where each line sits inside the box.
 * @param lineHeight The distance between two lines, or null for 9.
 * @param box        How the text fills a box of several lines, or null for one drawn line per line of text.
 * @param escapePlaceholders True to show the tags placeholders give as text.
 */
public record TextElement(@NotNull String name, @NotNull List<String> lines, @NotNull PackValue font, @NotNull PositionValue x, @NotNull PositionValue y,
                          @Nullable CanvasValue<Integer> width, @NotNull CanvasValue<HAlign> align, @Nullable Integer lineHeight, @Nullable TextBox box,
                          boolean escapePlaceholders)
        implements CanvasElement, CanvasLayoutable {

    private static final int DEFAULT_LINE_HEIGHT = 9;
    private static final int GLYPH_HEIGHT = 8;

    public TextElement {
        lines = List.copyOf(lines);
    }

    public TextElement(@NotNull String name, @NotNull List<String> lines, @NotNull PackValue font, @NotNull PositionValue x, @NotNull PositionValue y,
                       @Nullable CanvasValue<Integer> width, @NotNull CanvasValue<HAlign> align, @Nullable Integer lineHeight, @Nullable TextBox box) {
        this(name, lines, font, x, y, width, align, lineHeight, box, true);
    }

    /**
     * How a text fills a box.
     *
     * @param maxLines The number of lines the box holds.
     * @param wrap     How lines are broken.
     * @param vertical Where the lines sit inside the box.
     * @param ellipsis True to end the last line with ... when the text does not fit, false to drop the rest.
     */
    public record TextBox(int maxLines, @NotNull Wrap wrap, @NotNull VerticalAlign vertical, boolean ellipsis) {
    }

    public enum Wrap {
        WORD,
        CHAR,
        NONE
    }

    public enum VerticalAlign {
        TOP,
        CENTER,
        BOTTOM
    }

    private int lineStep() {
        return this.lineHeight == null ? DEFAULT_LINE_HEIGHT : this.lineHeight;
    }

    private int lineOffset(int index) {
        return index * this.lineStep();
    }

    private int verticalOffset(int lineCount) {
        if (this.box == null) return 0;
        int free = (this.box.maxLines() - lineCount) * this.lineStep();
        return switch (this.box.vertical()) {
            case TOP -> 0;
            case CENTER -> free / 2;
            case BOTTOM -> free;
        };
    }

    /**
     * @param lineCount A number of lines.
     * @return The height the lines take, which is what a growing box gives to a layout.
     */
    public int heightFor(int lineCount) {
        return Math.max(1, lineCount) * this.lineStep();
    }

    /**
     * @return The most lines this text can take.
     */
    public int maxLines() {
        return this.box == null ? this.lines.size() : this.box.maxLines();
    }

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        if (this.width == null) return 0;
        Integer fixed = this.width.fixed();
        return fixed == null ? 0 : fixed;
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        if (this.box != null) return this.heightFor(this.box.maxLines());
        return this.lineOffset(this.lines.size() - 1) + GLYPH_HEIGHT;
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new TextElement(this.name, this.lines, this.font, PositionValue.of(x), PositionValue.of(y), width > 0 ? CanvasValue.of(width) : this.width,
                this.align, this.lineHeight, this.box, this.escapePlaceholders);
    }

    @Override
    public boolean isStatic() {
        return !this.font.isPlaceholder() && !this.x.isPlaceholder() && !this.y.isPlaceholder() && (this.width == null || !this.width.isPlaceholder())
                && !this.align.isPlaceholder() && this.lines.stream().noneMatch(line -> line.indexOf('%') >= 0);
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (String font : this.font.values()) {
            for (int top : this.y.all()) {
                if (this.box == null) {
                    for (int index = 0; index < this.lines.size(); index++) variants.text(font, top + this.lineOffset(index));
                    continue;
                }
                for (int count = 1; count <= this.box.maxLines(); count++) {
                    int offset = this.verticalOffset(count);
                    for (int index = 0; index < count; index++) variants.text(font, top + offset + this.lineOffset(index));
                }
            }
        }
    }

    /**
     * Breaks the text into the lines drawn for a player.
     *
     * @param context The player.
     * @param builder The canvas, which measures text.
     * @return The lines.
     */
    public @NotNull List<Component> drawnLines(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        List<Component> formatted = new ArrayList<>(this.lines.size());
        for (String line : this.lines) formatted.add(context.format(line, this.escapePlaceholders));
        if (this.box == null || this.width == null) return formatted;

        int boxWidth = this.width.resolve(context);
        int maxLines = this.box.maxLines();
        if (this.box.wrap() == Wrap.NONE) {
            List<Component> kept = new ArrayList<>(formatted.subList(0, Math.min(maxLines, formatted.size())));
            if (this.box.ellipsis() && formatted.size() > maxLines) {
                kept.set(kept.size() - 1, kept.getLast().append(Component.text("...")));
            }
            return kept;
        }
        Component joined = Component.join(JoinConfiguration.separator(Component.newline()), formatted);
        return builder.wrap(joined, this.font.resolve(context), boxWidth, this.box.wrap() == Wrap.WORD, maxLines, this.box.ellipsis());
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        int left = this.x.resolve(context);
        List<Component> drawn = this.drawnLines(context, builder);
        int top = this.y.resolve(context) + this.verticalOffset(drawn.size());
        Integer boxWidth = this.width == null ? null : this.width.resolve(context);
        HAlign lineAlign = this.align.resolve(context);
        String lineFont = this.font.resolve(context);
        for (int index = 0; index < drawn.size(); index++) {
            int lineY = top + this.lineOffset(index);
            if (boxWidth == null) {
                builder.text(drawn.get(index), lineFont, left, lineY);
            } else {
                builder.text(drawn.get(index), lineFont, left, lineY, boxWidth, lineAlign);
            }
        }
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "text");
        section.set("text", this.lines.size() == 1 ? this.lines.getFirst() : this.lines);
        if (!this.font.raw().equals("default")) this.font.serialize(section, "font");
        this.x.serialize(section, "x");
        this.y.serialize(section, "y");
        if (this.width != null) this.width.serialize(section, "width");
        if (this.align.isPlaceholder() || this.align.fixed() != HAlign.LEFT) this.align.serialize(section, "align");
        if (this.lineHeight != null) section.set("line-height", this.lineHeight);
        if (!this.escapePlaceholders) section.set("escape-placeholders", false);
        if (this.box != null) {
            section.set("max-lines", this.box.maxLines());
            if (this.box.wrap() != Wrap.WORD) section.set("wrap", this.box.wrap().name().toLowerCase(Locale.ROOT));
            if (this.box.vertical() != VerticalAlign.TOP) section.set("vertical-align", this.box.vertical().name().toLowerCase(Locale.ROOT));
            if (!this.box.ellipsis()) section.set("overflow", "hide");
        }
    }
}
