package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.PackValue;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.PositionValue;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.TextElement;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WidgetTest {

    private static WidgetSettings settings(String name, int x, int y) {
        return new WidgetSettings(name, name, "", CanvasStateService.Keep.OPEN, x, y, null, List.of(), true, List.of());
    }

    private static List<String> variants(CanvasElement element) {
        return variants(element, 0, 0);
    }

    private static List<String> variants(CanvasElement element, int canvasWidth, int canvasHeight) {
        GlyphCell cell = new GlyphCell(Key.key("zmenu", "font/canvas/x.png"), 1, 1, 0, 0, 10, 10, 10, 10, null, null, 0);
        List<String> found = new ArrayList<>();
        element.collectVariants(new CanvasVariants() {
            @Override
            public int canvasWidth() {
                return canvasWidth;
            }

            @Override
            public int canvasHeight() {
                return canvasHeight;
            }

            @Override
            public void glyph(@NotNull ResolvedGlyph glyph, int y) {
                found.add("glyph " + glyph.reference() + "@" + y);
            }

            @Override
            public void text(@NotNull String font, int y) {
                found.add("text@" + y);
            }

            @Override
            public void area(int y, int width, int height) {
                found.add("area " + width + "x" + height + "@" + y);
            }

            @Override
            public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
                return new ResolvedGlyph(reference, List.of(cell), false);
            }
        });
        return found;
    }

    @Test
    public void aToggleIsOnForTrueYesOrOne() {
        assertTrue(ToggleWidget.isOn("TRUE"));
        assertTrue(ToggleWidget.isOn("1"));
        assertFalse(ToggleWidget.isOn("false"));
        assertFalse(ToggleWidget.isOn(""));

        ToggleWidget toggle = new ToggleWidget(settings("music", 0, 20), "on", "off", "Music", "default", 70);
        assertEquals(List.of("glyph on@20", "glyph off@20", "text@21", "area 70x10@20"), variants(toggle));
    }

    @Test
    public void aStepperStaysBetweenItsLimits() {
        StepperWidget stepper = new StepperWidget(settings("amount", 0, 0), 2, 32, 2, 60, "minus", "plus", null, null, "%value%", "default");
        assertEquals(2, stepper.current("abc"));
        assertEquals(32, stepper.current("50"));
        assertEquals(10, stepper.current("10"));
    }

    @Test
    public void aSliderSnapsToItsStepsAndSplitsItsTrack() {
        SliderWidget slider = new SliderWidget(settings("volume", 0, 0), 0, 100, 5, 150, "widgets/track", 6, 2, "knob", 21, null, "default");
        assertEquals(0, slider.valueOf(0));
        assertEquals(50, slider.valueOf(10));
        assertEquals(100, slider.valueOf(20));
        assertEquals(55, slider.current("53"));
        assertEquals(0, slider.areaStart(0));
        assertEquals(150, slider.areaStart(21));

        List<String> areas = variants(slider).stream().filter(variant -> variant.startsWith("area")).distinct().toList();
        assertTrue(areas.size() <= 2, areas.toString());
    }

    @Test
    public void aDropdownPagesItsOptionsAndHoldsItsOpenListInThePack() {
        List<DropdownWidget.Option> options = new ArrayList<>();
        for (String id : List.of("en", "fr", "de", "es", "it", "pt", "nl")) options.add(new DropdownWidget.Option(id, id));
        DropdownWidget dropdown = new DropdownWidget(settings("lang", 80, 18), options, 110, 14, "widgets/dropdown", "widgets/dropdown",
                "widgets/dropdown_selected", 3, null, 12, 5, "«", "»", "default");

        assertTrue(dropdown.isPaged());
        assertEquals(4, dropdown.pageSize());
        assertEquals(2, dropdown.pages());
        assertEquals(5, dropdown.rows());
        assertEquals(32, dropdown.listTop());
        assertEquals(66, dropdown.listHeight());
        assertEquals(35, dropdown.rowY(0));
        assertEquals("de", dropdown.current("de").id());
        assertEquals("en", dropdown.current("xx").id());

        List<String> found = variants(dropdown, 260, 140);
        assertTrue(found.contains("area 260x140@0"), found.toString());
        assertTrue(found.contains("glyph panel:widgets/dropdown@110x66@3@32"), found.toString());
        assertTrue(found.contains("area 104x12@35"), found.toString());
        assertTrue(found.contains("area 52x12@83"), found.toString());
    }

    @Test
    public void aModalCoversTheCanvasInStripsOf256Pixels() {
        ModalWidget modal = new ModalWidget(settings("confirm", 50, 30), 160, 80, "panels/frame", 4, "widgets/dim", true, List.of());
        List<String> found = variants(modal, 260, 300);

        assertEquals(List.of("glyph panel:widgets/dim@260x300@0@0", "area 260x256@0", "area 260x44@256", "glyph panel:panels/frame@160x80@4@30"), found);
        assertTrue(variants(modal).stream().noneMatch(variant -> variant.startsWith("area")));
    }

    @Test
    public void aListPlacesItsTemplateInEverySlotAndCountsItsPages() {
        TextElement template = new TextElement("name", List.of("%entry%"), PackValue.of("default"), PositionValue.of(0), PositionValue.of(0), null,
                CanvasValue.of(HAlign.LEFT), null, null);
        PageNavigation navigation = new PageNavigation(new PageNavigation.Button(8, 76, 60, 12, null, null, "«", "default"),
                new PageNavigation.Button(192, 76, 60, 12, null, null, "»", "default"), null);
        ListWidget grid = new ListWidget(settings("shop", 8, 14), navigation, List.of("a"), null, ",", 6, 3, 20, 20, 2, template, null, "default");
        assertEquals(52, grid.slotX(2));
        assertEquals(8, grid.slotX(3));
        assertEquals(36, grid.slotY(3));
        assertEquals(2, grid.rows());

        ListWidget list = new ListWidget(settings("shop", 8, 14), navigation, List.of("a", "b", "c", "d"), null, ",", 3, 1, 244, 18, 2, template,
                null, "default");

        assertEquals(List.of("a", "b", "c"), ListWidget.split(" a, b,, c ,", ","));
        assertEquals(2, list.pages(4));
        assertEquals(1, list.pages(0));
        assertEquals(34, list.slotY(1));
        assertEquals(8, list.slotX(2));
        assertEquals(List.of("text@14", "text@34", "text@54", "text@78", "area 60x12@76", "text@78", "area 60x12@76"), variants(list));

        assertEquals(0, PagedWidget.page("0", 3));
        assertEquals(1, PagedWidget.page("2", 3));
        assertEquals(2, PagedWidget.page("9", 3));
        assertEquals(0, PagedWidget.page("abc", 3));
    }

    @Test
    public void tabsHoldEveryLookAndEveryPageInThePack() {
        TextElement page = new TextElement("hello", List.of("Hello"), PackValue.of("default"), PositionValue.of(4), PositionValue.of(40), null,
                CanvasValue.of(HAlign.LEFT), null, null);
        ChoiceWidget tabs = new ChoiceWidget(settings("section", 0, 10), ChoiceWidget.Style.TABS,
                List.of(new ChoiceWidget.Option("general", "General", 60), new ChoiceWidget.Option("audio", "Audio", 50)), false, 2, 14,
                "widgets/tab_on", "widgets/tab_off", 3, "default", Map.of("audio", List.of(page)));

        assertEquals("general", tabs.current("unknown").id());
        assertEquals("audio", tabs.current("audio").id());
        assertEquals(List.of(
                "glyph panel:widgets/tab_on@60x14@3@10", "glyph panel:widgets/tab_off@60x14@3@10", "text@13", "area 60x14@10",
                "glyph panel:widgets/tab_on@50x14@3@10", "glyph panel:widgets/tab_off@50x14@3@10", "text@13", "area 50x14@10",
                "text@40"), variants(tabs));
    }

    @Test
    public void aChecklistKeepsTheOrderOfItsOptionsAndItsMax() {
        ChecklistWidget checklist = new ChecklistWidget(settings("tags", 0, 0), List.of(new ChoiceWidget.Option("pvp", "PvP", 40),
                new ChoiceWidget.Option("shop", "Shop", 40), new ChoiceWidget.Option("build", "Build", 40)), true, 2, 10, 2,
                "widgets/checkbox_on", "widgets/checkbox_off", "default");

        assertEquals(List.of("pvp", "build"), List.copyOf(checklist.checked("build, pvp,unknown")));
        assertEquals("pvp,shop", checklist.toggled(checklist.checked("shop"), "pvp"));
        assertEquals("shop", checklist.toggled(checklist.checked("pvp,shop"), "pvp"));
        assertNull(checklist.toggled(checklist.checked("pvp,shop"), "build"));
        assertEquals("", checklist.toggled(checklist.checked("pvp"), "pvp"));
        assertTrue(checklist.problems(200, 100).isEmpty());
        assertEquals(1, checklist.problems(20, 100).size());
    }

    @Test
    public void aRatingFillsUpToTheStarClickedAndEmptiesOnTheLastOne() {
        RatingWidget rating = new RatingWidget(settings("stars", 0, 0), 5, 1, "widgets/star_on", "widgets/star_off", true);
        assertEquals(3, rating.filled("3"));
        assertEquals(5, rating.filled("9"));
        assertEquals(0, rating.filled("-2"));
        assertEquals(0, rating.filled("abc"));
        assertEquals(4, rating.clicked(2, 4));
        assertEquals(0, rating.clicked(4, 4));
        assertEquals(4, new RatingWidget(settings("stars", 0, 0), 5, 1, "widgets/star_on", "widgets/star_off", false).clicked(4, 4));
    }

    @Test
    public void onlyElementsHoldingAnOverlayTakeTheOverlayPass() {
        ModalWidget modal = new ModalWidget(settings("window", 0, 0), 160, 80, "panels/frame", 4, "widgets/dim", true, List.of());
        ToggleWidget toggle = new ToggleWidget(settings("music", 0, 0), "widgets/toggle_on", "widgets/toggle_off", null, "default", null);
        PageNavigation.Button button = new PageNavigation.Button(0, 0, 10, 10, null, null, "x", "default");
        PageNavigation navigation = new PageNavigation(button, button, null);

        assertTrue(modal.hasOverlay());
        assertFalse(toggle.hasOverlay());
        assertTrue(new PagesWidget(settings("pages", 0, 0), navigation, Map.of("one", List.of(toggle), "two", List.of(modal))).hasOverlay());
        assertFalse(new PagesWidget(settings("pages", 0, 0), navigation, Map.of("one", List.of(toggle))).hasOverlay());
    }

    private static WidgetPart glyphAt(String glyph, Integer x, Integer y) {
        return new WidgetPart(null, glyph, null, x, y, null, null, null, List.of());
    }

    @Test
    public void theButtonsAndTheValueOfAStepperGoWhereTheyAreWritten() {
        StepperWidget stepper = new StepperWidget(settings("amount", 0, 20), 1, 9, 1, 60, glyphAt("minus", null, 12), glyphAt("plus", 0, -12), null, null,
                WidgetPart.ofText("%value%"), WidgetPart.ofText("<red>%value%"), WidgetPart.NONE, "default");
        List<String> found = variants(stepper);

        assertTrue(found.contains("glyph minus@32"), found.toString());
        assertTrue(found.contains("glyph plus@8"), found.toString());
        assertEquals(1, found.stream().filter(variant -> variant.startsWith("text@")).count(), found.toString());
        assertTrue(found.stream().anyMatch(variant -> variant.startsWith("area") && variant.endsWith("@32")), found.toString());
        assertTrue(found.stream().anyMatch(variant -> variant.startsWith("area") && variant.endsWith("@8")), found.toString());
    }

    @Test
    public void optionsWithAPositionLeaveTheRowAndTheOthersCloseUp() {
        List<ChoiceWidget.Option> options = List.of(new ChoiceWidget.Option("a", "A", 30),
                new ChoiceWidget.Option("up", "Up", 30, 40, -20, null, null, null, List.of()), new ChoiceWidget.Option("b", "B", 30),
                new ChoiceWidget.Option("down", "Down", 30, null, 20, "<gold>Down", "my_on", "my_off", List.of("tip")));
        int[][] row = ChoiceWidget.positions(options, false, 2, 14);
        assertArrayEquals(new int[]{0, 0}, row[0]);
        assertArrayEquals(new int[]{40, -20}, row[1]);
        assertArrayEquals(new int[]{32, 0}, row[2]);
        assertArrayEquals(new int[]{0, 20}, row[3]);
        assertArrayEquals(new int[]{0, 16}, ChoiceWidget.positions(options, true, 2, 14)[2]);
        assertEquals("<gold>Down", options.get(3).label(true));
        assertEquals("Down", options.get(3).label(false));
    }

    @Test
    public void starsPlacedOneByOneSetTheMax() {
        RatingWidget rating = new RatingWidget(settings("stars", 10, 10), 5, 1, "widgets/star_on", "widgets/star_off", true, List.of("%value%"),
                List.of(new int[]{0, 8}, new int[]{12, 2}, new int[]{24, 0}));
        assertEquals(3, rating.filled("7"));
        assertArrayEquals(new int[]{12, 2}, rating.position(2, 9));
        assertArrayEquals(new int[]{10, 0}, new RatingWidget(settings("stars", 0, 0), 5, 1, "a", "b", true).position(2, 9));
    }

    @Test
    public void theOpenListOfADropdownCanOpenElsewhere() {
        List<DropdownWidget.Option> options = List.of(new DropdownWidget.Option("en", "English"), new DropdownWidget.Option("fr", "French", List.of("tip")));
        DropdownWidget dropdown = new DropdownWidget(settings("lang", 20, 30), options, 100, 14, "widgets/dropdown", "widgets/dropdown", null, 3,
                WidgetPart.NONE, 12, 6, "<", ">", "default", WidgetPart.NONE, new WidgetPart(null, null, null, 104, 0, null, null, null, List.of()),
                WidgetPart.NONE);
        assertEquals(124, dropdown.listX());
        assertEquals(30, dropdown.listTop());
    }

    @Test
    public void aPartIsAValueOrASectionAndIsWrittenBackTheSameWay() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("label", "<white>Music");
        yaml.set("minus.glyph", "my/minus");
        yaml.set("minus.y", 12);
        yaml.set("minus.align", "center");
        yaml.set("minus.hover", List.of("<gray>One less"));

        WidgetPart label = WidgetPart.read(yaml, "label", false, "w", "test");
        WidgetPart minus = WidgetPart.read(yaml, "minus", true, "w", "test");
        assertEquals("<white>Music", label.text());
        assertEquals("my/minus", minus.glyph());
        assertEquals(12, minus.y(0));
        assertEquals(7, minus.x(7));
        assertEquals(HAlign.CENTER, minus.align(HAlign.LEFT));
        assertEquals(List.of("<gray>One less"), minus.hover());
        assertEquals("Off", WidgetPart.ofText("Off").orElse(minus).text());
        assertEquals(12, WidgetPart.ofText("Off").orElse(minus).y(0));

        YamlConfiguration written = new YamlConfiguration();
        label.serialize(written, "label", false);
        minus.serialize(written, "minus", true);
        WidgetPart.NONE.serialize(written, "none", false);
        assertEquals("<white>Music", written.getString("label"));
        assertEquals(12, written.getInt("minus.y"));
        assertEquals("center", written.getString("minus.align"));
        assertFalse(written.contains("none"));
    }

    @Test
    public void aConfirmButtonHoldsBothLooksInThePack() {
        ConfirmWidget confirm = new ConfirmWidget(settings("delete", 10, 20), 80, 14, "widgets/minus", "widgets/minus_off", "Delete", "Sure?", "default",
                List.of(), List.of(), true);
        assertEquals(List.of("glyph widgets/minus@20", "glyph widgets/minus_off@20", "text@23", "area 80x14@20"), variants(confirm));
        assertTrue(confirm.problems(100, 40).isEmpty());
        assertEquals(1, confirm.problems(80, 40).size());
    }
}
