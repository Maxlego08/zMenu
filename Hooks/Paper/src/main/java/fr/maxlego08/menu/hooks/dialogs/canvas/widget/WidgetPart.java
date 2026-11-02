package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ElementSections;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A text or a glyph a widget draws, such as the label of a toggle or the minus button of a stepper. It is
 * written as a value, drawn where the widget puts it, or as a section to change it:
 *
 * <pre>
 * label: "&lt;white&gt;Music"            # only the text (or the glyph)
 * label:
 *   text: "&lt;white&gt;Music: %value%"
 *   x: 0                           # relative to the widget (or to its option); where the widget puts it by default
 *   y: 12
 *   width: 80                      # a box: the text is cut to fit and aligned in it
 *   align: center                  # left, center or right in the width
 *   font: default
 *   hover: ["&lt;gray&gt;..."]           # the tooltip of this part, when it takes clicks
 * minus:
 *   glyph: my_pack/minus
 *   glyph-disabled: my_pack/minus_off
 *   x: 0
 *   y: 0
 *   hover: ["&lt;gray&gt;One less"]
 * </pre>
 * Every key is optional: what is left out keeps the widget's default.
 *
 * @param text          The text, or null for the widget's.
 * @param glyph         The glyph, or null for the widget's.
 * @param glyphDisabled The glyph when the part takes no click, or null for the widget's.
 * @param x             The x relative to the widget, or null for the widget's.
 * @param y             The y relative to the widget, or null for the widget's.
 * @param width         The width of the text box, or null for the widget's.
 * @param align         The alignment in the width, or null for the widget's.
 * @param font          The font, or null for the widget's.
 * @param hover         The tooltip lines, empty for the widget's.
 */
public record WidgetPart(@Nullable String text, @Nullable String glyph, @Nullable String glyphDisabled, @Nullable Integer x, @Nullable Integer y,
                         @Nullable Integer width, @Nullable HAlign align, @Nullable String font, @NotNull List<String> hover) {

    /**
     * A part that keeps every default.
     */
    public static final WidgetPart NONE = new WidgetPart(null, null, null, null, null, null, null, null, List.of());

    private static final Set<String> KEYS = Set.of("text", "glyph", "glyph-disabled", "x", "y", "width", "align", "font", "hover");

    public WidgetPart {
        hover = List.copyOf(hover);
    }

    /**
     * @param text A text.
     * @return A part with only this text, or {@link #NONE} for null.
     */
    public static @NotNull WidgetPart ofText(@Nullable String text) {
        return text == null ? NONE : new WidgetPart(text, null, null, null, null, null, null, null, List.of());
    }

    /**
     * @param glyph A glyph reference.
     * @return A part with only this glyph, or {@link #NONE} for null.
     */
    public static @NotNull WidgetPart ofGlyph(@Nullable String glyph) {
        return glyph == null ? NONE : new WidgetPart(null, glyph, null, null, null, null, null, null, List.of());
    }

    /**
     * Reads a part: a value (a text, or a glyph for a glyph part) or a section.
     *
     * @param parent    The widget section.
     * @param key       The key of the part.
     * @param glyphPart True if a value is a glyph, false if it is a text.
     * @param name      The widget name, for error messages.
     * @param source    Where it is read from, for error messages.
     * @return The part, {@link #NONE} when the key is missing.
     */
    public static @NotNull WidgetPart read(@NotNull ConfigurationSection parent, @NotNull String key, boolean glyphPart, @NotNull String name,
                                           @NotNull String source) {
        if (!parent.contains(key)) return NONE;
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) {
            String value = String.valueOf(parent.get(key));
            return glyphPart ? ofGlyph(value) : ofText(value);
        }
        ElementSections.rejectUnknownKeys(section, KEYS, name + "." + key, source);
        HAlign align = null;
        if (section.contains("align")) {
            try {
                align = HAlign.valueOf(String.valueOf(section.get("align")).toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException exception) {
                throw ElementSections.error(name, source, "needs " + key + ".align left, center or right");
            }
        }
        Integer width = section.contains("width") ? ElementSections.requireInt(section, "width", name, source) : null;
        if (width != null && width < 1) throw ElementSections.error(name, source, "needs a " + key + ".width of at least 1");
        return new WidgetPart(section.getString("text"), section.getString("glyph"), section.getString("glyph-disabled"),
                section.contains("x") ? ElementSections.requireInt(section, "x", name, source) : null,
                section.contains("y") ? ElementSections.requireInt(section, "y", name, source) : null, width, align, section.getString("font"),
                section.isList("hover") ? section.getStringList("hover") : section.contains("hover") ? List.of(String.valueOf(section.get("hover"))) : List.of());
    }

    /**
     * @param fallback The widget's text.
     * @return The text of this part, else the fallback.
     */
    public @Nullable String text(@Nullable String fallback) {
        return this.text == null ? fallback : this.text;
    }

    /**
     * @param fallback The widget's glyph.
     * @return The glyph of this part, else the fallback.
     */
    public @Nullable String glyph(@Nullable String fallback) {
        return this.glyph == null ? fallback : this.glyph;
    }

    /**
     * @param fallback The widget's glyph when the part takes no click.
     * @return The disabled glyph of this part, else the fallback.
     */
    public @Nullable String glyphDisabled(@Nullable String fallback) {
        return this.glyphDisabled == null ? fallback : this.glyphDisabled;
    }

    public int x(int fallback) {
        return this.x == null ? fallback : this.x;
    }

    public int y(int fallback) {
        return this.y == null ? fallback : this.y;
    }

    public int width(int fallback) {
        return this.width == null ? fallback : this.width;
    }

    public @NotNull HAlign align(@NotNull HAlign fallback) {
        return this.align == null ? fallback : this.align;
    }

    public @NotNull String font(@NotNull String fallback) {
        return this.font == null ? fallback : this.font;
    }

    /**
     * @param text A text.
     * @return This part with that text, for the placement of a label whose text comes from its option.
     */
    public @NotNull WidgetPart withText(@Nullable String text) {
        return new WidgetPart(text, this.glyph, this.glyphDisabled, this.x, this.y, this.width, this.align, this.font, this.hover);
    }

    /**
     * @return True if a key of this part is set.
     */
    public boolean isSet() {
        return this.text != null || this.glyph != null || this.glyphDisabled != null || this.x != null || this.y != null || this.width != null
                || this.align != null || this.font != null || !this.hover.isEmpty();
    }

    /**
     * @param other A part to take the missing keys from, such as {@code label} for {@code label-on}.
     * @return This part, its missing keys taken from the other.
     */
    public @NotNull WidgetPart orElse(@NotNull WidgetPart other) {
        return new WidgetPart(this.text == null ? other.text : this.text, this.glyph == null ? other.glyph : this.glyph,
                this.glyphDisabled == null ? other.glyphDisabled : this.glyphDisabled, this.x == null ? other.x : this.x, this.y == null ? other.y : this.y,
                this.width == null ? other.width : this.width, this.align == null ? other.align : this.align, this.font == null ? other.font : this.font,
                this.hover.isEmpty() ? other.hover : this.hover);
    }

    /**
     * Writes the part back: as a value when it only sets its text (or glyph), else as a section.
     *
     * @param parent    The widget section.
     * @param key       The key of the part.
     * @param glyphPart True if a value is a glyph.
     */
    public void serialize(@NotNull ConfigurationSection parent, @NotNull String key, boolean glyphPart) {
        if (!this.isSet()) return;
        boolean onlyValue = this.glyphDisabled == null && this.x == null && this.y == null && this.width == null && this.align == null && this.font == null
                && this.hover.isEmpty() && (glyphPart ? this.text == null && this.glyph != null : this.glyph == null && this.text != null);
        if (onlyValue) {
            parent.set(key, glyphPart ? this.glyph : this.text);
            return;
        }
        ConfigurationSection section = parent.createSection(key);
        if (this.text != null) section.set("text", this.text);
        if (this.glyph != null) section.set("glyph", this.glyph);
        if (this.glyphDisabled != null) section.set("glyph-disabled", this.glyphDisabled);
        if (this.x != null) section.set("x", this.x);
        if (this.y != null) section.set("y", this.y);
        if (this.width != null) section.set("width", this.width);
        if (this.align != null) section.set("align", this.align.name().toLowerCase(Locale.ROOT));
        if (this.font != null) section.set("font", this.font);
        if (!this.hover.isEmpty()) section.set("hover", this.hover);
    }
}
