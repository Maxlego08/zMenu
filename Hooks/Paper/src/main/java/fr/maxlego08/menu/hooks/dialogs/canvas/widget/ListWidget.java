package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Entries shown a page at a time, each drawn with a template.
 *
 * <pre>
 * shop:
 *   type: list
 *   entries: [sword, bow, apple, bread, shield]   # or a placeholder giving a list: "%my_plugin_items%"
 *   separator: ","                # splits a placeholder result
 *   per-page: 3
 *   x: 10
 *   y: 20
 *   columns: 1                    # slots per row: 1 (a column, default), per-page (a row), or a grid
 *                                 # (direction: row is the same as columns: per-page)
 *   slot-width: 200
 *   slot-height: 16
 *   gap: 2
 *   template:                     # one element placed in each slot, like a child of a row or a column
 *     type: row
 *     children:
 *       icon: { type: image, glyph: "minecraft:item/%entry%", glyph-values: [diamond_sword, bow, apple] }
 *       name: { type: text, text: "&lt;white&gt;%entry%" }
 *   empty-text: "&lt;gray&gt;Nothing here"   # optional, at x and y when there is no entry
 *   filter: "%canvas_search%"     # optional: only the entries containing it (any case), all when it is empty
 *   filter-on: "%entry%"          # optional: the text searched for each entry, with %entry%, %index%...
 *   previous: { x: 10, y: 80, width: 30, height: 12 }
 *   next: { x: 180, y: 80, width: 30, height: 12 }
 *   page-text: { x: 10, y: 82, width: 200, align: center }
 * </pre>
 * In the template: {@code %entry%}, {@code %index%} (from 0) and {@code %number%} (from 1). Its pack keys
 * use {@code *-values} as anywhere, so an entry outside them draws the default. The value of the widget is
 * the page, from 1. Plus the keys of {@link WidgetSettings} and {@link PageNavigation}.
 */
public final class ListWidget extends PagedWidget {

    public static final String TYPE = "list";

    private final List<String> entries;
    private final String source;
    private final String separator;
    private final int perPage;
    private final int columns;
    private final int slotWidth;
    private final int slotHeight;
    private final int gap;
    private final CanvasLayoutable template;
    private final String emptyText;
    private final String font;
    private final String filter;
    private final String filterOn;

    public ListWidget(@NotNull WidgetSettings settings, @NotNull PageNavigation navigation, @NotNull List<String> entries, @Nullable String source,
                      @NotNull String separator, int perPage, int columns, int slotWidth, int slotHeight, int gap, @NotNull CanvasLayoutable template,
                      @Nullable String emptyText, @NotNull String font) {
        this(settings, navigation, entries, source, separator, perPage, columns, slotWidth, slotHeight, gap, template, emptyText, font, null, "%entry%");
    }

    public ListWidget(@NotNull WidgetSettings settings, @NotNull PageNavigation navigation, @NotNull List<String> entries, @Nullable String source,
                      @NotNull String separator, int perPage, int columns, int slotWidth, int slotHeight, int gap, @NotNull CanvasLayoutable template,
                      @Nullable String emptyText, @NotNull String font, @Nullable String filter, @NotNull String filterOn) {
        super(settings, navigation);
        this.entries = List.copyOf(entries);
        this.source = source;
        this.separator = separator;
        this.perPage = perPage;
        this.columns = Math.clamp(columns, 1, Math.max(1, perPage));
        this.slotWidth = slotWidth;
        this.slotHeight = slotHeight;
        this.gap = gap;
        this.template = template;
        this.emptyText = emptyText;
        this.font = font;
        this.filter = filter;
        this.filterOn = filterOn;
    }

    /**
     * @param context The player.
     * @return The entries for the player.
     */
    public @NotNull List<String> entries(@NotNull CanvasRenderContext context) {
        List<String> all = this.source == null ? this.entries : split(context.resolve(this.source), this.separator);
        if (this.filter == null) return all;
        String query = context.resolve(this.filter).trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) return all;
        List<String> kept = new ArrayList<>();
        for (int index = 0; index < all.size(); index++) {
            String entry = all.get(index);
            String searched = this.entryContext(context, entry, index).resolve(this.filterOn).toLowerCase(Locale.ROOT);
            if (searched.contains(query)) kept.add(entry);
        }
        return kept;
    }

    /**
     * @param text      A list as text.
     * @param separator What separates two entries.
     * @return The entries, trimmed, without empty ones.
     */
    public static @NotNull List<String> split(@NotNull String text, @NotNull String separator) {
        List<String> entries = new ArrayList<>();
        for (String entry : text.split(Pattern.quote(separator), -1)) {
            String trimmed = entry.trim();
            if (!trimmed.isEmpty()) entries.add(trimmed);
        }
        return entries;
    }

    /**
     * @param slot A slot of the page.
     * @return Its x.
     */
    public int slotX(int slot) {
        return this.settings.x() + (slot % this.columns) * (this.slotWidth + this.gap);
    }

    /**
     * @param slot A slot of the page.
     * @return Its y.
     */
    public int slotY(int slot) {
        return this.settings.y() + (slot / this.columns) * (this.slotHeight + this.gap);
    }

    /**
     * @return The number of rows of a page.
     */
    public int rows() {
        return (this.perPage + this.columns - 1) / this.columns;
    }

    /**
     * @param slot A slot of the page.
     * @return The template placed in the slot.
     */
    public @NotNull CanvasElement placed(int slot) {
        return this.template.placed(this.slotX(slot), this.slotY(slot), this.slotWidth, this.slotHeight);
    }

    /**
     * @param count The number of entries.
     * @return The number of pages, at least 1.
     */
    public int pages(int count) {
        return Math.max(1, (count + this.perPage - 1) / this.perPage);
    }

    private @NotNull CanvasRenderContext entryContext(@NotNull CanvasRenderContext context, @NotNull String entry, int index) {
        return context.with(Map.of("entry", entry, "index", String.valueOf(index), "number", String.valueOf(index + 1)));
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (int slot = 0; slot < this.perPage; slot++) this.placed(slot).collectVariants(variants);
        if (this.emptyText != null) variants.text(this.font, this.settings.y());
        this.collectNavigation(variants);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        List<String> entries = this.entries(context);
        int pages = this.pages(entries.size());
        int page = page(this.value(context), pages);
        CanvasRenderContext pageContext = pageContext(context, page, pages);

        if (entries.isEmpty() && this.emptyText != null) {
            builder.text(pageContext.format(this.emptyText, this.settings.escapePlaceholders()), this.font, this.settings.x(), this.settings.y());
        }
        for (int slot = 0; slot < this.perPage; slot++) {
            int index = page * this.perPage + slot;
            if (index >= entries.size()) break;
            this.placed(slot).render(this.entryContext(pageContext, entries.get(index), index), builder);
        }
        this.renderNavigation(pageContext, builder, page, pages);
    }

    @Override
    public boolean hasOverlay() {
        return this.template instanceof CanvasElement element && element.hasOverlay();
    }

    @Override
    public void renderOverlay(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        List<String> entries = this.entries(context);
        int pages = this.pages(entries.size());
        int page = page(this.value(context), pages);
        CanvasRenderContext pageContext = pageContext(context, page, pages);
        for (int slot = 0; slot < this.perPage; slot++) {
            int index = page * this.perPage + slot;
            if (index >= entries.size()) break;
            this.placed(slot).renderOverlay(this.entryContext(pageContext, entries.get(index), index), builder);
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        List<String> problems = new ArrayList<>(outside(this.slotX(this.columns - 1) + this.slotWidth, this.slotY(this.perPage - 1) + this.slotHeight,
                canvasWidth, canvasHeight, "the slots"));
        boolean clickable = this.template instanceof CanvasElement element && element.isClickable();
        if (clickable && this.rows() > 1 && this.slotHeight + this.gap < CLICK_ROW) {
            problems.add("its clickable slots are " + (this.slotHeight + this.gap) + " pixels apart: clients before 1.21.11 find a click by its line of "
                    + CLICK_ROW + " pixels and may click the wrong entry (slot-height + gap of " + CLICK_ROW + " or more)");
        }
        return problems;
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "1");
        if (this.source == null) {
            section.set("entries", this.entries);
        } else {
            section.set("entries", this.source);
            if (!this.separator.equals(",")) section.set("separator", this.separator);
        }
        section.set("per-page", this.perPage);
        if (this.columns != 1) section.set("columns", this.columns);
        if (this.filter != null) section.set("filter", this.filter);
        if (!this.filterOn.equals("%entry%")) section.set("filter-on", this.filterOn);
        section.set("slot-width", this.slotWidth);
        section.set("slot-height", this.slotHeight);
        if (this.gap != 0) section.set("gap", this.gap);
        if (this.template instanceof CanvasElement element) element.serialize(section.createSection("template"));
        if (this.emptyText != null) section.set("empty-text", this.emptyText);
        if (!this.font.equals("default")) section.set("font", this.font);
        this.navigation.serialize(section);
    }
}
