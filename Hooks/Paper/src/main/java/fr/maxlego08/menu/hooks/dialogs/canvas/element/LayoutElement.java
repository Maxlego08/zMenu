package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.SizeSpec;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Lays its children out in a row or a column, written {@code type: row} or {@code type: column}.
 *
 * <pre>
 * toolbar:
 *   type: row
 *   x: 0
 *   y: 0
 *   width: 200          # a number or fit; inside another layout also grow or 50%
 *   height: fit
 *   padding: 4          # or [top, right, bottom, left]
 *   gap: 2
 *   align: center       # start, center or end, on the other axis
 *   justify: start      # start, center, end or space-between
 *   children:
 *     icon:
 *       type: image
 *       glyph: minecraft:item/diamond
 *     label:
 *       type: text
 *       text: "Diamonds"
 *       layout-width: grow        # how the layout sizes a child: a number, fit, grow or 50%
 *       layout-height: fit
 *     vip:
 *       type: text
 *       text: "VIP"
 *       visible: "%player_has_permission_shop.vip%"
 *       collapse: true            # when hidden, the children after it take its place
 * </pre>
 * A child takes its position from the layout, so it needs no {@code x} or {@code y}. Its size must not
 * depend on the player, because the vertical positions are generated into the resource pack.
 * <p>
 * The one exception is a text box with {@code layout-height: grow-text} in a column: it takes the height of
 * the lines its text needs for the player, and the children after it move down. The pack then holds the
 * children after it at every position, one per possible number of lines; only one such child is allowed per
 * column. A column inside another layout keeps its full height in its parent.
 * <p>
 * A child hidden by {@code visible} or {@code view-requirement} keeps its space, unless it sets
 * {@code collapse: true}: the layout is then arranged without it, and the pack holds the children at the
 * positions of every combination of collapsed children (at most {@value #MAX_COLLAPSING} per layout). The
 * layout itself keeps the size it has with every child shown.
 */
public final class LayoutElement implements CanvasElement, CanvasLayoutable {

    /**
     * The most children with {@code collapse: true} in one layout, since the pack holds every combination.
     */
    public static final int MAX_COLLAPSING = 6;

    private final String name;
    private final LayoutEngine.Direction direction;
    private final int x;
    private final int y;
    private final SizeSpec width;
    private final SizeSpec height;
    private final LayoutEngine.Insets padding;
    private final int gap;
    private final LayoutEngine.Align align;
    private final LayoutEngine.Justify justify;
    private final List<Child> children;
    private final LayoutEngine.Box box;

    public LayoutElement(@NotNull String name, @NotNull LayoutEngine.Direction direction, int x, int y, @NotNull SizeSpec width, @NotNull SizeSpec height,
                         @NotNull LayoutEngine.Insets padding, int gap, @NotNull LayoutEngine.Align align, @NotNull LayoutEngine.Justify justify,
                         @NotNull List<Child> children, @Nullable LayoutEngine.Box box) {
        this.name = name;
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.padding = padding;
        this.gap = gap;
        this.align = align;
        this.justify = justify;
        this.children = List.copyOf(children);
        this.box = box;
    }

    /**
     * A child of a layout.
     *
     * @param element  The child, which can be placed.
     * @param width    How the layout chooses its width.
     * @param height   How the layout chooses its height.
     * @param growText True for a text box whose height is the height of the lines it takes for the player
     *                 ({@code layout-height: grow-text}), which moves the children after it.
     * @param collapse True to arrange the layout without this child when it is hidden for the player.
     */
    public record Child(@NotNull CanvasLayoutable element, @NotNull SizeSpec width, @NotNull SizeSpec height, boolean growText, boolean collapse) {

        public Child(@NotNull CanvasLayoutable element, @NotNull SizeSpec width, @NotNull SizeSpec height, boolean growText) {
            this(element, width, height, growText, false);
        }

        public Child(@NotNull CanvasLayoutable element, @NotNull SizeSpec width, @NotNull SizeSpec height) {
            this(element, width, height, false, false);
        }
    }

    private @Nullable TextElement growingText() {
        for (Child child : this.children) {
            if (child.growText() && unwrap(child) instanceof TextElement text) return text;
        }
        return null;
    }

    private static @NotNull Object unwrap(@NotNull Child child) {
        return child.element() instanceof CanvasElement element ? ConditionalElement.unwrap(element) : child.element();
    }

    private @NotNull List<Integer> collapsing() {
        List<Integer> indexes = new ArrayList<>();
        for (int index = 0; index < this.children.size(); index++) {
            if (this.children.get(index).collapse()) indexes.add(index);
        }
        return indexes;
    }

    @Override
    public @NotNull String name() {
        return this.name;
    }

    /**
     * @return How a parent layout chooses the width of this layout.
     */
    public @NotNull SizeSpec widthSpec() {
        return this.width;
    }

    /**
     * @return How a parent layout chooses the height of this layout.
     */
    public @NotNull SizeSpec heightSpec() {
        return this.height;
    }

    private @NotNull List<LayoutEngine.Item> items(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.items(glyphs, null);
    }

    private @NotNull List<LayoutEngine.Item> items(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @Nullable Integer textLines) {
        return this.items(glyphs, textLines, this.children);
    }

    private @NotNull List<LayoutEngine.Item> items(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @Nullable Integer textLines,
                                                   @NotNull List<Child> shown) {
        List<LayoutEngine.Item> items = new ArrayList<>(shown.size());
        for (Child child : shown) {
            int preferredHeight = child.growText() && textLines != null && unwrap(child) instanceof TextElement text
                    ? text.heightFor(textLines)
                    : child.element().preferredHeight(glyphs);
            items.add(new LayoutEngine.Item(child.element().preferredWidth(glyphs), preferredHeight, child.width(), child.height()));
        }
        return items;
    }

    private int @NotNull [] preferred(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        int[] fit = LayoutEngine.preferredSize(this.direction, this.padding, this.gap, this.items(glyphs));
        return new int[]{
                this.width.kind() == SizeSpec.Kind.FIXED ? this.width.value() : fit[0],
                this.height.kind() == SizeSpec.Kind.FIXED ? this.height.value() : fit[1]
        };
    }

    @Override
    public int preferredWidth(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.preferred(glyphs)[0];
    }

    @Override
    public int preferredHeight(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.preferred(glyphs)[1];
    }

    @Override
    public @NotNull CanvasElement placed(int x, int y, int width, int height) {
        return new LayoutElement(this.name, this.direction, x, y, this.width, this.height, this.padding, this.gap, this.align, this.justify,
                this.children, new LayoutEngine.Box(x, y, width, height));
    }

    /**
     * Places the children.
     *
     * @param glyphs Resolves glyph references, to measure images.
     * @return The children, each placed in its box.
     */
    public @NotNull List<CanvasElement> place(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return this.place(glyphs, null);
    }

    /**
     * Places the children, with the growing text box taking a number of lines.
     *
     * @param glyphs    Resolves glyph references, to measure images.
     * @param textLines The number of lines of the growing text box, or null for its most lines.
     * @return The children, each placed in its box.
     */
    public @NotNull List<CanvasElement> place(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @Nullable Integer textLines) {
        return this.place(glyphs, textLines, Set.of());
    }

    /**
     * Places the children, without the collapsed ones.
     *
     * @param glyphs    Resolves glyph references, to measure images.
     * @param textLines The number of lines of the growing text box, or null for its most lines.
     * @param collapsed The indexes of the children left out, which must have {@code collapse: true}.
     * @return The children shown, each placed in its box.
     */
    public @NotNull List<CanvasElement> place(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @Nullable Integer textLines,
                                              @NotNull Set<Integer> collapsed) {
        List<Child> shown = new ArrayList<>(this.children.size());
        for (int index = 0; index < this.children.size(); index++) {
            if (!collapsed.contains(index)) shown.add(this.children.get(index));
        }
        List<LayoutEngine.Item> items = this.items(glyphs, textLines, shown);
        LayoutEngine.Box container = this.box;
        if (container == null) {
            int[] size = this.preferred(glyphs);
            container = new LayoutEngine.Box(this.x, this.y, size[0], size[1]);
        }
        List<LayoutEngine.Box> boxes = LayoutEngine.layout(this.direction, container, this.padding, this.gap, this.align, this.justify, items);
        List<CanvasElement> placed = new ArrayList<>(boxes.size());
        for (int index = 0; index < boxes.size(); index++) {
            LayoutEngine.Box child = boxes.get(index);
            placed.add(shown.get(index).element().placed(child.x(), Math.max(0, child.y()), child.width(), child.height()));
        }
        return placed;
    }

    /**
     * @return Every combination of collapsed children the pack must hold, the empty one first.
     */
    private @NotNull List<Set<Integer>> collapseCombinations() {
        List<Integer> collapsing = this.collapsing();
        List<Set<Integer>> combinations = new ArrayList<>(1 << collapsing.size());
        for (int mask = 0; mask < 1 << collapsing.size(); mask++) {
            Set<Integer> combination = new HashSet<>();
            for (int bit = 0; bit < collapsing.size(); bit++) {
                if ((mask & 1 << bit) != 0) combination.add(collapsing.get(bit));
            }
            combinations.add(combination);
        }
        return combinations;
    }

    @Override
    public boolean isClickable() {
        return this.children.stream().anyMatch(child -> child.element() instanceof CanvasElement element && element.isClickable());
    }

    @Override
    public boolean isStatic() {
        return this.children.stream().allMatch(child -> child.element() instanceof CanvasElement element && element.isStatic());
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        Function<String, Optional<ResolvedGlyph>> glyphs = reference -> Optional.ofNullable(variants.resolve(reference));
        TextElement growing = this.growingText();
        for (Set<Integer> collapsed : this.collapseCombinations()) {
            if (growing == null) {
                for (CanvasElement child : this.place(glyphs, null, collapsed)) child.collectVariants(variants);
                continue;
            }
            for (int lines = 1; lines <= growing.maxLines(); lines++) {
                for (CanvasElement child : this.place(glyphs, lines, collapsed)) child.collectVariants(variants);
            }
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        for (CanvasElement child : this.placeFor(context, builder)) child.render(context, builder);
    }

    @Override
    public boolean hasOverlay() {
        return this.children.stream().anyMatch(child -> child.element() instanceof CanvasElement element && element.hasOverlay());
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        for (CanvasElement child : this.placeFor(context, builder)) {
            if (child.hasOverlay()) child.renderOverlay(context, builder);
        }
    }

    private @NotNull List<CanvasElement> placeFor(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        TextElement growing = this.growingText();
        Integer lines = growing == null ? null : growing.drawnLines(context, builder).size();
        Set<Integer> collapsed = new HashSet<>();
        for (int index : this.collapsing()) {
            if (!ConditionalElement.isVisible(this.children.get(index).element(), context)) collapsed.add(index);
        }
        return this.place(context.canvasManager()::resolveGlyph, lines, collapsed);
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        section.set("type", this.direction.name().toLowerCase(Locale.ROOT));
        if (this.x != 0) section.set("x", this.x);
        if (this.y != 0) section.set("y", this.y);
        section.set("width", this.width.serialize());
        section.set("height", this.height.serialize());
        if (!this.padding.equals(LayoutEngine.Insets.NONE)) {
            section.set("padding", List.of(this.padding.top(), this.padding.right(), this.padding.bottom(), this.padding.left()));
        }
        if (this.gap != 0) section.set("gap", this.gap);
        if (this.align != LayoutEngine.Align.START) section.set("align", this.align.name().toLowerCase(Locale.ROOT));
        if (this.justify != LayoutEngine.Justify.START) section.set("justify", this.justify.name().toLowerCase(Locale.ROOT).replace('_', '-'));

        ConfigurationSection childrenSection = section.createSection("children");
        for (Child child : this.children) {
            CanvasElement element = (CanvasElement) child.element();
            ConfigurationSection childSection = childrenSection.createSection(element.name());
            element.serialize(childSection);
            if (child.collapse()) childSection.set("collapse", true);
            if (ConditionalElement.unwrap(element) instanceof LayoutElement) continue;
            childSection.set("x", null);
            childSection.set("y", null);
            if (child.width() != SizeSpec.FIT) childSection.set("layout-width", child.width().serialize());
            if (child.growText()) {
                childSection.set("layout-height", LayoutElementLoader.GROW_TEXT);
            } else if (child.height() != SizeSpec.FIT) {
                childSection.set("layout-height", child.height().serialize());
            }
        }
    }
}
