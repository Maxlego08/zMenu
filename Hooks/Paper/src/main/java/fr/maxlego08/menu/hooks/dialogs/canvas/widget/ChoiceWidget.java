package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * One value out of a list of options: {@code type: tabs} draws each option as a panel, {@code type: radio} as
 * a glyph with its label.
 *
 * <pre>
 * section:
 *   type: tabs
 *   default: general              # the first option by default
 *   x: 0
 *   y: 0
 *   direction: row                # row (default for tabs) or column (default for radio)
 *   gap: 2
 *   height: 14                    # the height of an option
 *   options:
 *     general: { label: "General", width: 70 }
 *     audio:   { label: "Audio",   width: 70 }
 *   panel-selected: widgets/tab_on  # tabs: panel textures, stretched to each option
 *   panel-idle: widgets/tab_off
 *   border: 3
 *   # glyph-selected / glyph-idle    # radio: the glyph left of each label
 *   pages:                         # optional: elements drawn only while their option is selected
 *     general:
 *       hello: { type: text, text: "Hello", x: 4, y: 20 }
 * </pre>
 * Options can take any shape: an option with {@code x} or {@code y} (relative to the widget) leaves the row or
 * column, and can have its own look, label and tooltip:
 * <pre>
 *     shop: { label: "Shop", width: 40, x: 120, y: 15, label-selected: "&lt;gold&gt;Shop",
 *             panel-selected: my_pack/shop_on, panel-idle: my_pack/shop_off, hover: ["&lt;gray&gt;Buy things"] }
 * </pre>
 * {@code label} and {@code label-selected} at the widget level are parts (see {@link WidgetPart}) placing the
 * label inside each option (relative to the option), their text comes from the option. Plus the keys of
 * {@link WidgetSettings}.
 */
public final class ChoiceWidget extends WidgetElement {

    private static final int TEXT_HEIGHT = 8;
    private static final int LABEL_GAP = 4;

    /**
     * How the options look.
     */
    public enum Style {
        TABS,
        RADIO;

        /**
         * @return The {@code type} of the widget.
         */
        public @NotNull String type() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * An option.
     *
     * @param id            The value it sets.
     * @param label         Its label.
     * @param width         Its width.
     * @param x             Its x relative to the widget, or null to follow the others.
     * @param y             Its y relative to the widget, or null to follow the others.
     * @param labelSelected Its label while selected (or checked), or null for its label.
     * @param lookSelected  Its own selected (or checked) glyph or panel, or null for the widget's.
     * @param lookIdle      Its own idle glyph or panel, or null for the widget's.
     * @param hover         Its own tooltip, empty for the widget's.
     */
    public record Option(@NotNull String id, @NotNull String label, int width, @Nullable Integer x, @Nullable Integer y, @Nullable String labelSelected,
                         @Nullable String lookSelected, @Nullable String lookIdle, @NotNull List<String> hover) {

        public Option {
            hover = List.copyOf(hover);
        }

        public Option(@NotNull String id, @NotNull String label, int width) {
            this(id, label, width, null, null, null, null, null, List.of());
        }

        /**
         * @return True if the option has its own position.
         */
        public boolean isPlaced() {
            return this.x != null || this.y != null;
        }

        /**
         * @param selected True for the selected (or checked) state.
         * @return The label in that state.
         */
        public @NotNull String label(boolean selected) {
            return selected && this.labelSelected != null ? this.labelSelected : this.label;
        }

        /**
         * Writes the option back, with the keys the widget type names its looks with.
         *
         * @param section       The options section.
         * @param selectedKey   The key of the selected look.
         * @param idleKey       The key of the idle look.
         * @param labelSelected The key of the label while selected.
         */
        public void serialize(@NotNull ConfigurationSection section, @NotNull String selectedKey, @NotNull String idleKey, @NotNull String labelSelected) {
            ConfigurationSection option = section.createSection(this.id);
            option.set("label", this.label);
            option.set("width", this.width);
            if (this.x != null) option.set("x", this.x);
            if (this.y != null) option.set("y", this.y);
            if (this.labelSelected != null) option.set(labelSelected, this.labelSelected);
            if (this.lookSelected != null) option.set(selectedKey, this.lookSelected);
            if (this.lookIdle != null) option.set(idleKey, this.lookIdle);
            if (!this.hover.isEmpty()) option.set("hover", this.hover);
        }
    }

    /**
     * Places options: those without a position of their own follow each other in a row or a column.
     *
     * @param options The options.
     * @param column  True for a column.
     * @param gap     The distance between two options.
     * @param height  The height of an option.
     * @return The x and y of each option, relative to the widget.
     */
    public static int @NotNull [][] positions(@NotNull List<Option> options, boolean column, int gap, int height) {
        int[][] positions = new int[options.size()][];
        int cursor = 0;
        for (int index = 0; index < options.size(); index++) {
            Option option = options.get(index);
            if (option.isPlaced()) {
                positions[index] = new int[]{option.x() == null ? 0 : option.x(), option.y() == null ? 0 : option.y()};
                continue;
            }
            positions[index] = column ? new int[]{0, cursor} : new int[]{cursor, 0};
            cursor += (column ? height : option.width()) + gap;
        }
        return positions;
    }

    private final Style style;
    private final List<Option> options;
    private final boolean column;
    private final int gap;
    private final int height;
    private final String selected;
    private final String idle;
    private final int border;
    private final String font;
    private final WidgetPart label;
    private final WidgetPart labelSelected;
    private final Map<String, List<CanvasElement>> pages;
    private final int[][] positions;

    public ChoiceWidget(@NotNull WidgetSettings settings, @NotNull Style style, @NotNull List<Option> options, boolean column, int gap, int height,
                        @NotNull String selected, @NotNull String idle, int border, @NotNull String font, @NotNull Map<String, List<CanvasElement>> pages) {
        this(settings, style, options, column, gap, height, selected, idle, border, font, WidgetPart.NONE, WidgetPart.NONE, pages);
    }

    public ChoiceWidget(@NotNull WidgetSettings settings, @NotNull Style style, @NotNull List<Option> options, boolean column, int gap, int height,
                        @NotNull String selected, @NotNull String idle, int border, @NotNull String font, @NotNull WidgetPart label,
                        @NotNull WidgetPart labelSelected, @NotNull Map<String, List<CanvasElement>> pages) {
        super(settings);
        this.style = style;
        this.options = List.copyOf(options);
        this.column = column;
        this.gap = gap;
        this.height = height;
        this.selected = selected;
        this.idle = idle;
        this.border = border;
        this.font = font;
        this.label = label;
        this.labelSelected = labelSelected;
        Map<String, List<CanvasElement>> copy = new LinkedHashMap<>();
        pages.forEach((id, elements) -> copy.put(id, List.copyOf(elements)));
        this.pages = copy;
        this.positions = positions(this.options, column, gap, height);
    }

    /**
     * @param value A value.
     * @return The option of the value, the first option for an unknown value.
     */
    public @NotNull Option current(@NotNull String value) {
        for (Option option : this.options) {
            if (option.id().equals(value)) return option;
        }
        return this.options.getFirst();
    }

    private int optionX(int index) {
        return this.settings.x() + this.positions[index][0];
    }

    private int optionY(int index) {
        return this.settings.y() + this.positions[index][1];
    }

    private @NotNull String look(@NotNull Option option, boolean chosen) {
        String own = chosen ? option.lookSelected() : option.lookIdle();
        String reference = own == null ? (chosen ? this.selected : this.idle) : own;
        return this.style == Style.TABS ? PanelGlyphProvider.reference(reference, option.width(), this.height, this.border) : reference;
    }

    private @NotNull WidgetPart labelPart(boolean chosen) {
        return chosen ? this.labelSelected.orElse(this.label) : this.label;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int textY = Math.max(0, (this.height - TEXT_HEIGHT) / 2);
        for (int index = 0; index < this.options.size(); index++) {
            Option option = this.options.get(index);
            int y = this.optionY(index);
            this.glyph(variants, this.look(option, true), y);
            this.glyph(variants, this.look(option, false), y);
            collectTexts(variants, List.of(this.labelPart(false), this.labelPart(true)), option.label(), y, textY, this.font);
            variants.area(y, option.width(), this.height);
        }
        for (List<CanvasElement> page : this.pages.values()) {
            for (CanvasElement element : page) element.collectVariants(variants);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        Option current = this.current(this.value(context));
        int textY = Math.max(0, (this.height - TEXT_HEIGHT) / 2);
        for (int index = 0; index < this.options.size(); index++) {
            Option option = this.options.get(index);
            boolean chosen = option == current;
            int x = this.optionX(index);
            int y = this.optionY(index);
            String reference = this.look(option, chosen);
            this.glyph(context, builder, reference, x, y);
            WidgetPart part = this.labelPart(chosen).withText(option.label(chosen));
            if (this.style == Style.TABS) {
                this.drawText(context, builder, part, null, option.id(), x, y, 0, textY, option.width(), HAlign.CENTER, this.font);
            } else {
                int glyphWidth = context.canvasManager().resolveGlyph(reference).map(ResolvedGlyph::visualWidth).orElse(0);
                this.drawText(context, builder, part, null, option.id(), x, y, glyphWidth + LABEL_GAP, textY, 0, HAlign.LEFT, this.font);
            }
            builder.area(x, y, option.width(), this.height, chosen ? null : this.change(context, option.id()), this.hover(context, option.id(), option.hover()));
        }
        for (CanvasElement element : this.pages.getOrDefault(current.id(), List.of())) element.render(context, builder);
    }

    @Override
    public boolean hasOverlay() {
        return this.pages.values().stream().flatMap(List::stream).anyMatch(CanvasElement::hasOverlay);
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        Option current = this.current(this.value(context));
        for (CanvasElement element : this.pages.getOrDefault(current.id(), List.of())) {
            if (element.hasOverlay()) element.renderOverlay(context, builder);
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        int right = 0;
        int bottom = 0;
        for (int index = 0; index < this.options.size(); index++) {
            right = Math.max(right, this.optionX(index) + this.options.get(index).width());
            bottom = Math.max(bottom, this.optionY(index) + this.height);
        }
        List<String> problems = new ArrayList<>(outside(right, bottom, canvasWidth, canvasHeight, "the options"));
        long flowing = this.options.stream().filter(option -> !option.isPlaced()).count();
        if (this.column && flowing > 1 && this.height + this.gap < CLICK_ROW) {
            problems.add("its options are " + (this.height + this.gap) + " pixels apart: clients before 1.21.11 find a click by its line of "
                    + CLICK_ROW + " pixels and may pick the wrong option (height + gap of " + CLICK_ROW + " or more)");
        }
        return problems;
    }

    @Override
    public @NotNull List<CanvasElement> children() {
        return this.pages.values().stream().flatMap(List::stream).toList();
    }

    /**
     * @return The elements of each option, by option id.
     */
    public @NotNull Map<String, List<CanvasElement>> pages() {
        return this.pages;
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        boolean tabs = this.style == Style.TABS;
        this.settings.serialize(section, this.style.type(), this.options.getFirst().id());
        if (this.column != (this.style == Style.RADIO)) section.set("direction", this.column ? "column" : "row");
        if (this.gap != 2) section.set("gap", this.gap);
        section.set("height", this.height);
        ConfigurationSection options = section.createSection("options");
        for (Option option : this.options) {
            option.serialize(options, tabs ? "panel-selected" : "glyph-selected", tabs ? "panel-idle" : "glyph-idle", "label-selected");
        }
        section.set(tabs ? "panel-selected" : "glyph-selected", this.selected);
        section.set(tabs ? "panel-idle" : "glyph-idle", this.idle);
        if (tabs) section.set("border", this.border);
        if (!this.font.equals("default")) section.set("font", this.font);
        this.label.serialize(section, "label", false);
        this.labelSelected.serialize(section, "label-selected", false);
        if (!this.pages.isEmpty()) {
            ConfigurationSection pagesSection = section.createSection("pages");
            this.pages.forEach((id, elements) -> {
                ConfigurationSection page = pagesSection.createSection(id);
                for (CanvasElement element : elements) element.serialize(page.createSection(element.name()));
            });
        }
    }

    /**
     * @param id An option id.
     * @return The option, if it exists.
     */
    public @NotNull Optional<Option> option(@Nullable String id) {
        return this.options.stream().filter(option -> option.id().equals(id)).findFirst();
    }
}
