package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.ProgressGlyphProvider;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Draws a progress bar: the filled part of a texture over an optional background glyph.
 *
 * <pre>
 * health:
 *   type: progress
 *   texture: bars/health_full     # resource-pack/textures/bars/health_full.png
 *   background: bars/health_empty # optional glyph drawn under it
 *   value: "%player_health%"
 *   max: 20                       # a number or a placeholder
 *   steps: 20                     # optional, the number of fill levels; the texture width by default
 *   x: 10
 *   y: 40
 * </pre>
 * The pack holds one texture per step, so a bar of 100 steps is 100 small textures.
 *
 * @param name       The name of the element.
 * @param texture    The texture of the full bar, without {@code .png}; may be a placeholder with
 *                   {@code texture-values}, see {@link PackValue}.
 * @param background The glyph drawn under the bar, or null; may be a placeholder with {@code background-values}.
 * @param value      The value, a number or a placeholder.
 * @param max        The value of a full bar, a number or a placeholder.
 * @param steps      The number of fill levels, or 0 for one per pixel.
 * @param x          The x of its left edge.
 * @param y          The y of its top edge.
 */
public record ProgressElement(@NotNull String name, @NotNull PackValue texture, @Nullable PackValue background, @NotNull String value, @NotNull String max,
                             int steps, @NotNull PositionValue x, @NotNull PositionValue y) implements CanvasElement, CanvasLayoutable {

    private static final Set<String> REPORTED = ConcurrentHashMap.newKeySet();

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return glyphs.apply(ProgressGlyphProvider.reference(this.texture.fallback(), Integer.MAX_VALUE)).map(ResolvedGlyph::visualWidth).orElse(0);
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return glyphs.apply(ProgressGlyphProvider.reference(this.texture.fallback(), Integer.MAX_VALUE)).map(ResolvedGlyph::height).orElse(0);
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new ProgressElement(this.name, this.texture, this.background, this.value, this.max, this.steps, PositionValue.of(x), PositionValue.of(y));
    }

    private int width(@Nullable ResolvedGlyph full) {
        if (full == null) return 0;
        return full.cells().stream().mapToInt(GlyphCell::cellWidth).sum();
    }

    private int levels(int width) {
        return this.steps > 0 ? Math.min(this.steps, width) : width;
    }

    private static int filled(int width, int levels, int level) {
        return (int) Math.round(width * (double) level / levels);
    }

    @Override
    public boolean isStatic() {
        return !this.texture.isPlaceholder() && (this.background == null || !this.background.isPlaceholder()) && !this.x.isPlaceholder()
                && !this.y.isPlaceholder() && this.value.indexOf('%') < 0 && this.max.indexOf('%') < 0;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        if (this.background != null) {
            for (String reference : this.background.values()) {
                ResolvedGlyph backgroundGlyph = variants.resolve(reference);
                if (backgroundGlyph == null) continue;
                for (int top : this.y.all()) variants.glyph(backgroundGlyph, top);
            }
        }
        for (String texture : this.texture.values()) {
            int width = this.width(variants.resolve(ProgressGlyphProvider.reference(texture, Integer.MAX_VALUE)));
            if (width == 0) continue;
            int levels = this.levels(width);
            for (int top : this.y.all()) {
                for (int level = 1; level <= levels; level++) {
                    ResolvedGlyph bar = variants.resolve(ProgressGlyphProvider.reference(texture, filled(width, levels, level)));
                    if (bar != null) variants.glyph(bar, top);
                }
            }
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        int left = this.x.resolve(context);
        int top = this.y.resolve(context);
        if (this.background != null) {
            context.canvasManager().resolveGlyph(this.background.resolve(context)).ifPresent(glyph -> builder.glyph(glyph, left, top));
        }

        String texture = this.texture.resolve(context);
        int width = this.width(context.canvasManager().resolveGlyph(ProgressGlyphProvider.reference(texture, Integer.MAX_VALUE)).orElse(null));
        if (width == 0) return;
        double current = this.number(context, this.value);
        double maximum = this.number(context, this.max);
        if (maximum <= 0) return;
        int levels = this.levels(width);
        int level = (int) Math.round(Math.clamp(current / maximum, 0, 1) * levels);
        if (level == 0) return;
        context.canvasManager().resolveGlyph(ProgressGlyphProvider.reference(texture, filled(width, levels, level)))
                .ifPresent(glyph -> builder.glyph(glyph, left, top));
    }

    private double number(@NotNull CanvasRenderContext context, @NotNull String raw) {
        String parsed = context.resolve(raw).trim();
        try {
            return Double.parseDouble(parsed);
        } catch (NumberFormatException exception) {
            if (REPORTED.add(raw + "=" + parsed)) {
                Logger.info("The progress bar " + this.name + " read " + parsed + " from " + raw + ", which is not a number", Logger.LogType.WARNING);
            }
            return 0;
        }
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "progress");
        this.texture.serialize(section, "texture");
        if (this.background != null) this.background.serialize(section, "background");
        section.set("value", this.value);
        section.set("max", this.max);
        if (this.steps > 0) section.set("steps", this.steps);
        this.x.serialize(section, "x");
        this.y.serialize(section, "y");
    }
}
