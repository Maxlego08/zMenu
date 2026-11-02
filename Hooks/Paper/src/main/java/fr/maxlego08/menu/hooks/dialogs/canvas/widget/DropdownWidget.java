package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One value out of a list that opens over the canvas.
 *
 * <pre>
 * language:
 *   type: dropdown
 *   default: en                  # the first option by default
 *   x: 20
 *   y: 30
 *   width: 100
 *   height: 14                   # the closed box
 *   options:
 *     en: "English"
 *     fr: "Français"
 *   panel: widgets/dropdown      # panel texture of the box
 *   list-panel: widgets/dropdown # optional, of the open list (the box's by default)
 *   selected-panel: widgets/dropdown_selected  # optional, behind the selected option
 *   border: 3
 *   arrow: widgets/arrow_down    # optional glyph on the right of the box
 *   option-height: 12            # 9 or more: older clients find clicks by 9 pixel rows
 *   max-visible: 5               # more options add a page row: previous-text / next-text
 * </pre>
 * An option is {@code id: label} or {@code id: { label, hover }}. The parts (see {@link WidgetPart}) move the
 * pieces, relative to the widget unless said: {@code text} (the chosen label in the box), {@code arrow} (a
 * glyph part), {@code list} (its x and y: where the open list appears, under the box by default) and
 * {@code option-text} (the label in each row, relative to the row). A {@code text} or {@code option-text}
 * with a text of its own shows the label with {@code %label%}: {@code text: { text: "&lt;gray&gt;Language: %label%" }}.
 * <p>
 * Plus the keys of {@link WidgetSettings}. The list is drawn in the overlay pass, over every element; a click
 * outside it closes it. Its open state is the key {@code <key>_open}, its page {@code <key>_page}.
 */
public final class DropdownWidget extends WidgetElement {

    public static final String TYPE = "dropdown";
    private static final int TEXT_HEIGHT = 8;
    private static final int TEXT_PADDING = 2;

    /**
     * An option.
     *
     * @param id    The value it sets.
     * @param label Its label.
     * @param hover Its tooltip in the open list, empty for none.
     */
    public record Option(@NotNull String id, @NotNull String label, @NotNull List<String> hover) {

        public Option {
            hover = List.copyOf(hover);
        }

        public Option(@NotNull String id, @NotNull String label) {
            this(id, label, List.of());
        }
    }

    private final List<Option> options;
    private final int width;
    private final int height;
    private final String panel;
    private final String listPanel;
    private final String selectedPanel;
    private final int border;
    private final WidgetPart arrow;
    private final int optionHeight;
    private final int maxVisible;
    private final String previousText;
    private final String nextText;
    private final String font;
    private final WidgetPart text;
    private final WidgetPart list;
    private final WidgetPart optionText;

    public DropdownWidget(@NotNull WidgetSettings settings, @NotNull List<Option> options, int width, int height, @NotNull String panel,
                          @NotNull String listPanel, @Nullable String selectedPanel, int border, @Nullable String arrow, int optionHeight, int maxVisible,
                          @NotNull String previousText, @NotNull String nextText, @NotNull String font) {
        this(settings, options, width, height, panel, listPanel, selectedPanel, border, WidgetPart.ofGlyph(arrow), optionHeight, maxVisible, previousText,
                nextText, font, WidgetPart.NONE, WidgetPart.NONE, WidgetPart.NONE);
    }

    public DropdownWidget(@NotNull WidgetSettings settings, @NotNull List<Option> options, int width, int height, @NotNull String panel,
                          @NotNull String listPanel, @Nullable String selectedPanel, int border, @NotNull WidgetPart arrow, int optionHeight, int maxVisible,
                          @NotNull String previousText, @NotNull String nextText, @NotNull String font, @NotNull WidgetPart text, @NotNull WidgetPart list,
                          @NotNull WidgetPart optionText) {
        super(settings);
        this.text = text;
        this.list = list;
        this.optionText = optionText;
        this.options = List.copyOf(options);
        this.width = width;
        this.height = height;
        this.panel = panel;
        this.listPanel = listPanel;
        this.selectedPanel = selectedPanel;
        this.border = border;
        this.arrow = arrow;
        this.optionHeight = optionHeight;
        this.maxVisible = maxVisible;
        this.previousText = previousText;
        this.nextText = nextText;
        this.font = font;
    }

    private @NotNull String openKey() {
        return this.settings.key() + "_open";
    }

    private @NotNull String pageKey() {
        return this.settings.key() + "_page";
    }

    /**
     * @return True if the options do not fit in one page.
     */
    public boolean isPaged() {
        return this.options.size() > this.maxVisible;
    }

    /**
     * @return The number of options on a page.
     */
    public int pageSize() {
        return this.isPaged() ? this.maxVisible - 1 : this.options.size();
    }

    /**
     * @return The number of pages.
     */
    public int pages() {
        return (this.options.size() + this.pageSize() - 1) / this.pageSize();
    }

    /**
     * @return The number of rows of the open list, the page row included.
     */
    public int rows() {
        return this.isPaged() ? this.maxVisible : this.options.size();
    }

    /**
     * @return The y of the top of the open list.
     */
    public int listTop() {
        return this.settings.y() + this.list.y(this.height);
    }

    /**
     * @return The x of the left edge of the open list.
     */
    public int listX() {
        return this.settings.x() + this.list.x(0);
    }

    /**
     * @return The height of the open list.
     */
    public int listHeight() {
        return this.rows() * this.optionHeight + 2 * this.border;
    }

    /**
     * @param row A row of the open list.
     * @return Its y.
     */
    public int rowY(int row) {
        return this.listTop() + this.border + row * this.optionHeight;
    }

    /**
     * @param value A value.
     * @return Its option, the first one for an unknown value.
     */
    public @NotNull Option current(@NotNull String value) {
        return this.options.stream().filter(option -> option.id().equals(value)).findFirst().orElse(this.options.getFirst());
    }

    private static int textY(int top, int height) {
        return top + Math.max(0, (height - TEXT_HEIGHT) / 2);
    }

    private static int textOffset(int height) {
        return Math.max(0, (height - TEXT_HEIGHT) / 2);
    }

    private int[] arrowSize(@NotNull java.util.function.Function<String, Optional<ResolvedGlyph>> glyphs) {
        String reference = this.arrow.glyph(null);
        if (reference == null) return new int[]{0, 0};
        return glyphs.apply(reference).map(glyph -> new int[]{glyph.visualWidth(), glyph.height()}).orElse(new int[]{0, 0});
    }

    private int arrowX(int arrowWidth) {
        return this.arrow.x(this.width - this.border - arrowWidth - 1);
    }

    private int arrowY(int arrowHeight) {
        return this.arrow.y(Math.max(0, (this.height - arrowHeight) / 2));
    }

    private int selectedBorder() {
        return Math.min(this.border, this.optionHeight / 2);
    }

    private int innerWidth() {
        return this.width - 2 * this.border;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int y = this.settings.y();
        this.glyph(variants, PanelGlyphProvider.reference(this.panel, this.width, this.height, this.border), y);
        int[] arrow = this.arrowSize(reference -> Optional.ofNullable(variants.resolve(reference)));
        this.glyph(variants, this.arrow.glyph(null), y + this.arrowY(arrow[1]));
        collectText(variants, this.text, "", y, textOffset(this.height), this.font);
        variants.area(y, this.width, this.height);

        variants.beginOverlay();
        coverCanvas(variants);
        this.glyph(variants, PanelGlyphProvider.reference(this.listPanel, this.width, this.listHeight(), this.border), this.listTop());
        for (int row = 0; row < this.rows(); row++) {
            int rowY = this.rowY(row);
            boolean pageRow = this.isPaged() && row == this.rows() - 1;
            if (!pageRow && this.selectedPanel != null) {
                this.glyph(variants, PanelGlyphProvider.reference(this.selectedPanel, this.innerWidth(), this.optionHeight, this.selectedBorder()), rowY);
            }
            if (pageRow) {
                variants.text(this.font, textY(rowY, this.optionHeight));
            } else {
                collectText(variants, this.optionText, "", rowY, textOffset(this.optionHeight), this.font);
            }
            if (pageRow) {
                int half = this.innerWidth() / 2;
                variants.area(rowY, half, this.optionHeight);
                variants.area(rowY, this.innerWidth() - half, this.optionHeight);
            } else {
                variants.area(rowY, this.innerWidth(), this.optionHeight);
            }
        }
        variants.endOverlay();
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        Option current = this.current(value);
        int x = this.settings.x();
        int y = this.settings.y();
        boolean open = "true".equals(this.stored(context, this.openKey()));

        this.glyph(context, builder, PanelGlyphProvider.reference(this.panel, this.width, this.height, this.border), x, y);
        int[] arrow = this.arrowSize(context.canvasManager()::resolveGlyph);
        this.glyph(context, builder, this.arrow.glyph(null), x + this.arrowX(arrow[0]), y + this.arrowY(arrow[1]));
        this.drawText(context, builder, this.text.withText(this.text.text(current.label()).replace("%label%", current.label())), null, current.id(), x, y,
                this.border + TEXT_PADDING, textOffset(this.height), Math.max(1, this.width - 2 * this.border - 2 * TEXT_PADDING - arrow[0]), HAlign.LEFT,
                this.font);

        Map<String, String> toggle = new LinkedHashMap<>();
        toggle.put(this.openKey(), open ? null : "true");
        builder.area(x, y, this.width, this.height, this.isEnabled(context) ? this.update(context, toggle, null) : null, this.hover(context, current.id()));
    }

    @Override
    public boolean hasOverlay() {
        return true;
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        if (!"true".equals(this.stored(context, this.openKey()))) return;
        Option current = this.current(this.value(context));
        int x = this.listX();
        int page = Math.clamp((int) CanvasStateAction.number(this.stored(context, this.pageKey()), 0), 0, this.pages() - 1);

        Map<String, String> close = new LinkedHashMap<>();
        close.put(this.openKey(), null);
        coverCanvas(builder, this.update(context, close, null));

        this.glyph(context, builder, PanelGlyphProvider.reference(this.listPanel, this.width, this.listHeight(), this.border), x, this.listTop());
        int innerX = x + this.border;
        for (int row = 0; row < this.pageSize(); row++) {
            int index = page * this.pageSize() + row;
            if (index >= this.options.size()) break;
            Option option = this.options.get(index);
            int rowY = this.rowY(row);
            if (option == current && this.selectedPanel != null) {
                this.glyph(context, builder, PanelGlyphProvider.reference(this.selectedPanel, this.innerWidth(), this.optionHeight, this.selectedBorder()),
                        innerX, rowY);
            }
            this.drawText(context, builder, this.optionText.withText(this.optionText.text(option.label()).replace("%label%", option.label())), null,
                    option.id(), innerX, rowY, TEXT_PADDING, textOffset(this.optionHeight), Math.max(1, this.innerWidth() - 2 * TEXT_PADDING), HAlign.LEFT,
                    this.font);
            Map<String, String> choose = new LinkedHashMap<>();
            choose.put(this.settings.key(), option.id());
            choose.put(this.openKey(), null);
            choose.put(this.pageKey(), null);
            builder.area(innerX, rowY, this.innerWidth(), this.optionHeight, this.update(context, choose, option.id()),
                    option.hover().isEmpty() ? null : this.hover(context, option.id(), option.hover()));
        }

        if (!this.isPaged()) return;
        int rowY = this.rowY(this.rows() - 1);
        int half = this.innerWidth() / 2;
        int textY = textY(rowY, this.optionHeight);
        if (page > 0) {
            builder.text(this.text(context, this.previousText, String.valueOf(page)), this.font, innerX, textY, half, HAlign.CENTER);
            builder.area(innerX, rowY, half, this.optionHeight, this.update(context, Map.of(this.pageKey(), String.valueOf(page - 1)), null), null);
        }
        if (page < this.pages() - 1) {
            builder.text(this.text(context, this.nextText, String.valueOf(page + 2)), this.font, innerX + half, textY, this.innerWidth() - half, HAlign.CENTER);
            builder.area(innerX + half, rowY, this.innerWidth() - half, this.optionHeight,
                    this.update(context, Map.of(this.pageKey(), String.valueOf(page + 1)), null), null);
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        return outside(this.listX() + this.width, this.listTop() + this.listHeight(), canvasWidth, canvasHeight, "the open list");
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, this.options.getFirst().id());
        section.set("width", this.width);
        section.set("height", this.height);
        ConfigurationSection options = section.createSection("options");
        for (Option option : this.options) {
            if (option.hover().isEmpty()) {
                options.set(option.id(), option.label());
            } else {
                ConfigurationSection optionSection = options.createSection(option.id());
                optionSection.set("label", option.label());
                optionSection.set("hover", option.hover());
            }
        }
        section.set("panel", this.panel);
        if (!this.listPanel.equals(this.panel)) section.set("list-panel", this.listPanel);
        if (this.selectedPanel != null) section.set("selected-panel", this.selectedPanel);
        section.set("border", this.border);
        this.arrow.serialize(section, "arrow", true);
        this.text.serialize(section, "text", false);
        this.list.serialize(section, "list", false);
        this.optionText.serialize(section, "option-text", false);
        section.set("option-height", this.optionHeight);
        section.set("max-visible", this.maxVisible);
        section.set("previous-text", this.previousText);
        section.set("next-text", this.nextText);
        if (!this.font.equals("default")) section.set("font", this.font);
    }
}
