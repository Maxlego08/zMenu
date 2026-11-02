package fr.maxlego08.menu.api.dialog.canvas;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Resolves the images of one source for canvases: zMenu's own textures, vanilla textures, or the glyphs of
 * a plugin such as Nexo, ItemsAdder or CraftEngine.
 * <p>
 * A reference is written {@code <provider>:<path>}: the provider is the value of {@link #key()}, the
 * path is what the provider understands, such as {@code menu_items} for Nexo or
 * {@code my_pack:coin} for ItemsAdder.
 */
public interface GlyphProvider extends Keyed {

    /**
     * @return True if the source of this provider is installed and ready.
     */
    boolean isAvailable();

    /**
     * Resolves a glyph. A provider returns an empty result for a glyph it does not know, and never
     * guesses.
     *
     * @param path The part of the reference after {@code <provider>:}.
     * @return The glyph, or an empty result if this provider does not know it.
     */
    @NotNull
    Optional<ResolvedGlyph> resolve(@NotNull String path);

    /**
     * Reads a texture this provider owns, to measure it or to copy it.
     *
     * @param texture The texture, as named in the resource pack.
     * @return The texture, or an empty result if it cannot be read.
     */
    @NotNull
    Optional<BufferedImage> readTexture(@NotNull Key texture);

    /**
     * Called when the canvas manager regenerates its resource pack, so the provider can drop what it
     * cached.
     */
    default void reload() {
    }
}
