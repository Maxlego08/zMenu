package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.GlyphProvider;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.hooks.dialogs.canvas.CanvasTestSupport;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextFonts;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.VanillaTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.vanilla.VanillaAssets;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CanvasPackGeneratorTest {

    private static final Gson GSON = new Gson();

    @TempDir
    Path folder;

    @BeforeAll
    static void logger() {
        CanvasTestSupport.installLogger();
    }

    private static GlyphProvider provider(int height) {
        BufferedImage image = new BufferedImage(8, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF);
        return new GlyphProvider() {
            @Override
            public @NotNull Key key() {
                return Key.key("zmenu", "zmenu");
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public @NotNull Optional<ResolvedGlyph> resolve(@NotNull String path) {
                GlyphCell cell = new GlyphCell(Key.key("zmenu", "font/canvas/" + path + "_s0.png"), 1, 1, 0, 0, 8, height, height, 8, null, null, 0);
                return Optional.of(new ResolvedGlyph("zmenu:" + path, List.of(cell), false));
            }

            @Override
            public @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
                return Optional.of(image);
            }
        };
    }

    private CanvasPackGenerator.Result generate(GlyphProvider provider, String reference, int y) throws IOException {
        CanvasRequest request = new CanvasRequest(provider::resolve, ignored -> Optional.of(provider));
        ResolvedGlyph glyph = request.resolve(reference.substring(reference.indexOf(':') + 1));
        assertNotNull(glyph);
        request.glyph(glyph, y);
        TextFonts fonts = new TextFonts(new VanillaTextFont(new VanillaAssets(this.folder, "test")));
        return new CanvasPackGenerator(fonts).generate(request, CodepointLock.load(this.folder.resolve("codepoints.lock")));
    }

    private static JsonObject json(CanvasPackGenerator.Result result, String path) {
        byte[] bytes = result.files().get(path);
        assertNotNull(bytes, path + " was not generated");
        return GSON.fromJson(new String(bytes, StandardCharsets.UTF_8), JsonObject.class);
    }

    @Test
    public void aGlyphGetsAFontPerVerticalPosition() throws IOException {
        CanvasPackGenerator.Result result = this.generate(provider(8), "zmenu:icon", 5);

        JsonObject font = json(result, "assets/zmenu/font/canvas/y5.json");
        JsonObject bitmap = font.getAsJsonArray("providers").get(1).getAsJsonObject();
        assertEquals("zmenu:font/canvas/icon_s0.png", bitmap.get("file").getAsString());
        assertEquals(2, bitmap.get("ascent").getAsInt());
        assertEquals(8, bitmap.get("height").getAsInt());
        assertTrue(result.files().containsKey("assets/zmenu/textures/font/canvas/icon_s0.png"));
        assertTrue(result.files().containsKey("assets/zmenu/font/space.json"));
        assertEquals(1, result.layout().glyphs().size());
    }

    @Test
    public void aSmallGlyphNearTheTopIsPadded() throws IOException {
        CanvasPackGenerator.Result result = this.generate(provider(4), "zmenu:dot", 0);

        JsonObject bitmap = json(result, "assets/zmenu/font/canvas/y0.json").getAsJsonArray("providers").get(1).getAsJsonObject();
        assertEquals(7, bitmap.get("ascent").getAsInt());
        assertTrue(bitmap.get("height").getAsInt() >= 7);
        assertTrue(bitmap.get("file").getAsString().startsWith("zmenu:font/canvas/padded/"));
    }

    @Test
    public void anAreaGetsAnInvisibleTextureOfItsSize() throws IOException {
        CanvasRequest request = new CanvasRequest(reference -> Optional.empty(), reference -> Optional.empty());
        request.area(20, 64, 18);
        TextFonts fonts = new TextFonts(new VanillaTextFont(new VanillaAssets(this.folder, "test")));
        CanvasPackGenerator.Result result = new CanvasPackGenerator(fonts).generate(request, CodepointLock.load(this.folder.resolve("codepoints.lock")));

        JsonObject bitmap = json(result, "assets/zmenu/font/canvas/y20.json").getAsJsonArray("providers").get(1).getAsJsonObject();
        assertEquals("zmenu:font/canvas/hitbox/64x18.png", bitmap.get("file").getAsString());
        assertEquals(-13, bitmap.get("ascent").getAsInt());

        BufferedImage texture = TextureMetrics.read(result.files().get("assets/zmenu/textures/font/canvas/hitbox/64x18.png"));
        assertNotNull(texture);
        assertEquals(64, TextureMetrics.inkWidth(texture, 0, 0, 64, 18));
        assertEquals(1, texture.getRGB(63, 0) >>> 24);
    }

    @Test
    public void theSameRequestGivesTheSameFiles() throws IOException {
        String first = PackOutput.contentHash(this.generate(provider(8), "zmenu:icon", 5).files());
        String second = PackOutput.contentHash(this.generate(provider(8), "zmenu:icon", 5).files());
        assertEquals(first, second);
    }
}
