package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * A row of stars: the value is the number of stars filled, from 0 to {@code max}, and a click on a star
 * fills the stars up to it.
 *
 * <pre>
 * stars:
 *   type: rating
 *   default: "3"
 *   x: 10
 *   y: 20
 *   max: 5                       # the number of stars (default 5)
 *   gap: 1
 *   glyph-on: widgets/star_on
 *   glyph-off: widgets/star_off
 *   clear: true                  # a click on the last filled star empties them all (default true)
 *   star-hover: ["&lt;gray&gt;%value% stars"]   # optional, the tooltip of each star, %value% its number
 *   stars:                       # optional, each star where you want it (relative to the widget), instead of a row
 *     - { x: 0, y: 8 }
 *     - { x: 12, y: 2 }
 *     - { x: 24, y: 0 }
 * </pre>
 * With {@code stars}, {@code max} is the number of stars listed. Plus the keys of {@link WidgetSettings}.
 */
public final class RatingWidget extends WidgetElement {

    public static final String TYPE = "rating";

    private final int max;
    private final int gap;
    private final String glyphOn;
    private final String glyphOff;
    private final boolean clear;
    private final List<String> starHover;
    private final List<int[]> stars;

    public RatingWidget(@NotNull WidgetSettings settings, int max, int gap, @NotNull String glyphOn, @NotNull String glyphOff, boolean clear) {
        this(settings, max, gap, glyphOn, glyphOff, clear, List.of(), List.of());
    }

    public RatingWidget(@NotNull WidgetSettings settings, int max, int gap, @NotNull String glyphOn, @NotNull String glyphOff, boolean clear,
                        @NotNull List<String> starHover, @NotNull List<int[]> stars) {
        super(settings);
        this.max = stars.isEmpty() ? max : stars.size();
        this.gap = gap;
        this.glyphOn = glyphOn;
        this.glyphOff = glyphOff;
        this.clear = clear;
        this.starHover = List.copyOf(starHover);
        this.stars = stars.stream().map(int[]::clone).toList();
    }

    /**
     * @param value A value.
     * @return The stars it fills, from 0 to max.
     */
    public int filled(@NotNull String value) {
        try {
            return Math.clamp((long) Math.floor(Double.parseDouble(value.trim())), 0, this.max);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    /**
     * @param filled The stars filled.
     * @param star   The star clicked, from 1.
     * @return The value after the click.
     */
    public int clicked(int filled, int star) {
        return this.clear && star == filled ? 0 : star;
    }

    private int[] size(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        int width = 0;
        int height = 0;
        for (String reference : new String[]{this.glyphOn, this.glyphOff}) {
            Optional<ResolvedGlyph> glyph = glyphs.apply(reference);
            if (glyph.isEmpty()) continue;
            width = Math.max(width, glyph.get().visualWidth());
            height = Math.max(height, glyph.get().height());
        }
        return new int[]{Math.max(width, 1), Math.max(height, 1)};
    }

    /**
     * @param star   A star, from 1.
     * @param width  The width of a star.
     * @return Its x and y, relative to the widget.
     */
    public int @NotNull [] position(int star, int width) {
        if (!this.stars.isEmpty()) return this.stars.get(star - 1);
        return new int[]{(star - 1) * (width + this.gap), 0};
    }

    /**
     * @return The click width of a star: with the gap in a row, the glyph alone when placed one by one.
     */
    private int clickWidth(int width) {
        return this.stars.isEmpty() ? width + this.gap : width;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int[] size = this.size(reference -> Optional.ofNullable(variants.resolve(reference)));
        Set<Integer> ys = new TreeSet<>();
        for (int star = 1; star <= this.max; star++) ys.add(this.settings.y() + this.position(star, size[0])[1]);
        for (int y : ys) {
            this.glyph(variants, this.glyphOn, y);
            this.glyph(variants, this.glyphOff, y);
            variants.area(y, this.clickWidth(size[0]), size[1]);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        int filled = this.filled(value);
        int[] size = this.size(context.canvasManager()::resolveGlyph);
        for (int star = 1; star <= this.max; star++) {
            int[] position = this.position(star, size[0]);
            int x = this.settings.x() + position[0];
            int y = this.settings.y() + position[1];
            this.glyph(context, builder, star <= filled ? this.glyphOn : this.glyphOff, x, y);
            builder.area(x, y, this.clickWidth(size[0]), size[1], this.change(context, String.valueOf(this.clicked(filled, star))),
                    this.starHover.isEmpty() ? this.hover(context, value) : this.hover(context, String.valueOf(star), this.starHover));
        }
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "0");
        if (this.stars.isEmpty() && this.max != 5) section.set("max", this.max);
        if (this.gap != 1) section.set("gap", this.gap);
        section.set("glyph-on", this.glyphOn);
        section.set("glyph-off", this.glyphOff);
        if (!this.clear) section.set("clear", false);
        if (!this.starHover.isEmpty()) section.set("star-hover", this.starHover);
        if (!this.stars.isEmpty()) {
            List<Map<String, Integer>> list = new ArrayList<>();
            for (int[] star : this.stars) {
                Map<String, Integer> position = new LinkedHashMap<>();
                position.put("x", star[0]);
                position.put("y", star[1]);
                list.add(position);
            }
            section.set("stars", list);
        }
    }
}
