package fr.maxlego08.menu.hooks.dialogs.canvas.check;

import com.google.gson.JsonArray;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ImageElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.PackValue;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.PositionValue;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.TextElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.CanvasTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.ChoiceWidget;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.DropdownWidget;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.ModalWidget;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.WidgetSettings;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CanvasCheckerTest {

    private static final GlyphCell CELL = new GlyphCell(Key.key("zmenu", "font/canvas/known.png"), 1, 1, 0, 0, 16, 16, 16, 16, null, null, 0);
    private static final ResolvedGlyph KNOWN = new ResolvedGlyph("zmenu:known", List.of(CELL), false);

    private static final CanvasTextFont FIXED = new CanvasTextFont() {
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
            return 6;
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

    private final List<String> forwarded = new ArrayList<>();

    private final CanvasVariants request = new CanvasVariants() {
        @Override
        public void glyph(@NotNull ResolvedGlyph glyph, int y) {
            CanvasCheckerTest.this.forwarded.add("glyph " + glyph.reference() + "@" + y);
        }

        @Override
        public void text(@NotNull String font, int y) {
            CanvasCheckerTest.this.forwarded.add("text " + font + "@" + y);
        }

        @Override
        public void area(int y, int width, int height) {
            CanvasCheckerTest.this.forwarded.add("area@" + y);
        }

        @Override
        public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
            if (reference.equals("zmenu:known")) return KNOWN;
            return reference.startsWith("zmenu:icon") ? new ResolvedGlyph(reference, List.of(CELL), false) : null;
        }
    };

    private static TextElement text(String name, String line, String font, int y, Integer width, TextElement.TextBox box) {
        return new TextElement(name, List.of(line), PackValue.of(font), PositionValue.of(0), PositionValue.of(y), width == null ? null : CanvasValue.of(width),
                CanvasValue.of(HAlign.LEFT), null, box);
    }

    private Map<String, List<String>> check(CanvasElement... elements) {
        CanvasChecker checker = new CanvasChecker(this.request, name -> name.equals("fixed") ? FIXED : null, Component::text);
        checker.check("shop", new DialogCanvasBody(CanvasValue.of(100), CanvasValue.of(20), List.of(elements), List.of()));
        return checker.problems();
    }

    @Test
    void aCorrectCanvasHasNoProblemAndEveryVariantIsPassedOn() {
        Map<String, List<String>> problems = this.check(new ImageElement("logo", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(2), null),
                text("title", "abc", "fixed", 10, 30, null));
        assertTrue(problems.isEmpty(), problems.toString());
        assertEquals(List.of("glyph zmenu:known@2", "text fixed@1"), this.forwarded);
    }

    @Test
    void anImageOneLineLowerSharesItsPackCopy() {
        this.check(new ImageElement("top", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(1), null),
                new ImageElement("middle", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(10), null),
                new ImageElement("bottom", PackValue.of("zmenu:known"), PositionValue.of(40), PositionValue.of(10), null));
        assertEquals(List.of("glyph zmenu:known@1", "glyph zmenu:known@1", "glyph zmenu:known@1"), this.forwarded);
    }

    @Test
    void anElementNeverMovesBelowWhatIsDrawnAfterIt() {
        this.check(new ImageElement("low", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(19), null),
                new ImageElement("high", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(2), null));
        assertEquals(List.of("glyph zmenu:known@19", "glyph zmenu:known@2"), this.forwarded);
    }

    @Test
    void reportsMissingGlyphsUnknownFontsAndThingsOutsideTheCanvas() {
        List<String> problems = this.check(new ImageElement("logo", PackValue.of("zmenu:missing"), PositionValue.of(0), PositionValue.of(0), null),
                new ImageElement("low", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(10), null),
                text("title", "abc", "nope", 0, null, null),
                text("footer", "abc", "fixed", 15, null, null)).get("shop");

        assertEquals(4, problems.size(), problems.toString());
        assertTrue(problems.get(0).startsWith("logo: the glyph zmenu:missing is not found"));
        assertTrue(problems.get(1).startsWith("low: the image zmenu:known at y 10 (16 pixels high) is outside the canvas"));
        assertTrue(problems.get(2).startsWith("title: the font nope does not exist"));
        assertTrue(problems.get(3).startsWith("footer: a text line at y 15 is outside the canvas"));
    }

    @Test
    void warnsWhenOneElementAsksForTooManyVariants() {
        List<String> few = new ArrayList<>();
        List<String> many = new ArrayList<>();
        for (int index = 0; index < 300; index++) {
            if (index < 10) few.add("zmenu:icon" + index);
            many.add("zmenu:icon" + index);
        }
        PackValue small = new PackValue("zmenu:icon%n%", few, few.getFirst(), few, null, UnaryOperator.identity());
        PackValue big = new PackValue("zmenu:icon%n%", many, many.getFirst(), many, null, UnaryOperator.identity());

        Map<String, List<String>> problems = this.check(new ImageElement("small", small, PositionValue.of(0), PositionValue.of(0), null),
                new ImageElement("big", big, PositionValue.of(0), PositionValue.of(0), null));

        assertEquals(1, problems.get("shop").size(), problems.toString());
        assertTrue(problems.get("shop").getFirst().startsWith("big: the pack holds it 300 times"));
    }

    @Test
    void countsTheVariantsOfEachDialog() {
        CanvasChecker checker = new CanvasChecker(this.request, name -> name.equals("fixed") ? FIXED : null, Component::text);
        checker.check("shop", new DialogCanvasBody(CanvasValue.of(100), CanvasValue.of(20), List.of(
                new ImageElement("logo", PackValue.of("zmenu:known"), PositionValue.of(0), PositionValue.of(2), null),
                text("title", "abc", "fixed", 10, 30, null)), List.of()));
        checker.check("profile", new DialogCanvasBody(CanvasValue.of(100), CanvasValue.of(20), List.of(
                text("name", "abc", "fixed", 0, null, null)), List.of()));

        assertEquals(Map.of("shop", 2, "profile", 1), checker.variantCounts());
    }

    @Test
    void reportsWidgetsPastTheCanvasRowsTooCloseAndTextInsideWindows() {
        WidgetSettings dropdownSettings = new WidgetSettings("lang", "lang", "", CanvasStateService.Keep.OPEN, 0, 0, null, List.of(), true, List.of());
        DropdownWidget dropdown = new DropdownWidget(dropdownSettings, List.of(new DropdownWidget.Option("a", "A"), new DropdownWidget.Option("b", "B")),
                60, 14, "widgets/dropdown", "widgets/dropdown", null, 3, null, 12, 5, "«", "»", "fixed");
        WidgetSettings radioSettings = new WidgetSettings("quality", "quality", "", CanvasStateService.Keep.OPEN, 70, 0, null, List.of(), true, List.of());
        ChoiceWidget radio = new ChoiceWidget(radioSettings, ChoiceWidget.Style.RADIO, List.of(new ChoiceWidget.Option("low", "Low", 20),
                new ChoiceWidget.Option("high", "High", 20)), true, 0, 8, "zmenu:known", "zmenu:known", 0, "fixed", Map.of());
        WidgetSettings modalSettings = new WidgetSettings("confirm", "confirm", "false", CanvasStateService.Keep.OPEN, 0, 0, null, List.of(), true, List.of());
        ModalWidget modal = new ModalWidget(modalSettings, 90, 20, "widgets/modal", 2, null, true, List.of(text("question", "This question is too long", "fixed", 2, 30, null)));

        List<String> problems = this.check(dropdown, radio, modal).get("shop");

        assertTrue(problems.contains("lang: the open list goes past the bottom of the canvas (to y 44, the canvas is 20 high)"), String.join("\n", problems));
        assertTrue(problems.stream().anyMatch(problem -> problem.startsWith("quality: its options are 8 pixels apart")), String.join("\n", problems));
        assertTrue(problems.stream().anyMatch(problem -> problem.startsWith("confirm: the text (question)")), String.join("\n", problems));
    }

    @Test
    void reportsFixedTextThatDoesNotFitItsBox() {
        List<String> problems = this.check(text("name", "abcdefgh", "fixed", 0, 30, null),
                text("description", "aaa bbb ccc", "fixed", 0, 30, new TextElement.TextBox(1, TextElement.Wrap.WORD, TextElement.VerticalAlign.TOP, true)),
                text("placeholder", "%player_name% is very long", "fixed", 0, 30, null)).get("shop");

        assertEquals(2, problems.size(), problems.toString());
        assertTrue(problems.get(0).startsWith("name: the text \"abcdefgh\" is 48 pixels wide but its width is 30"));
        assertTrue(problems.get(1).startsWith("description: the text needs 3 lines but max-lines is 1"));
    }
}
