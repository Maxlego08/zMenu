package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.SizeSpec;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticCanvasTest {

    private static TextElement text(String line, PositionValue y) {
        return new TextElement("text", List.of(line), PackValue.of("default"), PositionValue.of(0), y, null, CanvasValue.of(HAlign.LEFT), null, null);
    }

    private static LayoutElement column(CanvasElement child) {
        return new LayoutElement("column", LayoutEngine.Direction.COLUMN, 0, 0, SizeSpec.FIT, SizeSpec.FIT, LayoutEngine.Insets.NONE, 0,
                LayoutEngine.Align.START, LayoutEngine.Justify.START, List.of(new LayoutElement.Child((TextElement) child, SizeSpec.FIT, SizeSpec.FIT)), null);
    }

    private static DialogCanvasBody body(CanvasElement... elements) {
        return new DialogCanvasBody(CanvasValue.of(200), CanvasValue.of(90), List.of(elements), List.of());
    }

    @Test
    public void elementsWithoutPlaceholdersAreStatic() {
        assertTrue(text("Hello", PositionValue.of(10)).isStatic());
        assertTrue(new ImageElement("image", PackValue.of("zmenu:logo"), PositionValue.of(0), null, null).isStatic());
        assertTrue(new PanelElement("panel", PackValue.of("panels/frame"), 2, PositionValue.of(0), PositionValue.of(0), 50, 50).isStatic());
        assertTrue(new ProgressElement("bar", PackValue.of("bars/full"), null, "3", "10", 0, PositionValue.of(0), PositionValue.of(0)).isStatic());
        assertTrue(column(text("Hello", PositionValue.of(0))).isStatic());
        assertTrue(body(text("Hello", PositionValue.of(0))).isStatic());
    }

    @Test
    public void placeholdersMakeAnElementDynamic() {
        PositionValue placeholderY = new PositionValue("%y%", null, List.of(0, 9));
        assertFalse(text("Hello %player_name%", PositionValue.of(0)).isStatic());
        assertFalse(text("Hello", placeholderY).isStatic());
        assertFalse(new ProgressElement("bar", PackValue.of("bars/full"), null, "%level%", "10", 0, PositionValue.of(0), PositionValue.of(0)).isStatic());
        assertFalse(column(text("%player_name%", PositionValue.of(0))).isStatic());
        assertFalse(body(text("Hello", PositionValue.of(0)), text("%player_name%", PositionValue.of(10))).isStatic());
    }
}
