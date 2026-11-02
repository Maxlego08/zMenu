package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.SizeSpec;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextBoxLayoutTest {

    private static TextElement box(TextElement.VerticalAlign vertical) {
        return new TextElement("text", List.of("%quest%"), PackValue.of("default"), PositionValue.of(0), PositionValue.of(20), CanvasValue.of(100), CanvasValue.of(HAlign.LEFT), 10,
                new TextElement.TextBox(3, TextElement.Wrap.WORD, vertical, true));
    }

    private static Set<Integer> textPositions(CanvasElement element) {
        Set<Integer> positions = new TreeSet<>();
        element.collectVariants(new CanvasVariants() {
            @Override
            public void glyph(@NotNull ResolvedGlyph glyph, int y) {
            }

            @Override
            public void text(@NotNull String font, int y) {
                positions.add(y);
            }

            @Override
            public void area(int y, int width, int height) {
            }

            @Override
            public ResolvedGlyph resolve(@NotNull String reference) {
                return null;
            }
        });
        return positions;
    }

    @Test
    public void aTopBoxNeedsEveryLinePosition() {
        assertEquals(Set.of(20, 30, 40), textPositions(box(TextElement.VerticalAlign.TOP)));
    }

    @Test
    public void aCentredBoxNeedsEveryOffset() {
        assertEquals(Set.of(20, 25, 30, 35, 40), textPositions(box(TextElement.VerticalAlign.CENTER)));
    }

    @Test
    public void aBottomBoxNeedsEveryOffset() {
        assertEquals(Set.of(20, 30, 40), textPositions(box(TextElement.VerticalAlign.BOTTOM)));
    }

    @Test
    public void growTextMovesTheChildrenBelow() {
        TextElement text = box(TextElement.VerticalAlign.TOP);
        TextElement below = new TextElement("below", List.of("Footer"), PackValue.of("default"), PositionValue.of(0), PositionValue.of(0), CanvasValue.of(100),
                CanvasValue.of(HAlign.LEFT), null, null);
        LayoutElement column = new LayoutElement("column", LayoutEngine.Direction.COLUMN, 0, 0, SizeSpec.FIT, SizeSpec.FIT, LayoutEngine.Insets.NONE, 2,
                LayoutEngine.Align.START, LayoutEngine.Justify.START,
                List.of(new LayoutElement.Child(text, SizeSpec.FIT, SizeSpec.FIT, true), new LayoutElement.Child(below, SizeSpec.FIT, SizeSpec.FIT)), null);

        List<CanvasElement> oneLine = column.place(reference -> Optional.empty(), 1);
        List<CanvasElement> threeLines = column.place(reference -> Optional.empty(), 3);
        assertEquals(12, ((TextElement) oneLine.get(1)).y().fixed());
        assertEquals(32, ((TextElement) threeLines.get(1)).y().fixed());

        Set<Integer> positions = textPositions(column);
        assertEquals(Set.of(0, 10, 20, 12, 22, 32), positions);
    }
}
