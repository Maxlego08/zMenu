package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * Draws a glyph: a zMenu texture, a vanilla texture, or a glyph of Nexo, ItemsAdder or CraftEngine.
 *
 * <pre>
 * coin:
 *   type: image
 *   glyph: itemsadder:my_pack:coin
 *   x: 12
 *   y: 30        # optional: without it, the glyph is drawn where its plugin draws it
 *   height: 16   # optional: draws the glyph scaled to this height
 * </pre>
 * {@code x} and {@code y} accept placeholders, see {@link PositionValue}. {@code glyph} accepts a placeholder
 * with the list of glyphs it can give:
 * <pre>
 * rank:
 *   type: image
 *   glyph: "ranks/%luckperms_primary_group_name%"
 *   glyph-values: [default, vip, mvp]
 *   x: 4
 *   y: 4
 * </pre>
 * In a {@code row} or {@code column}, the size of the default glyph places the element.
 *
 * @param name   The name of the element.
 * @param glyph  The glyph reference, which may be a placeholder with {@code glyph-values}, see {@link PackValue}.
 * @param x      The x of its left edge.
 * @param y      The y of its top edge, or null to draw it where its plugin draws it.
 * @param height The height to draw it at, or null for its own height.
 */
public record ImageElement(@NotNull String name, @NotNull PackValue glyph, @NotNull PositionValue x, @Nullable PositionValue y,
                           @Nullable Integer height) implements CanvasElement, CanvasLayoutable {

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return glyphs.apply(this.glyph.fallback()).map(this::scaled).map(ResolvedGlyph::visualWidth).orElse(0);
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return glyphs.apply(this.glyph.fallback()).map(this::scaled).map(ResolvedGlyph::height).orElse(0);
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new ImageElement(this.name, this.glyph, PositionValue.of(x), PositionValue.of(y), this.height);
    }

    @Override
    public boolean isStatic() {
        return !this.glyph.isPlaceholder() && !this.x.isPlaceholder() && (this.y == null || !this.y.isPlaceholder());
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (String reference : this.glyph.values()) {
            ResolvedGlyph resolved = variants.resolve(reference);
            if (resolved == null) continue;
            ResolvedGlyph scaled = this.scaled(resolved);
            if (this.y == null) {
                variants.glyph(scaled, scaled.hasNativeGlyph() ? scaled.nativeY() : 0);
                continue;
            }
            for (int value : this.y.all()) variants.glyph(scaled, value);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        Optional<ResolvedGlyph> resolved = context.canvasManager().resolveGlyph(this.glyph.resolve(context));
        resolved.ifPresent(value -> builder.glyph(this.scaled(value), this.x.resolve(context), this.y == null ? Integer.MIN_VALUE : this.y.resolve(context)));
    }

    private @NotNull ResolvedGlyph scaled(@NotNull ResolvedGlyph glyph) {
        return this.height == null ? glyph : glyph.withHeight(this.height);
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "image");
        this.glyph.serialize(section, "glyph");
        this.x.serialize(section, "x");
        if (this.y != null) this.y.serialize(section, "y");
        if (this.height != null) section.set("height", this.height);
    }
}
