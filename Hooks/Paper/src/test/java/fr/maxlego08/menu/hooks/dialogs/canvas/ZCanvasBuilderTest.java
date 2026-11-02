package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.dialog.canvas.GlyphCell;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.CanvasLayout;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.GlyphVariant;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.HitboxGlyphs;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextFonts;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.VanillaTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.vanilla.VanillaAssets;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZCanvasBuilderTest {

    private static final GlyphCell CELL = new GlyphCell(Key.key("zmenu", "font/canvas/a_s0.png"), 1, 1, 0, 0, 16, 16, 16, 16, null, null, 0);
    private static final ResolvedGlyph GLYPH = new ResolvedGlyph("zmenu:a", List.of(CELL), false);
    private static final GlyphCell NATIVE_CELL = new GlyphCell(Key.key("nexo", "b.png"), 1, 1, 0, 0, 8, 8, 8, 8, Key.key("nexo", "default"), "", 7);
    private static final ResolvedGlyph NATIVE_GLYPH = new ResolvedGlyph("nexo:b", List.of(NATIVE_CELL), false);

    @TempDir
    Path folder;

    private TextFonts fonts() {
        return new TextFonts(new VanillaTextFont(new VanillaAssets(this.folder, "test")));
    }

    @Test
    public void everyLineEndsOnePixelPastTheCanvas() {
        String character = Character.toString(0xF0000);
        CanvasLayout layout = new CanvasLayout(Map.of(new GlyphVariant(CELL, 10), character), Set.of());
        ZCanvasBuilder builder = new ZCanvasBuilder(100, 30, layout, this.fonts(), missing -> {
        });
        builder.glyph(GLYPH, 20, 10);
        builder.glyph(NATIVE_GLYPH, 50, Integer.MIN_VALUE);

        Component component = builder.build();
        List<Component> lines = lines(component);
        assertEquals(4, lines.size());
        double first = CanvasTestSupport.netAdvance(lines.getFirst(), Map.of(0xF0000, 17.0, 0xE000, 9.0));
        assertEquals(101, first, 1e-9);
        for (Component line : lines.subList(1, lines.size())) assertEquals(0, CanvasTestSupport.netAdvance(line, Map.of()), 1e-9);
        assertTrue(builder.bodyWidth() >= 110);
    }

    @Test
    public void aGlyphMissingFromThePackIsSkippedAndReported() {
        List<String> reported = new ArrayList<>();
        ZCanvasBuilder builder = new ZCanvasBuilder(50, 9, CanvasLayout.EMPTY, this.fonts(), reported::add);
        builder.glyph(GLYPH, 0, 3);

        assertEquals(List.of("glyph zmenu:a at y 3"), reported);
        assertEquals(51, CanvasTestSupport.netAdvance(builder.build(), Map.of()), 1e-9);
    }

    @Test
    public void theBodyGrowsWhenAnElementPassesTheCanvas() {
        String character = Character.toString(0xF0000);
        CanvasLayout layout = new CanvasLayout(Map.of(new GlyphVariant(CELL, 0), character), Set.of());
        ZCanvasBuilder builder = new ZCanvasBuilder(20, 9, layout, this.fonts(), missing -> {
        });
        builder.glyph(GLYPH, 10, 0);
        assertEquals(46, builder.bodyWidth());
        assertEquals(30, new ZCanvasBuilder(20, 9, layout, this.fonts(), missing -> {
        }).bodyWidth());
    }

    private static CanvasLayout areaLayout(int width, int height, int y) {
        Map<GlyphVariant, String> glyphs = new HashMap<>();
        int codepoint = 0xF0100;
        for (GlyphCell cell : HitboxGlyphs.cells(width, height)) glyphs.put(new GlyphVariant(cell, y), Character.toString(codepoint++));
        return new CanvasLayout(glyphs, Set.of());
    }

    @Test
    public void anAreaIsAnInvisibleGlyphCarryingTheClick() {
        ClickEvent click = ClickEvent.runCommand("/test");
        ZCanvasBuilder builder = new ZCanvasBuilder(100, 18, areaLayout(40, 12, 3), this.fonts(), missing -> {
        });
        builder.area(10, 3, 40, 12, click, Component.text("tip"));

        List<Component> lines = lines(builder.build());
        assertEquals(101, CanvasTestSupport.netAdvance(lines.getFirst(), Map.of(0xF0100, 41.0)), 1e-9);
        assertTrue(styles(lines.getFirst()).stream().anyMatch(style -> click.equals(style.clickEvent()) && style.hoverEvent() != null));
    }

    @Test
    public void noLineGoesPastTheLongestLineSoTheClientAddsNoEmptyLine() {
        ClickEvent click = ClickEvent.runCommand("/test");
        ZCanvasBuilder builder = new ZCanvasBuilder(100, 18, areaLayout(100, 12, 0), this.fonts(), missing -> {
        });
        builder.area(0, 0, 100, 12, click, null);

        Map<Integer, Double> advances = Map.of(0xF0100, 101.0);
        List<Component> lines = lines(builder.build());
        double longest = lines.stream().mapToDouble(line -> CanvasTestSupport.netAdvance(line, advances)).max().orElse(0);
        for (Component line : lines) assertTrue(CanvasTestSupport.peakAdvance(line, advances) <= longest, "a line goes past " + longest);
        assertEquals(110, builder.bodyWidth());
    }

    @Test
    public void wideAreasAreSliced() {
        assertEquals(2, HitboxGlyphs.cells(300, 10).size());
        assertEquals(256, HitboxGlyphs.cells(300, 10).getFirst().cellWidth());
        assertEquals(44, HitboxGlyphs.cells(300, 10).get(1).cellWidth());
    }

    @Test
    public void oldClientsGetTheClickOnEveryLineTheAreaCrosses() {
        ClickEvent click = ClickEvent.runCommand("/test");
        ZCanvasBuilder builder = new ZCanvasBuilder(100, 27, areaLayout(40, 12, 5), this.fonts(), missing -> {
        }, true);
        builder.area(10, 5, 40, 12, click, null);

        List<Component> lines = lines(builder.build());
        assertEquals(3, lines.size());
        assertEquals(101, CanvasTestSupport.netAdvance(lines.getFirst(), Map.of(0xF0100, 41.0)), 1e-9);
        assertEquals(0, CanvasTestSupport.netAdvance(lines.get(1), Map.of()), 1e-9);
        assertTrue(styles(lines.get(1)).stream().anyMatch(style -> click.equals(style.clickEvent())));
        assertTrue(styles(lines.get(2)).stream().noneMatch(style -> click.equals(style.clickEvent())));

        Component layer = lines.get(1).children().getFirst();
        assertEquals(10, CanvasTestSupport.netAdvance(layer, Map.of()), 1e-9);
        assertEquals(null, layer.style().clickEvent());
    }

    @Test
    public void aGridGlyphDrawsEachRowLower() {
        GlyphCell top = new GlyphCell(Key.key("test", "grid.png"), 1, 2, 0, 0, 8, 8, 8, 8, null, null, 0);
        GlyphCell bottom = new GlyphCell(Key.key("test", "grid.png"), 1, 2, 0, 1, 8, 8, 8, 8, null, null, 0);
        CanvasLayout layout = new CanvasLayout(Map.of(new GlyphVariant(top, 4), "󰀀", new GlyphVariant(bottom, 12), "󰀁"), Set.of());
        List<String> reported = new ArrayList<>();
        ZCanvasBuilder builder = new ZCanvasBuilder(40, 27, layout, this.fonts(), reported::add);
        builder.glyph(new ResolvedGlyph("test:grid", List.of(top, bottom), false), 0, 4);

        assertEquals(List.of(), reported);
        List<Style> styles = styles(lines(builder.build()).getFirst());
        assertTrue(styles.stream().anyMatch(style -> CanvasLayout.canvasFont(4).equals(style.font())));
        assertTrue(styles.stream().anyMatch(style -> CanvasLayout.canvasFont(12).equals(style.font())));
    }

    @Test
    public void inlineComponentsGoOnTheirLine() {
        List<String> reported = new ArrayList<>();
        ZCanvasBuilder builder = new ZCanvasBuilder(60, 27, CanvasLayout.EMPTY, this.fonts(), reported::add);
        builder.inline(Component.text(""), 8, 10, 2);
        builder.inline(Component.text(""), 8, 0, 5);

        List<Component> lines = lines(builder.build());
        assertEquals(3, lines.size());
        assertEquals(61, CanvasTestSupport.netAdvance(lines.get(2), Map.of(0xE123, 8.0)), 1e-9);
        assertTrue(styles(lines.get(2)).size() > 1);
        assertEquals(1, reported.size());
    }

    private static List<Style> styles(Component component) {
        List<Style> styles = new ArrayList<>();
        styles.add(component.style());
        for (Component child : component.children()) styles.addAll(styles(child));
        return styles;
    }

    @Test
    public void anElementIsDrawnOnItsLineWithTheYInsideIt() {
        String character = Character.toString(0xF0000);
        CanvasLayout layout = new CanvasLayout(Map.of(new GlyphVariant(CELL, 1), character), Set.of());
        List<String> reported = new ArrayList<>();
        ZCanvasBuilder builder = new ZCanvasBuilder(100, 30, layout, this.fonts(), reported::add);
        builder.line(2).glyph(GLYPH, 20, 19);
        builder.line(9).glyph(GLYPH, 40, 28);

        List<Component> lines = lines(builder.build());
        assertTrue(reported.isEmpty(), reported.toString());
        assertEquals(4, lines.size());
        assertTrue(PlainTextComponentSerializer.plainText().serialize(lines.get(0)).indexOf(character) < 0);
        assertTrue(PlainTextComponentSerializer.plainText().serialize(lines.get(2)).contains(character));
        assertTrue(PlainTextComponentSerializer.plainText().serialize(lines.get(3)).contains(character));
        assertEquals(101, CanvasTestSupport.netAdvance(lines.get(2), Map.of(0xF0000, 17.0)), 1e-9);
    }

    private static List<Component> lines(Component root) {
        List<Component> lines = new ArrayList<>();
        TextComponent.Builder current = Component.text();
        for (Component child : root.children()) {
            if (child instanceof TextComponent text && text.content().equals("\n")) {
                lines.add(current.build());
                current = Component.text();
            } else {
                current.append(child);
            }
        }
        lines.add(current.build());
        return lines;
    }
}
