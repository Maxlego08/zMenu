package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.LayoutEngine;
import fr.maxlego08.menu.hooks.dialogs.canvas.layout.SizeSpec;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Reads {@link LayoutElement}s, written {@code type: row} or {@code type: column}.
 */
public final class LayoutElementLoader implements CanvasElementLoader {

    static final String GROW_TEXT = "grow-text";
    private static final Set<String> KEYS = Set.of("type", "x", "y", "width", "height", "padding", "gap", "align", "justify", "children");

    private final LayoutEngine.Direction direction;

    public LayoutElementLoader(@NotNull LayoutEngine.Direction direction) {
        this.direction = direction;
    }

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key(this.direction.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        int x = section.contains("x") ? ElementSections.requireInt(section, "x", name, source) : 0;
        int y = section.contains("y") ? ElementSections.requireInt(section, "y", name, source) : 0;
        if (y < 0) throw ElementSections.error(name, source, "cannot have a negative y");
        SizeSpec width = this.size(section, "width", name, source);
        SizeSpec height = this.size(section, "height", name, source);
        int gap = section.contains("gap") ? ElementSections.requireInt(section, "gap", name, source) : 0;

        LayoutEngine.Align align;
        LayoutEngine.Justify justify;
        try {
            align = LayoutEngine.Align.parse(section.getString("align", "start"));
            justify = LayoutEngine.Justify.parse(section.getString("justify", "start"));
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "needs align start, center or end, and justify start, center, end or space-between");
        }

        CanvasManager canvasManager = plugin.getCanvasManager();
        if (canvasManager == null) throw ElementSections.error(name, source, "needs dialog canvases to be enabled");
        List<LayoutElement.Child> children = new ArrayList<>();
        ConfigurationSection childrenSection = section.getConfigurationSection("children");
        if (childrenSection != null) {
            for (String childName : childrenSection.getKeys(false)) {
                ConfigurationSection childSection = childrenSection.getConfigurationSection(childName);
                if (childSection == null) throw ElementSections.error(childName, source, "must be a section");
                LayoutElement.Child child = this.loadChild(plugin, canvasManager, childName, childSection, source);
                if (child.growText() && children.stream().anyMatch(LayoutElement.Child::growText)) {
                    throw ElementSections.error(childName, source, "is a second " + GROW_TEXT + " child, a column can only have one");
                }
                if (child.collapse() && children.stream().filter(LayoutElement.Child::collapse).count() >= LayoutElement.MAX_COLLAPSING) {
                    throw ElementSections.error(childName, source, "uses collapse, but a row or a column can only have " + LayoutElement.MAX_COLLAPSING
                            + " children that collapse, since the pack holds every combination");
                }
                children.add(child);
            }
        }
        return new LayoutElement(name, this.direction, x, y, width, height, this.padding(section, name, source), gap, align, justify, children, null);
    }

    private @NotNull LayoutElement.Child loadChild(@NotNull MenuPlugin plugin, @NotNull CanvasManager canvasManager, @NotNull String name,
                                                   @NotNull ConfigurationSection section, @NotNull String source) {
        String type = section.getString("type");
        if (type == null) throw ElementSections.error(name, source, "needs a type");
        CanvasElementLoader loader = canvasManager.getElementLoader(type).orElseThrow(() -> ElementSections.error(name, source, "has the unknown type " + type));

        boolean growText = GROW_TEXT.equalsIgnoreCase(section.getString("layout-height", ""));
        if (growText && this.direction != LayoutEngine.Direction.COLUMN) {
            throw ElementSections.error(name, source, "uses layout-height: " + GROW_TEXT + ", which only works in a column");
        }
        if (growText) section.set("layout-height", null);
        SizeSpec width = this.size(section, "layout-width", name, source);
        SizeSpec height = this.size(section, "layout-height", name, source);
        section.set("layout-width", null);
        section.set("layout-height", null);
        boolean collapse = section.getBoolean("collapse", false);
        section.set("collapse", null);
        boolean layout = loader instanceof LayoutElementLoader;
        if (!layout) {
            if (!section.contains("x")) section.set("x", 0);
            if (!section.contains("y")) section.set("y", 0);
        }

        CanvasElement element = ConditionalElement.load(plugin, loader, name, section, source);
        CanvasElement inner = ConditionalElement.unwrap(element);
        if (!(inner instanceof CanvasLayoutable) || !(element instanceof CanvasLayoutable layoutable)) {
            throw ElementSections.error(name, source, "is a " + type + ", which a row or a column cannot place");
        }
        if (collapse && element == inner) {
            throw ElementSections.error(name, source, "uses collapse, which needs visible or view-requirement to hide it");
        }
        if (growText) {
            if (!(inner instanceof TextElement text) || text.box() == null) {
                throw ElementSections.error(name, source, "uses layout-height: " + GROW_TEXT + ", which needs a text with max-lines or height");
            }
            if (text.box().vertical() != TextElement.VerticalAlign.TOP) {
                throw ElementSections.error(name, source, "uses layout-height: " + GROW_TEXT + ", which takes the height of its lines: vertical-align must be top");
            }
            return new LayoutElement.Child(layoutable, width, SizeSpec.FIT, true, collapse);
        }
        if (inner instanceof LayoutElement nested) return new LayoutElement.Child(layoutable, nested.widthSpec(), nested.heightSpec(), false, collapse);
        return new LayoutElement.Child(layoutable, width, height, false, collapse);
    }

    private @NotNull SizeSpec size(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        if (!section.contains(key)) return SizeSpec.FIT;
        SizeSpec spec = SizeSpec.parse(section.get(key));
        if (spec == null) throw ElementSections.error(name, source, "needs " + key + " as a number, fit, grow or a percentage such as 50%");
        return spec;
    }

    private @NotNull LayoutEngine.Insets padding(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source) {
        if (!section.contains("padding")) return LayoutEngine.Insets.NONE;
        if (section.isInt("padding")) {
            int padding = section.getInt("padding");
            return new LayoutEngine.Insets(padding, padding, padding, padding);
        }
        List<Integer> values = section.getIntegerList("padding");
        if (values.size() != 4 || values.stream().anyMatch(value -> value < 0)) {
            throw ElementSections.error(name, source, "needs padding as a number or as [top, right, bottom, left]");
        }
        return new LayoutEngine.Insets(values.get(0), values.get(1), values.get(2), values.get(3));
    }
}
