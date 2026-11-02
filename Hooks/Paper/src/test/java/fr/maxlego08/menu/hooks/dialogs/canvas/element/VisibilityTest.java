package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.*;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.SizeSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

public class VisibilityTest {

    private static TextElement text(String name) {
        return new TextElement(name, List.of(name), PackValue.of("default"), PositionValue.of(0), PositionValue.of(0), null, CanvasValue.of(HAlign.LEFT), null, null);
    }

    private static ConditionalElement hidden(CanvasElement element) {
        return new ConditionalElement(element, VisibleCondition.parse("false"), null);
    }

    private static LayoutElement column(LayoutElement.Child... children) {
        return new LayoutElement("column", LayoutEngine.Direction.COLUMN, 0, 0, SizeSpec.FIT, SizeSpec.FIT, LayoutEngine.Insets.NONE, 2,
                LayoutEngine.Align.START, LayoutEngine.Justify.START, List.of(children), null);
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
            public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
                return null;
            }
        });
        return positions;
    }

    @Test
    public void aConditionalElementKeepsItsConditionWhenPlacedAndIsNeverStatic() {
        ConditionalElement badge = hidden(text("badge"));
        CanvasElement placed = badge.placed(5, 20, 30, 8);

        assertInstanceOf(ConditionalElement.class, placed);
        TextElement inner = assertInstanceOf(TextElement.class, ConditionalElement.unwrap(placed));
        assertEquals(20, inner.y().fixed());
        assertFalse(badge.isStatic());
        assertEquals("badge", badge.name());
        TextElement plain = text("plain");
        assertSame(plain, ConditionalElement.unwrap(plain));
    }

    @Test
    public void aCollapsedChildGivesItsPlaceToTheChildrenAfterIt() {
        LayoutElement column = column(new LayoutElement.Child(hidden(text("staff")), SizeSpec.FIT, SizeSpec.FIT, false, true),
                new LayoutElement.Child(text("buttons"), SizeSpec.FIT, SizeSpec.FIT));

        List<CanvasElement> shown = column.place(reference -> Optional.empty(), null, Set.of());
        List<CanvasElement> collapsed = column.place(reference -> Optional.empty(), null, Set.of(0));

        assertEquals(2, shown.size());
        assertEquals(10, ((TextElement) shown.get(1)).y().fixed());
        assertEquals(1, collapsed.size());
        assertEquals(0, ((TextElement) collapsed.getFirst()).y().fixed());
        assertTrue(column.preferredHeight(reference -> Optional.empty()) >= 18);
    }

    @Test
    public void thePackHoldsEveryCombinationOfCollapsedChildren() {
        LayoutElement column = column(new LayoutElement.Child(hidden(text("a")), SizeSpec.FIT, SizeSpec.FIT, false, true),
                new LayoutElement.Child(hidden(text("b")), SizeSpec.FIT, SizeSpec.FIT, false, true),
                new LayoutElement.Child(text("c"), SizeSpec.FIT, SizeSpec.FIT));

        assertEquals(Set.of(0, 10, 20), textPositions(column));
    }

    @Test
    public void aHiddenChildWithoutCollapseKeepsItsSpace() {
        LayoutElement column = column(new LayoutElement.Child(hidden(text("staff")), SizeSpec.FIT, SizeSpec.FIT),
                new LayoutElement.Child(text("buttons"), SizeSpec.FIT, SizeSpec.FIT));

        assertEquals(Set.of(0, 10), textPositions(column));
    }
}
