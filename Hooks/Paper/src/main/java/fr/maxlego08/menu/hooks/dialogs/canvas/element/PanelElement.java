package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Draws a texture of {@code resource-pack/textures} stretched to a size by nine-slice: its corners are
 * kept, its edges and middle are repeated.
 *
 * <pre>
 * background:
 *   type: panel
 *   texture: panels/dark      # resource-pack/textures/panels/dark.png
 *   border: 4                 # the size of its corners
 *   x: 0
 *   y: 0
 *   width: 320
 *   height: 180               # any size: over 256 pixels the panel is drawn as a grid of tiles
 * </pre>
 *
 * @param name    The name of the element.
 * @param texture The texture, without {@code .png}; may be a placeholder with {@code texture-values}, see {@link PackValue}.
 * @param border  The size of the corners.
 * @param x       The x of its left edge.
 * @param y       The y of its top edge.
 * @param width   Its width.
 * @param height  Its height.
 */
public record PanelElement(@NotNull String name, @NotNull PackValue texture, int border, @NotNull PositionValue x, @NotNull PositionValue y,
                           int width, int height) implements CanvasElement, CanvasLayoutable {

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.width;
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.height;
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        int minimum = Math.max(1, this.border * 2);
        return new PanelElement(this.name, this.texture, this.border, PositionValue.of(x), PositionValue.of(y), Math.clamp(width, minimum, 1024),
                Math.clamp(height, minimum, PanelElementLoader.MAX_HEIGHT));
    }

    private @NotNull String reference(@NotNull String texture) {
        return PanelGlyphProvider.reference(texture, this.width, this.height, this.border);
    }

    @Override
    public boolean isStatic() {
        return !this.texture.isPlaceholder() && !this.x.isPlaceholder() && !this.y.isPlaceholder();
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (String value : this.texture.values()) {
            ResolvedGlyph glyph = variants.resolve(this.reference(value));
            if (glyph == null) continue;
            for (int top : this.y.all()) variants.glyph(glyph, top);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        context.canvasManager().resolveGlyph(this.reference(this.texture.resolve(context))).ifPresent(glyph -> builder.glyph(glyph, this.x.resolve(context), this.y.resolve(context)));
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", "panel");
        this.texture.serialize(section, "texture");
        section.set("border", this.border);
        this.x.serialize(section, "x");
        this.y.serialize(section, "y");
        section.set("width", this.width);
        section.set("height", this.height);
    }
}
