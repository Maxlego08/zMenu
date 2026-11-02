package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Reads {@link TextElement}s, written {@code type: text}.
 */
public final class TextElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "text", "font", "font-values", "font-default", "x", "y", "y-values", "width", "align", "line-height",
            "max-lines", "height", "wrap", "vertical-align", "overflow", "escape-placeholders");
    private static final Set<String> BOX_KEYS = Set.of("wrap", "vertical-align", "overflow");
    private static final int MAX_LINES = 32;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("text");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        List<String> lines = section.isList("text") ? section.getStringList("text") : List.of(ElementSections.requireString(section, "text", name, source));
        if (lines.isEmpty()) throw ElementSections.error(name, source, "needs text");

        PositionValue x = PositionValue.readX(section, "x", name, source);
        PositionValue y = PositionValue.readY(section, "y", name, source);
        String where = "The canvas element " + name + " of " + source;
        CanvasValue<Integer> width = section.contains("width") ? CanvasValue.number(section, "width", 100, 1, 1014, where) : null;
        if (section.contains("align") && width == null) throw ElementSections.error(name, source, "needs a width to be aligned");
        Integer lineHeight = section.contains("line-height") ? ElementSections.requireInt(section, "line-height", name, source) : null;
        if (lineHeight != null && lineHeight < 1) throw ElementSections.error(name, source, "needs a line-height of at least 1");
        CanvasValue<HAlign> align = CanvasValue.enumeration(section, "align", HAlign.class, HAlign.LEFT, where);

        PackValue font = PackValue.read(section, "font", "default", UnaryOperator.identity(), name, source);
        return new TextElement(name, lines, font, x, y, width, align, lineHeight,
                this.box(section, name, source, width, lineHeight == null ? 9 : lineHeight), section.getBoolean("escape-placeholders", true));
    }

    private TextElement.TextBox box(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source, CanvasValue<Integer> width, int lineHeight) {
        boolean hasBox = section.contains("max-lines") || section.contains("height");
        if (!hasBox) {
            for (String key : BOX_KEYS) {
                if (section.contains(key)) throw ElementSections.error(name, source, "needs max-lines or height to use " + key);
            }
            return null;
        }
        if (width == null) throw ElementSections.error(name, source, "needs a width to wrap its text");
        if (section.contains("max-lines") && section.contains("height")) throw ElementSections.error(name, source, "needs max-lines or height, not both");

        int maxLines = section.contains("max-lines")
                ? ElementSections.requireInt(section, "max-lines", name, source)
                : ElementSections.requireInt(section, "height", name, source) / lineHeight;
        if (maxLines < 1 || maxLines > MAX_LINES) throw ElementSections.error(name, source, "holds " + maxLines + " lines, it can hold from 1 to " + MAX_LINES);

        TextElement.Wrap wrap = this.parse(TextElement.Wrap.class, section.getString("wrap", "word"), "wrap", name, source);
        TextElement.VerticalAlign vertical = this.parse(TextElement.VerticalAlign.class, section.getString("vertical-align", "top"), "vertical-align", name, source);
        String overflow = section.getString("overflow", "ellipsis").toLowerCase(Locale.ROOT);
        if (!overflow.equals("ellipsis") && !overflow.equals("hide")) throw ElementSections.error(name, source, "needs overflow ellipsis or hide");
        return new TextElement.TextBox(maxLines, wrap, vertical, overflow.equals("ellipsis"));
    }

    private <E extends Enum<E>> E parse(@NotNull Class<E> type, @NotNull String value, @NotNull String key, @NotNull String name, @NotNull String source) {
        try {
            return Enum.valueOf(type, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "has the unknown " + key + " " + value);
        }
    }
}
