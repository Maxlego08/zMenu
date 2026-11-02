package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ElementSections;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The previous and next buttons and the page text of a paged widget:
 *
 * <pre>
 * previous: { x: 4, y: 100, width: 40, height: 12, text: "&lt;gray&gt;« Previous", glyph: widgets/prev, glyph-disabled: widgets/prev_off }
 * next: { x: 196, y: 100, width: 40, height: 12, text: "&lt;gray&gt;Next »" }
 * page-text: { x: 0, y: 102, width: 240, align: center, text: "&lt;white&gt;%page%/%pages%" }
 * </pre>
 * A button needs a glyph or a text (or both); at the first or the last page it shows {@code glyph-disabled}
 * (nothing without it) and takes no click.
 *
 * @param previous The previous button.
 * @param next     The next button.
 * @param pageText The page text, or null.
 */
public record PageNavigation(@NotNull Button previous, @NotNull Button next, @Nullable PageText pageText) {

    private static final int TEXT_HEIGHT = 8;

    /**
     * A button of the navigation.
     *
     * @param x             Its x.
     * @param y             Its y.
     * @param width         Its click width.
     * @param height        Its click height.
     * @param glyph         Its glyph, or null.
     * @param glyphDisabled Its glyph at the first or last page, or null.
     * @param text          Its text, centered, or null.
     * @param font          The font of the text.
     */
    public record Button(int x, int y, int width, int height, @Nullable String glyph, @Nullable String glyphDisabled, @Nullable String text,
                         @NotNull String font) {

        /**
         * @return The y of its text.
         */
        public int textY() {
            return this.y + Math.max(0, (this.height - TEXT_HEIGHT) / 2);
        }
    }

    /**
     * The page text: {@code %page%} and {@code %pages%}.
     *
     * @param x     Its x.
     * @param y     Its y.
     * @param width Its width, or 0 to draw it as it is.
     * @param align Its alignment in the width.
     * @param text  The text.
     * @param font  Its font.
     */
    public record PageText(int x, int y, int width, @NotNull HAlign align, @NotNull String text, @NotNull String font) {
    }

    /**
     * Declares the glyphs, texts and click areas of the navigation.
     *
     * @param variants The collector.
     */
    public void collectVariants(@NotNull CanvasVariants variants) {
        for (Button button : new Button[]{this.previous, this.next}) {
            for (String reference : new String[]{button.glyph(), button.glyphDisabled()}) {
                if (reference == null) continue;
                ResolvedGlyph glyph = variants.resolve(reference);
                if (glyph != null) variants.glyph(glyph, button.y());
            }
            if (button.text() != null) variants.text(button.font(), button.textY());
            variants.area(button.y(), button.width(), button.height());
        }
        if (this.pageText != null) variants.text(this.pageText.font(), this.pageText.y());
    }

    /**
     * Reads the navigation of a widget.
     *
     * @param section The widget section.
     * @param name    The widget name, for error messages.
     * @param source  Where it is read from, for error messages.
     * @return The navigation.
     */
    public static @NotNull PageNavigation read(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source) {
        return new PageNavigation(button(section, "previous", "<gray>«", name, source), button(section, "next", "<gray>»", name, source),
                pageText(section.getConfigurationSection("page-text"), name, source));
    }

    private static @NotNull Button button(@NotNull ConfigurationSection parent, @NotNull String key, @NotNull String defaultText, @NotNull String name,
                                          @NotNull String source) {
        ConfigurationSection section = parent.getConfigurationSection(key);
        if (section == null) throw ElementSections.error(name, source, "needs " + key + " with x and y");
        int width = section.getInt("width", 20);
        int height = section.getInt("height", 12);
        if (width < 1 || height < 1) throw ElementSections.error(name, source, "needs a " + key + " width and height of at least 1");
        String glyph = section.getString("glyph");
        String text = section.getString("text", glyph == null ? defaultText : null);
        return new Button(ElementSections.requireInt(section, "x", name, source), ElementSections.requireInt(section, "y", name, source), width, height,
                glyph, section.getString("glyph-disabled"), text, section.getString("font", "default"));
    }

    private static @Nullable PageText pageText(@Nullable ConfigurationSection section, @NotNull String name, @NotNull String source) {
        if (section == null) return null;
        HAlign align;
        try {
            align = HAlign.valueOf(section.getString("align", "left").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "needs page-text align left, center or right");
        }
        return new PageText(ElementSections.requireInt(section, "x", name, source), ElementSections.requireInt(section, "y", name, source),
                Math.max(0, section.getInt("width", 0)), align, section.getString("text", "<white>%page%/%pages%"), section.getString("font", "default"));
    }

    /**
     * Writes the navigation back.
     *
     * @param section The widget section.
     */
    public void serialize(@NotNull ConfigurationSection section) {
        write(section.createSection("previous"), this.previous);
        write(section.createSection("next"), this.next);
        if (this.pageText != null) {
            ConfigurationSection text = section.createSection("page-text");
            text.set("x", this.pageText.x());
            text.set("y", this.pageText.y());
            if (this.pageText.width() > 0) text.set("width", this.pageText.width());
            if (this.pageText.align() != HAlign.LEFT) text.set("align", this.pageText.align().name().toLowerCase(Locale.ROOT));
            text.set("text", this.pageText.text());
            if (!this.pageText.font().equals("default")) text.set("font", this.pageText.font());
        }
    }

    private static void write(@NotNull ConfigurationSection section, @NotNull Button button) {
        section.set("x", button.x());
        section.set("y", button.y());
        section.set("width", button.width());
        section.set("height", button.height());
        if (button.glyph() != null) section.set("glyph", button.glyph());
        if (button.glyphDisabled() != null) section.set("glyph-disabled", button.glyphDisabled());
        if (button.text() != null) section.set("text", button.text());
        if (!button.font().equals("default")) section.set("font", button.font());
    }
}
