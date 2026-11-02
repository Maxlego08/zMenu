package fr.maxlego08.menu.hooks.dialogs.canvas.vanilla;

import fr.maxlego08.menu.common.canvas.TextureMetrics;
import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class VanillaAssetsTest {

    private static final Key ASCII = Key.key("minecraft", "font/ascii.png");

    @TempDir
    Path cache;

    private static byte[] png(int width, int height, int inkColumns) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < inkColumns; x++) image.setRGB(x, 0, 0xFFFFFFFF);
        return TextureMetrics.png(image);
    }

    @Test
    public void readsTheServerPackCopyOfATexture() {
        VanillaAssets assets = new VanillaAssets(this.cache, "test");
        assertTrue(assets.texture(ASCII).isEmpty());

        assets.useOverrides(path -> path.equals("assets/minecraft/textures/font/ascii.png") ? Optional.of(png(32, 16, 5)) : Optional.empty());
        BufferedImage image = assets.texture(ASCII).orElseThrow();
        assertEquals(32, image.getWidth());
        assertEquals(5, TextureMetrics.inkWidth(image, 0, 0, 32, 16));
    }

    @Test
    public void forgetsTexturesWhenTheOverridesChange() {
        VanillaAssets assets = new VanillaAssets(this.cache, "test");
        assets.useOverrides(path -> Optional.of(png(16, 16, 2)));
        assertEquals(16, assets.texture(ASCII).orElseThrow().getWidth());

        assets.useOverrides(path -> Optional.of(png(8, 8, 2)));
        assertEquals(8, assets.texture(ASCII).orElseThrow().getWidth());
    }

    @Test
    public void anOverriddenTextureOnlyUsesTheAnimationOfItsOwnPack() {
        Key texture = Key.key("minecraft", "block/magma.png");
        String meta = "assets/minecraft/textures/block/magma.png.mcmeta";
        Map<String, byte[]> withMeta = Map.of(
                "assets/minecraft/textures/block/magma.png", png(16, 48, 16),
                meta, "{\"animation\":{}}".getBytes(StandardCharsets.UTF_8));

        VanillaAssets assets = new VanillaAssets(this.cache, "test");
        assets.useOverrides(path -> Optional.ofNullable(withMeta.get(path)));
        assertTrue(assets.isAnimated(texture));

        assets.useOverrides(path -> path.equals(meta) ? Optional.empty() : Optional.ofNullable(withMeta.get(path)));
        assertFalse(assets.isAnimated(texture));
    }

    @Test
    public void readsTheServerPackCopyOfAFont() {
        VanillaAssets assets = new VanillaAssets(this.cache, "test");
        String json = "{\"providers\":[{\"type\":\"bitmap\",\"file\":\"minecraft:font/ascii.png\",\"chars\":[\"A\"]}]}";
        assets.useOverrides(path -> path.equals("assets/minecraft/font/include/default.json") ? Optional.of(json.getBytes(StandardCharsets.UTF_8)) : Optional.empty());
        assertEquals(1, assets.providers(Key.key("minecraft", "include/default")).size());
    }
}
