package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.api.exceptions.DialogException;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

public class PackValueTest {

    private static PackValue read(String yaml, String key, UnaryOperator<String> normalize) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(yaml);
        return PackValue.read(configuration, key, null, normalize, "rank", "test.yml");
    }

    @Test
    public void aSinglePlaceholderTakesShortOrFullValues() {
        assertEquals("ranks/vip", PackValue.expand("ranks/%rank%", "vip"));
        assertEquals("ranks/vip", PackValue.expand("ranks/%rank%", "ranks/vip"));
        assertEquals("ranks/vip_icon", PackValue.expand("ranks/%rank%_icon", "vip"));
        assertEquals("ranks/vip_icon", PackValue.expand("ranks/%rank%_icon", "ranks/vip_icon"));
        assertEquals("vip", PackValue.expand("%rank%", "vip"));
        assertEquals("a/b", PackValue.expand("%x%/%y%", "a/b"));
    }

    @Test
    public void readsTheValuesAndTheDefault() throws Exception {
        PackValue glyph = read("glyph: \"ranks/%rank%\"\nglyph-values: [default, ranks/vip, mvp, vip]\nglyph-default: mvp\n", "glyph", UnaryOperator.identity());

        assertTrue(glyph.isPlaceholder());
        assertEquals(List.of("ranks/default", "ranks/vip", "ranks/mvp"), glyph.values());
        assertEquals("ranks/mvp", glyph.fallback());

        YamlConfiguration written = new YamlConfiguration();
        glyph.serialize(written, "glyph");
        assertEquals("ranks/%rank%", written.getString("glyph"));
        assertEquals(List.of("default", "ranks/vip", "mvp", "vip"), written.getStringList("glyph-values"));
        assertEquals("mvp", written.getString("glyph-default"));
    }

    @Test
    public void theFirstValueIsTheDefaultAndAFixedValueNeedsNoList() throws Exception {
        assertEquals("ranks/default", read("glyph: \"ranks/%rank%\"\nglyph-values: [default, vip]\n", "glyph", UnaryOperator.identity()).fallback());

        PackValue fixed = read("glyph: ranks/vip\n", "glyph", UnaryOperator.identity());
        assertFalse(fixed.isPlaceholder());
        assertEquals(List.of("ranks/vip"), fixed.values());
    }

    @Test
    public void rejectsMissingListsUnknownDefaultsAndInvalidValues() {
        assertThrows(DialogException.class, () -> read("glyph: \"ranks/%rank%\"\n", "glyph", UnaryOperator.identity()));
        assertThrows(DialogException.class, () -> read("glyph: ranks/vip\nglyph-values: [a]\n", "glyph", UnaryOperator.identity()));
        assertThrows(DialogException.class, () -> read("glyph: \"ranks/%rank%\"\nglyph-values: [a]\nglyph-default: b\n", "glyph", UnaryOperator.identity()));
        assertThrows(DialogException.class, () -> read("texture: \"panels/%theme%\"\ntexture-values: [dark, \"../secret\"]\n", "texture", ElementSections::texture));
        assertEquals(List.of("panels/dark"), assertDoesNotThrowRead("texture: \"zmenu:panels/%theme%\"\ntexture-values: [dark]\n").values());
    }

    private static PackValue assertDoesNotThrowRead(String yaml) {
        try {
            return read(yaml, "texture", ElementSections::texture);
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    @Test
    public void everyValueOfAnImageIsInThePack() {
        GlyphCell cell = new GlyphCell(Key.key("zmenu", "font/canvas/x.png"), 1, 1, 0, 0, 8, 8, 8, 8, null, null, 0);
        PackValue glyph = new PackValue("ranks/%rank%", List.of("ranks/vip", "ranks/mvp"), "ranks/vip", List.of("vip", "mvp"), null, UnaryOperator.identity());
        ImageElement image = new ImageElement("rank", glyph, PositionValue.of(0), PositionValue.of(4), null);

        List<String> drawn = new ArrayList<>();
        image.collectVariants(new CanvasVariants() {
            @Override
            public void glyph(@NotNull ResolvedGlyph resolved, int y) {
                drawn.add(resolved.reference() + "@" + y);
            }

            @Override
            public void text(@NotNull String font, int y) {
            }

            @Override
            public void area(int y, int width, int height) {
            }

            @Override
            public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
                return new ResolvedGlyph(reference, List.of(cell), false);
            }
        });

        assertEquals(List.of("ranks/vip@4", "ranks/mvp@4"), drawn);
        assertFalse(image.isStatic());
    }
}
