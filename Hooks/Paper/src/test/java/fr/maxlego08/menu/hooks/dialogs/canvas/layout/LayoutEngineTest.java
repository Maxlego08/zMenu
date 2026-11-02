package fr.maxlego08.menu.hooks.dialogs.canvas.layout;

import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LayoutEngineTest {

    private static Item fit(int width, int height) {
        return new Item(width, height, SizeSpec.FIT, SizeSpec.FIT);
    }

    @Test
    public void aRowPlacesChildrenWithPaddingAndGap() {
        List<Box> boxes = LayoutEngine.layout(Direction.ROW, new Box(10, 20, 200, 40), new Insets(4, 4, 4, 4), 2, Align.START, Justify.START,
                List.of(fit(16, 16), fit(30, 9)));
        assertEquals(new Box(14, 24, 16, 16), boxes.get(0));
        assertEquals(new Box(32, 24, 30, 9), boxes.get(1));
    }

    @Test
    public void growTakesTheSpaceLeft() {
        List<Box> boxes = LayoutEngine.layout(Direction.ROW, new Box(0, 0, 100, 20), Insets.NONE, 0, Align.START, Justify.START,
                List.of(fit(20, 10), new Item(0, 10, SizeSpec.GROW, SizeSpec.FIT), new Item(0, 10, SizeSpec.GROW, SizeSpec.FIT)));
        assertEquals(20, boxes.get(0).width());
        assertEquals(40, boxes.get(1).width());
        assertEquals(40, boxes.get(2).width());
        assertEquals(60, boxes.get(2).x());
    }

    @Test
    public void crossAlignmentCentresChildren() {
        List<Box> boxes = LayoutEngine.layout(Direction.ROW, new Box(0, 0, 100, 30), Insets.NONE, 0, Align.CENTER, Justify.START, List.of(fit(10, 10)));
        assertEquals(10, boxes.getFirst().y());
    }

    @Test
    public void justifyUsesTheSpaceLeft() {
        List<Item> items = List.of(fit(10, 10), fit(10, 10));
        Box container = new Box(0, 0, 100, 10);
        assertEquals(40, LayoutEngine.layout(Direction.ROW, container, Insets.NONE, 0, Align.START, Justify.CENTER, items).getFirst().x());
        assertEquals(80, LayoutEngine.layout(Direction.ROW, container, Insets.NONE, 0, Align.START, Justify.END, items).getFirst().x());
        assertEquals(90, LayoutEngine.layout(Direction.ROW, container, Insets.NONE, 0, Align.START, Justify.SPACE_BETWEEN, items).get(1).x());
    }

    @Test
    public void aColumnStacksChildrenAndCanStretchThem() {
        List<Box> boxes = LayoutEngine.layout(Direction.COLUMN, new Box(0, 0, 80, 100), new Insets(2, 2, 2, 2), 3, Align.START, Justify.START,
                List.of(fit(16, 16), new Item(10, 9, SizeSpec.GROW, SizeSpec.FIT), new Item(10, 10, new SizeSpec(SizeSpec.Kind.PERCENT, 50), SizeSpec.FIT)));
        assertEquals(new Box(2, 2, 16, 16), boxes.get(0));
        assertEquals(new Box(2, 21, 76, 9), boxes.get(1));
        assertEquals(new Box(2, 33, 38, 10), boxes.get(2));
    }

    @Test
    public void preferredSizeFitsTheChildren() {
        assertArrayEquals(new int[]{50, 24}, LayoutEngine.preferredSize(Direction.ROW, new Insets(4, 4, 4, 4), 2, List.of(fit(16, 16), fit(24, 9))));
        assertArrayEquals(new int[]{28, 32}, LayoutEngine.preferredSize(Direction.COLUMN, new Insets(2, 2, 2, 2), 3, List.of(fit(16, 16), fit(24, 9))));
    }

    @Test
    public void sizesAreParsed() {
        assertEquals(new SizeSpec(SizeSpec.Kind.FIXED, 12), SizeSpec.parse(12));
        assertEquals(new SizeSpec(SizeSpec.Kind.FIXED, 12), SizeSpec.parse("12"));
        assertEquals(SizeSpec.FIT, SizeSpec.parse("fit"));
        assertEquals(SizeSpec.GROW, SizeSpec.parse("Grow"));
        assertEquals(new SizeSpec(SizeSpec.Kind.PERCENT, 50), SizeSpec.parse("50%"));
        assertNull(SizeSpec.parse("150%"));
        assertNull(SizeSpec.parse("big"));
        assertNull(SizeSpec.parse(-1));
    }
}
