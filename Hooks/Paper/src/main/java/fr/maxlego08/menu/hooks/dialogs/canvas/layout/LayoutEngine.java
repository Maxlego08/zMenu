package fr.maxlego08.menu.hooks.dialogs.canvas.layout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Places the children of a {@code row} or a {@code column}, in the spirit of the Clay layout library: each
 * child is sized on both axes as a fixed size, its own size ({@code fit}), a share of the space left
 * ({@code grow}) or a percentage of its container, then the children are laid along the main axis with a
 * gap and aligned on the other one.
 */
public final class LayoutEngine {

    private LayoutEngine() {
    }

    /**
     * The direction children are laid in.
     */
    public enum Direction {
        ROW,
        COLUMN
    }

    /**
     * Where children sit on the axis they are not laid along.
     */
    public enum Align {
        START,
        CENTER,
        END;

        public static @NotNull Align parse(@NotNull String value) {
            return valueOf(value.toUpperCase(Locale.ROOT).replace('-', '_'));
        }
    }

    /**
     * How the space left on the main axis is used.
     */
    public enum Justify {
        START,
        CENTER,
        END,
        SPACE_BETWEEN;

        public static @NotNull Justify parse(@NotNull String value) {
            return valueOf(value.toUpperCase(Locale.ROOT).replace('-', '_'));
        }
    }

    /**
     * The space kept inside a container, around its children.
     */
    public record Insets(int top, int right, int bottom, int left) {

        public static final Insets NONE = new Insets(0, 0, 0, 0);

        public int horizontal() {
            return this.left + this.right;
        }

        public int vertical() {
            return this.top + this.bottom;
        }
    }

    /**
     * A rectangle, in canvas pixels.
     */
    public record Box(int x, int y, int width, int height) {
    }

    /**
     * A child to place.
     *
     * @param preferredWidth  The width the child would like.
     * @param preferredHeight The height the child would like.
     * @param width           How its width is chosen.
     * @param height          How its height is chosen.
     */
    public record Item(int preferredWidth, int preferredHeight, @NotNull SizeSpec width, @NotNull SizeSpec height) {
    }

    /**
     * Computes the size a container would like with its children at their own size.
     *
     * @return The width and height, in this order.
     */
    public static int @NotNull [] preferredSize(@NotNull Direction direction, @NotNull Insets padding, int gap, @NotNull List<Item> items) {
        int main = 0;
        int cross = 0;
        for (Item item : items) {
            boolean row = direction == Direction.ROW;
            int itemMain = preferred(row ? item.width() : item.height(), row ? item.preferredWidth() : item.preferredHeight());
            int itemCross = preferred(row ? item.height() : item.width(), row ? item.preferredHeight() : item.preferredWidth());
            main += itemMain;
            cross = Math.max(cross, itemCross);
        }
        if (items.size() > 1) main += gap * (items.size() - 1);
        return direction == Direction.ROW
                ? new int[]{main + padding.horizontal(), cross + padding.vertical()}
                : new int[]{cross + padding.horizontal(), main + padding.vertical()};
    }

    private static int preferred(@NotNull SizeSpec spec, int preferred) {
        return switch (spec.kind()) {
            case FIXED -> spec.value();
            case PERCENT -> 0;
            case FIT, GROW -> preferred;
        };
    }

    /**
     * Places children inside a container.
     *
     * @param direction The direction children are laid in.
     * @param container The box of the container.
     * @param padding   The space kept inside the container.
     * @param gap       The space between two children.
     * @param align     Where children sit on the other axis.
     * @param justify   How the space left on the main axis is used.
     * @param items     The children, in order.
     * @return The box of each child, in the same order.
     */
    public static @NotNull List<Box> layout(@NotNull Direction direction, @NotNull Box container, @NotNull Insets padding, int gap, @NotNull Align align,
                                            @NotNull Justify justify, @NotNull List<Item> items) {
        boolean row = direction == Direction.ROW;
        int innerX = container.x() + padding.left();
        int innerY = container.y() + padding.top();
        int innerWidth = Math.max(0, container.width() - padding.horizontal());
        int innerHeight = Math.max(0, container.height() - padding.vertical());
        int innerMain = row ? innerWidth : innerHeight;
        int innerCross = row ? innerHeight : innerWidth;

        int count = items.size();
        int[] mains = new int[count];
        int used = count > 1 ? gap * (count - 1) : 0;
        int growing = 0;
        for (int index = 0; index < count; index++) {
            Item item = items.get(index);
            SizeSpec spec = row ? item.width() : item.height();
            int preferred = row ? item.preferredWidth() : item.preferredHeight();
            mains[index] = switch (spec.kind()) {
                case FIXED -> spec.value();
                case PERCENT -> (int) Math.round(innerMain * spec.value() / 100.0);
                case FIT, GROW -> preferred;
            };
            if (spec.kind() == SizeSpec.Kind.GROW) growing++;
            used += mains[index];
        }

        int free = innerMain - used;
        if (free > 0 && growing > 0) {
            int share = free / growing;
            int remainder = free % growing;
            for (int index = 0; index < count; index++) {
                Item item = items.get(index);
                if ((row ? item.width() : item.height()).kind() != SizeSpec.Kind.GROW) continue;
                mains[index] += share + (remainder-- > 0 ? 1 : 0);
            }
            free = 0;
        }
        free = Math.max(0, free);

        int cursor = switch (justify) {
            case START, SPACE_BETWEEN -> 0;
            case CENTER -> free / 2;
            case END -> free;
        };
        int extraGap = justify == Justify.SPACE_BETWEEN && count > 1 ? free / (count - 1) : 0;

        List<Box> boxes = new ArrayList<>(count);
        for (int index = 0; index < count; index++) {
            Item item = items.get(index);
            SizeSpec crossSpec = row ? item.height() : item.width();
            int crossPreferred = row ? item.preferredHeight() : item.preferredWidth();
            int cross = switch (crossSpec.kind()) {
                case FIXED -> crossSpec.value();
                case PERCENT -> (int) Math.round(innerCross * crossSpec.value() / 100.0);
                case FIT -> crossPreferred;
                case GROW -> innerCross;
            };
            int crossOffset = switch (align) {
                case START -> 0;
                case CENTER -> (innerCross - cross) / 2;
                case END -> innerCross - cross;
            };

            boxes.add(row
                    ? new Box(innerX + cursor, innerY + crossOffset, mains[index], cross)
                    : new Box(innerX + crossOffset, innerY + cursor, cross, mains[index]));
            cursor += mains[index] + gap + extraGap;
        }
        return boxes;
    }
}
