package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CanvasLinesTest {

    private static final int NONE = Integer.MAX_VALUE;

    @Test
    public void eachElementTakesTheLineOfItsHighestDrawing() {
        CanvasLines lines = CanvasLines.assign(new int[]{0, 10, 19, 40}, new int[]{0, 10, 19, 40}, new boolean[4], 10);
        assertArrayEquals(new int[]{0, 1, 2, 4}, lines.base());
    }

    @Test
    public void anElementNeverGoesPastTheLineOfTheNextOne() {
        CanvasLines lines = CanvasLines.assign(new int[]{40, 30, 10}, new int[]{40, 30, 10}, new boolean[3], 10);
        assertArrayEquals(new int[]{1, 1, 1}, lines.base());
    }

    @Test
    public void overlaysComeAfterEveryElementAndKeepThemAbove() {
        CanvasLines lines = CanvasLines.assign(new int[]{30, 50, 60}, new int[]{30, 20, 60}, new boolean[]{false, true, false}, 10);
        assertArrayEquals(new int[]{2, 2, 2}, lines.base());
        assertArrayEquals(new int[]{2, 2, 2}, lines.overlay());
    }

    @Test
    public void elementsDrawingNothingFollowTheNextOneAndLinesStayInTheCanvas() {
        CanvasLines lines = CanvasLines.assign(new int[]{NONE, 80, NONE}, new int[]{NONE, 80, NONE}, new boolean[3], 3);
        assertArrayEquals(new int[]{3, 3, 3}, lines.base());
        assertArrayEquals(new int[]{0, 0}, CanvasLines.assign(new int[]{-4, 20}, new int[]{-4, 20}, new boolean[2], 0).base());
    }
}
