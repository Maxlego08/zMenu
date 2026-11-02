package fr.maxlego08.menu.hooks.dialogs.canvas.glyph;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.hooks.dialogs.canvas.vanilla.VanillaAssets;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

/**
 * The textures of the vanilla client, referenced as {@code minecraft:<path>} without {@code .png}, such as
 * {@code minecraft:item/diamond}. The pack only points at the client's own copy.
 * <p>
 * An animated texture holds its frames stacked vertically: only its first frame is drawn.
 */
public final class MinecraftGlyphProvider implements GlyphProvider {

    private final VanillaAssets assets;

    public MinecraftGlyphProvider(@NotNull VanillaAssets assets) {
        this.assets = assets;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key(Key.MINECRAFT_NAMESPACE);
    }

    @Override
    public boolean isAvailable() {
        return this.assets.isReady();
    }

    @Override
    public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
        if (!Key.parseableValue(path)) return Optional.empty();
        Key texture = Key.key(Key.MINECRAFT_NAMESPACE, path + ".png");
        Optional<BufferedImage> image = this.assets.texture(texture);
        if (image.isEmpty()) return Optional.empty();

        BufferedImage value = image.get();
        int cellWidth = value.getWidth();
        int cellHeight = this.assets.isAnimated(texture) ? Math.min(value.getWidth(), value.getHeight()) : value.getHeight();
        int rows = Math.max(1, value.getHeight() / cellHeight);
        int ink = TextureMetrics.inkWidth(value, 0, 0, cellWidth, cellHeight);
        GlyphCell cell = new GlyphCell(texture, 1, rows, 0, 0, cellWidth, cellHeight, cellHeight, ink, null, null, 0);
        return Optional.of(new ResolvedGlyph(Key.MINECRAFT_NAMESPACE + ":" + path, List.of(cell), false));
    }

    @Override
    public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return this.assets.texture(texture);
    }
}
