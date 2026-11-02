package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import com.google.gson.JsonArray;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class TextMeasurerTest {

    private static final CanvasTextFont FONT = new CanvasTextFont() {
        @Override
        public @NotNull String name() {
            return "fixed";
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public double advance(int codepoint) {
            return codepoint == '.' ? 2 : 6;
        }

        @Override
        public int lineHeight() {
            return 9;
        }

        @Override
        public @NotNull JsonArray providers(int y) {
            return new JsonArray();
        }

        @Override
        public @NotNull SortedMap<String, byte[]> files() {
            return new TreeMap<>();
        }
    };

    @Test
    public void measuresEveryCharacter() {
        assertEquals(18, TextMeasurer.measure(Component.text("abc"), FONT));
    }

    @Test
    public void boldAddsOnePixelPerCharacterInheritedByChildren() {
        Component text = Component.text("ab", NamedTextColor.RED).decorate(TextDecoration.BOLD).append(Component.text("c"));
        assertEquals(21, TextMeasurer.measure(text, FONT));
    }

    @Test
    public void fittingTextIsKept() {
        Component text = Component.text("abc");
        assertSame(text, TextMeasurer.fit(text, FONT, 18));
    }

    private static List<String> plain(List<Component> lines) {
        return lines.stream().map(PlainTextComponentSerializer.plainText()::serialize).toList();
    }

    @Test
    public void wordsWrapAtTheLastSpace() {
        List<Component> lines = TextMeasurer.wrap(Component.text("aa bb cc dd"), FONT, 30, true, 10, true);
        assertEquals(List.of("aa bb", "cc dd"), plain(lines));
    }

    @Test
    public void aWordWiderThanTheBoxIsBroken() {
        assertEquals(List.of("abcde", "fgh"), plain(TextMeasurer.wrap(Component.text("abcdefgh"), FONT, 30, true, 10, true)));
        assertEquals(List.of("ab", "abcde", "fg"), plain(TextMeasurer.wrap(Component.text("ab abcdefg"), FONT, 30, true, 10, true)));
    }

    @Test
    public void characterModeBreaksAnywhere() {
        assertEquals(List.of("aa bb", "cc"), plain(TextMeasurer.wrap(Component.text("aa bbcc"), FONT, 30, false, 10, true)));
    }

    @Test
    public void lineFeedsAlwaysBreak() {
        assertEquals(List.of("a", "b"), plain(TextMeasurer.wrap(Component.text("a\nb"), FONT, 100, true, 10, true)));
    }

    @Test
    public void droppedLinesEndTheLastOneWithAnEllipsis() {
        List<Component> lines = TextMeasurer.wrap(Component.text("aa bb cc dd ee ff"), FONT, 30, true, 2, true);
        assertEquals(2, lines.size());
        assertTrue(plain(lines).get(1).endsWith("..."));
        assertTrue(TextMeasurer.measure(lines.get(1), FONT) <= 30);
        assertEquals(List.of("aa bb", "cc dd"), plain(TextMeasurer.wrap(Component.text("aa bb cc dd ee ff"), FONT, 30, true, 2, false)));
    }

    @Test
    public void stylesCarryOnToTheNextLine() {
        Component text = Component.text("aa ", NamedTextColor.RED).append(Component.text("bb cc", NamedTextColor.BLUE));
        List<Component> lines = TextMeasurer.wrap(text, FONT, 30, true, 10, true);
        assertEquals(List.of("aa bb", "cc"), plain(lines));
        assertEquals(NamedTextColor.BLUE, lines.get(1).children().getFirst().color());
    }

    @Test
    public void longTextIsCutWithAnEllipsis() {
        Component fitted = TextMeasurer.fit(Component.text("abcdefgh"), FONT, 30);
        String plain = PlainTextComponentSerializer.plainText().serialize(fitted);
        assertEquals("abcd...", plain);
        assertTrue(TextMeasurer.measure(fitted, FONT) <= 30);
    }
}
