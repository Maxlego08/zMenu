package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Everything the canvases of the loaded dialogs need in the pack, collected from their elements.
 */
public final class CanvasRequest implements CanvasVariants {

    private final Function<String, Optional<ResolvedGlyph>> resolver;
    private final Function<String, Optional<GlyphProvider>> providers;
    private final Set<GlyphVariant> glyphs = new LinkedHashSet<>();
    private final Set<String> texts = new LinkedHashSet<>();
    private final Map<Key, GlyphProvider> textureOwners = new HashMap<>();
    private final Map<String, Optional<ResolvedGlyph>> resolved = new HashMap<>();

    public CanvasRequest(@NotNull Function<String, Optional<ResolvedGlyph>> resolver, @NotNull Function<String, Optional<GlyphProvider>> providers) {
        this.resolver = resolver;
        this.providers = providers;
    }

    @Override
    public void glyph(@NotNull ResolvedGlyph glyph, int y) {
        if (glyph.hasNativeGlyph() && y == glyph.nativeY()) return;
        if (glyph.animated()) {
            Logger.info("The glyph " + glyph.reference() + " is animated, a canvas can only draw it where its plugin draws it (y " + glyph.nativeY() + ")", Logger.LogType.WARNING);
            return;
        }
        GlyphProvider provider = this.providers.apply(glyph.reference()).orElse(null);
        List<List<GlyphCell>> rows = glyph.rows();
        for (int row = 0; row < rows.size(); row++) {
            for (GlyphCell cell : rows.get(row)) {
                this.glyphs.add(new GlyphVariant(cell, y + row * glyph.rowHeight()));
                if (provider != null) this.textureOwners.putIfAbsent(cell.texture(), provider);
            }
        }
    }

    @Override
    public void text(@NotNull String font, int y) {
        this.texts.add(CanvasLayout.textKey(font, y));
    }

    @Override
    public void area(int y, int width, int height) {
        if (width < 1 || height < 1) return;
        for (GlyphCell cell : HitboxGlyphs.cells(width, height)) {
            this.glyphs.add(new GlyphVariant(cell, y));
            this.textureOwners.putIfAbsent(cell.texture(), HitboxGlyphs.INSTANCE);
        }
    }

    @Override
    public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
        return this.resolved.computeIfAbsent(reference, this.resolver).orElse(null);
    }

    /**
     * @return The image cell variants to generate.
     */
    public @NotNull Set<GlyphVariant> glyphs() {
        return this.glyphs;
    }

    /**
     * @return The text fonts to generate, as {@code font@y}.
     */
    public @NotNull Set<String> texts() {
        return this.texts;
    }

    /**
     * @param texture A texture used by a requested cell.
     * @return The provider that resolved it, to read it.
     */
    public @Nullable GlyphProvider owner(@NotNull Key texture) {
        return this.textureOwners.get(texture);
    }
}
