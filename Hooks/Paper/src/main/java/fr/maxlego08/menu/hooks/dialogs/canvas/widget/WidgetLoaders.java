package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasLayoutable;
import fr.maxlego08.menu.api.dialog.canvas.CanvasManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ConditionalElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ElementSections;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.VisibleCondition;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateAction;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Reads the widgets: {@code toggle}, {@code stepper}, {@code slider}, {@code tabs} and {@code radio}.
 */
public final class WidgetLoaders {

    private WidgetLoaders() {
    }

    /**
     * @return One loader per widget type.
     */
    public static @NotNull List<CanvasElementLoader> loaders() {
        return List.of(new Toggle(), new Stepper(), new Slider(), new Choice(ChoiceWidget.Style.TABS), new Choice(ChoiceWidget.Style.RADIO),
                new Dropdown(), new Modal(), new ListLoader(), new Pages(), new Checklist(), new Confirm(), new Rating());
    }

    private static @NotNull Set<String> keys(@NotNull String... keys) {
        Set<String> all = new HashSet<>(WidgetSettings.KEYS);
        all.addAll(List.of(keys));
        return all;
    }

    private static double number(@NotNull ConfigurationSection section, @NotNull String key, double fallback, @NotNull String name, @NotNull String source) {
        if (!section.contains(key)) return fallback;
        if (!section.isInt(key) && !section.isDouble(key)) throw ElementSections.error(name, source, "needs a number for " + key);
        return section.getDouble(key);
    }

    private static int positive(@NotNull ConfigurationSection section, @NotNull String key, int fallback, @NotNull String name, @NotNull String source) {
        int value = section.contains(key) ? ElementSections.requireInt(section, key, name, source) : fallback;
        if (value < 1) throw ElementSections.error(name, source, "needs a " + key + " of at least 1");
        return value;
    }

    private static @NotNull WidgetPart glyphPart(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        WidgetPart part = WidgetPart.read(section, key, true, name, source);
        if (part.glyph(null) == null) throw ElementSections.error(name, source, "needs " + key + ": a glyph, or a section with glyph");
        return part;
    }

    private static @NotNull String texture(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String name, @NotNull String source) {
        String value = ElementSections.requireString(section, key, name, source);
        try {
            return ElementSections.texture(value);
        } catch (IllegalArgumentException exception) {
            throw ElementSections.error(name, source, "has the " + key + " " + value + ", which " + exception.getMessage());
        }
    }

    /**
     * Reads {@code type: toggle}.
     */
    public static final class Toggle implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("glyph-on", "glyph-off", "label", "label-on", "label-off", "font", "width");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(ToggleWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "false");
            return new ToggleWidget(settings, ElementSections.requireString(section, "glyph-on", name, source),
                    ElementSections.requireString(section, "glyph-off", name, source), WidgetPart.read(section, "label", false, name, source),
                    WidgetPart.read(section, "label-on", false, name, source), WidgetPart.read(section, "label-off", false, name, source),
                    section.getString("font", "default"), section.contains("width") ? positive(section, "width", 1, name, source) : null);
        }
    }

    /**
     * Reads {@code type: stepper}.
     */
    public static final class Stepper implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("min", "max", "step", "width", "minus", "plus", "minus-disabled", "plus-disabled", "text", "text-min",
                "text-max", "font");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(StepperWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            double min = number(section, "min", 0, name, source);
            double max = number(section, "max", 10, name, source);
            double step = number(section, "step", 1, name, source);
            if (max <= min || step <= 0) throw ElementSections.error(name, source, "needs max above min and a step above 0");
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, CanvasStateAction.format(min));
            return new StepperWidget(settings, min, max, step, positive(section, "width", 60, name, source), glyphPart(section, "minus", name, source),
                    glyphPart(section, "plus", name, source), section.getString("minus-disabled"), section.getString("plus-disabled"),
                    WidgetPart.read(section, "text", false, name, source), WidgetPart.read(section, "text-min", false, name, source),
                    WidgetPart.read(section, "text-max", false, name, source), section.getString("font", "default"));
        }
    }

    /**
     * Reads {@code type: slider}.
     */
    public static final class Slider implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("min", "max", "step", "width", "track", "track-height", "border", "knob", "clickable-steps", "text",
                "text-min", "text-max", "font");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(SliderWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            double min = number(section, "min", 0, name, source);
            double max = number(section, "max", 100, name, source);
            double step = number(section, "step", 1, name, source);
            if (max <= min || step <= 0) throw ElementSections.error(name, source, "needs max above min and a step above 0");
            int width = positive(section, "width", 100, name, source);
            int trackHeight = positive(section, "track-height", 6, name, source);
            int border = section.contains("border") ? ElementSections.requireInt(section, "border", name, source) : 2;
            if (border < 0 || border * 2 > Math.min(width, trackHeight)) throw ElementSections.error(name, source, "has a border larger than its track");
            int possible = (int) Math.round((max - min) / step) + 1;
            int steps = positive(section, "clickable-steps", Math.min(possible, 21), name, source);
            if (steps < 2 || steps > width) throw ElementSections.error(name, source, "needs clickable-steps from 2 to its width");
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, CanvasStateAction.format(min));
            return new SliderWidget(settings, min, max, step, width, texture(section, "track", name, source), trackHeight, border,
                    glyphPart(section, "knob", name, source), steps, WidgetPart.read(section, "text", false, name, source),
                    WidgetPart.read(section, "text-min", false, name, source), WidgetPart.read(section, "text-max", false, name, source),
                    section.getString("font", "default"));
        }
    }

    /**
     * Reads {@code type: tabs} and {@code type: radio}.
     */
    public static final class Choice implements CanvasElementLoader {

        private final ChoiceWidget.Style style;
        private final Set<String> keys;

        public Choice(@NotNull ChoiceWidget.Style style) {
            this.style = style;
            this.keys = style == ChoiceWidget.Style.TABS
                    ? keys("direction", "gap", "height", "options", "panel-selected", "panel-idle", "border", "font", "pages", "label", "label-selected")
                    : keys("direction", "gap", "height", "options", "glyph-selected", "glyph-idle", "font", "pages", "label", "label-selected");
        }

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(this.style.type());
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, this.keys, name, source);
            boolean tabs = this.style == ChoiceWidget.Style.TABS;
            String direction = section.getString("direction", tabs ? "row" : "column").toLowerCase(Locale.ROOT);
            if (!direction.equals("row") && !direction.equals("column")) throw ElementSections.error(name, source, "needs direction: row or column");
            int height = positive(section, "height", tabs ? 14 : 10, name, source);
            int gap = section.contains("gap") ? ElementSections.requireInt(section, "gap", name, source) : 2;

            List<ChoiceWidget.Option> options = tabs ? options(section, name, source, "label-selected", "panel-selected", "panel-idle", true)
                    : options(section, name, source, "label-selected", "glyph-selected", "glyph-idle", false);

            String selected;
            String idle;
            int border = 0;
            if (tabs) {
                selected = texture(section, "panel-selected", name, source);
                idle = texture(section, "panel-idle", name, source);
                border = section.contains("border") ? ElementSections.requireInt(section, "border", name, source) : 3;
                for (ChoiceWidget.Option option : options) {
                    if (border < 0 || border * 2 > Math.min(option.width(), height)) {
                        throw ElementSections.error(name, source, "has a border larger than its option " + option.id());
                    }
                }
            } else {
                selected = ElementSections.requireString(section, "glyph-selected", name, source);
                idle = ElementSections.requireString(section, "glyph-idle", name, source);
            }

            Map<String, List<CanvasElement>> pages = this.pages(plugin, name, section, source, options);
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, options.getFirst().id());
            return new ChoiceWidget(settings, this.style, options, direction.equals("column"), gap, height, selected, idle, border,
                    section.getString("font", "default"), WidgetPart.read(section, "label", false, name, source),
                    WidgetPart.read(section, "label-selected", false, name, source), pages);
        }

        private @NotNull Map<String, List<CanvasElement>> pages(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section,
                                                                @NotNull String source, @NotNull List<ChoiceWidget.Option> options) {
            Map<String, List<CanvasElement>> pages = new LinkedHashMap<>();
            ConfigurationSection pagesSection = section.getConfigurationSection("pages");
            if (pagesSection == null) return pages;
            for (String id : pagesSection.getKeys(false)) {
                if (options.stream().noneMatch(option -> option.id().equals(id))) {
                    throw ElementSections.error(name, source, "has a page " + id + " that is not one of its options");
                }
                ConfigurationSection page = pagesSection.getConfigurationSection(id);
                if (page == null) throw ElementSections.error(name, source, "needs the page " + id + " as a section of elements");
                pages.put(id, elements(plugin, name, page, source));
            }
            return pages;
        }
    }

    /**
     * Reads the options of tabs, radio and checklist: {@code id: label} or {@code id: { label, width, x, y,
     * <label selected key>, <selected key>, <idle key>, hover }}.
     */
    private static @NotNull List<ChoiceWidget.Option> options(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source,
                                                              @NotNull String labelSelectedKey, @NotNull String selectedKey, @NotNull String idleKey,
                                                              boolean textures) {
        List<ChoiceWidget.Option> options = new ArrayList<>();
        ConfigurationSection optionsSection = section.getConfigurationSection("options");
        if (optionsSection == null || optionsSection.getKeys(false).isEmpty()) throw ElementSections.error(name, source, "needs options");
        Set<String> keys = Set.of("label", "width", "x", "y", labelSelectedKey, selectedKey, idleKey, "hover");
        for (String id : optionsSection.getKeys(false)) {
            if (id.contains(",")) throw ElementSections.error(name, source, "has the option " + id + ", an option id cannot hold a comma");
            ConfigurationSection option = optionsSection.getConfigurationSection(id);
            if (option == null) {
                options.add(new ChoiceWidget.Option(id, optionsSection.getString(id, id), 60));
                continue;
            }
            ElementSections.rejectUnknownKeys(option, keys, name + ".options." + id, source);
            String selected = option.contains(selectedKey) ? (textures ? texture(option, selectedKey, name, source) : option.getString(selectedKey)) : null;
            String idle = option.contains(idleKey) ? (textures ? texture(option, idleKey, name, source) : option.getString(idleKey)) : null;
            options.add(new ChoiceWidget.Option(id, option.getString("label", id), positive(option, "width", 60, name, source),
                    option.contains("x") ? ElementSections.requireInt(option, "x", name, source) : null,
                    option.contains("y") ? ElementSections.requireInt(option, "y", name, source) : null, option.getString(labelSelectedKey), selected, idle,
                    option.isList("hover") ? option.getStringList("hover") : option.contains("hover") ? List.of(option.getString("hover")) : List.of()));
        }
        return options;
    }

    /**
     * Reads {@code type: checklist}.
     */
    public static final class Checklist implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("direction", "gap", "height", "max", "options", "glyph-on", "glyph-off", "font", "label", "label-checked");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(ChecklistWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            String direction = section.getString("direction", "column").toLowerCase(Locale.ROOT);
            if (!direction.equals("row") && !direction.equals("column")) throw ElementSections.error(name, source, "needs direction: row or column");
            int max = section.contains("max") ? positive(section, "max", 1, name, source) : 0;
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "");
            return new ChecklistWidget(settings, options(section, name, source, "label-checked", "glyph-on", "glyph-off", false), direction.equals("column"),
                    section.contains("gap") ? ElementSections.requireInt(section, "gap", name, source) : 2, positive(section, "height", 10, name, source), max,
                    ElementSections.requireString(section, "glyph-on", name, source), ElementSections.requireString(section, "glyph-off", name, source),
                    section.getString("font", "default"), WidgetPart.read(section, "label", false, name, source),
                    WidgetPart.read(section, "label-checked", false, name, source));
        }
    }

    /**
     * Reads {@code type: confirm}.
     */
    public static final class Confirm implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("width", "height", "glyph", "glyph-armed", "text", "text-armed", "font", "click-requirement", "actions",
                "refresh");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(ConfirmWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            int width = ElementSections.requireInt(section, "width", name, source);
            int height = ElementSections.requireInt(section, "height", name, source);
            if (width < 1) throw ElementSections.error(name, source, "needs a width of at least 1");
            if (height < 1 || height > WidgetElement.MAX_AREA_HEIGHT) throw ElementSections.error(name, source, "needs a height from 1 to " + WidgetElement.MAX_AREA_HEIGHT);
            if (!(section.getRoot() instanceof YamlConfiguration configuration) || section.getCurrentPath() == null) {
                throw ElementSections.error(name, source, "must be read from a YAML file");
            }
            String path = section.getCurrentPath();
            File file = new File(source);
            ButtonManager buttonManager = plugin.getButtonManager();
            List<Action> actions;
            List<Requirement> clickRequirements;
            try {
                actions = buttonManager.loadActions(configuration, path + ".actions", file);
                clickRequirements = buttonManager.loadRequirements(configuration, path + ".click-requirement", file);
            } catch (InventoryException exception) {
                throw ElementSections.error(name, source, "has invalid actions or requirements: " + exception.getMessage());
            }
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "false");
            return new ConfirmWidget(settings, width, height, section.getString("glyph"), section.getString("glyph-armed"),
                    WidgetPart.read(section, "text", false, name, source), WidgetPart.read(section, "text-armed", false, name, source),
                    section.getString("font", "default"), actions, clickRequirements, section.getBoolean("refresh", true));
        }
    }

    /**
     * Reads {@code type: rating}.
     */
    public static final class Rating implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("max", "gap", "glyph-on", "glyph-off", "clear", "star-hover", "stars");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(RatingWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "0");
            return new RatingWidget(settings, positive(section, "max", 5, name, source),
                    section.contains("gap") ? ElementSections.requireInt(section, "gap", name, source) : 1,
                    ElementSections.requireString(section, "glyph-on", name, source), ElementSections.requireString(section, "glyph-off", name, source),
                    section.getBoolean("clear", true),
                    section.isList("star-hover") ? section.getStringList("star-hover") : section.contains("star-hover") ? List.of(section.getString("star-hover")) : List.of(),
                    stars(section, name, source));
        }

        private static @NotNull List<int[]> stars(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source) {
            if (!section.contains("stars")) return List.of();
            if (!section.isList("stars")) throw ElementSections.error(name, source, "needs stars as a list of { x, y }");
            List<int[]> stars = new ArrayList<>();
            for (Map<?, ?> star : section.getMapList("stars")) {
                if (!(star.get("x") instanceof Number x) || !(star.get("y") instanceof Number y)) {
                    throw ElementSections.error(name, source, "needs each star as { x: <number>, y: <number> }");
                }
                stars.add(new int[]{x.intValue(), y.intValue()});
            }
            if (stars.isEmpty()) throw ElementSections.error(name, source, "needs at least one star in stars");
            return stars;
        }
    }

    private static @NotNull List<CanvasElement> elements(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section,
                                                         @NotNull String source) {
        CanvasManager canvasManager = plugin.getCanvasManager();
        if (canvasManager == null) throw ElementSections.error(name, source, "needs dialog canvases to be enabled");
        List<CanvasElement> elements = new ArrayList<>();
        for (String elementName : section.getKeys(false)) {
            ConfigurationSection element = section.getConfigurationSection(elementName);
            if (element == null) throw ElementSections.error(elementName, source, "must be a section");
            String type = element.getString("type");
            if (type == null) throw ElementSections.error(elementName, source, "needs a type");
            CanvasElementLoader loader = canvasManager.getElementLoader(type)
                    .orElseThrow(() -> ElementSections.error(elementName, source, "has the unknown type " + type));
            elements.add(ConditionalElement.load(plugin, loader, elementName, element, source));
        }
        return elements;
    }

    /**
     * Reads {@code type: dropdown}.
     */
    public static final class Dropdown implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("width", "height", "options", "panel", "list-panel", "selected-panel", "border", "arrow",
                "option-height", "max-visible", "previous-text", "next-text", "font", "text", "list", "option-text");
        private static final Set<String> OPTION_KEYS = Set.of("label", "hover");
        private static final int MIN_OPTION_HEIGHT = 9;

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(DropdownWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            List<DropdownWidget.Option> options = new ArrayList<>();
            ConfigurationSection optionsSection = section.getConfigurationSection("options");
            if (optionsSection == null || optionsSection.getKeys(false).isEmpty()) throw ElementSections.error(name, source, "needs options");
            for (String id : optionsSection.getKeys(false)) {
                ConfigurationSection option = optionsSection.getConfigurationSection(id);
                if (option == null) {
                    options.add(new DropdownWidget.Option(id, optionsSection.getString(id, id)));
                    continue;
                }
                ElementSections.rejectUnknownKeys(option, OPTION_KEYS, name + ".options." + id, source);
                options.add(new DropdownWidget.Option(id, option.getString("label", id),
                        option.isList("hover") ? option.getStringList("hover") : option.contains("hover") ? List.of(option.getString("hover")) : List.of()));
            }

            int width = positive(section, "width", 100, name, source);
            int height = positive(section, "height", 14, name, source);
            int border = section.contains("border") ? ElementSections.requireInt(section, "border", name, source) : 3;
            if (border < 0 || border * 2 > Math.min(width, height)) throw ElementSections.error(name, source, "has a border larger than its box");
            int optionHeight = positive(section, "option-height", 12, name, source);
            if (optionHeight < MIN_OPTION_HEIGHT) {
                throw ElementSections.error(name, source, "needs an option-height of " + MIN_OPTION_HEIGHT + " or more: older clients find clicks by 9 pixel rows");
            }
            int maxVisible = positive(section, "max-visible", 6, name, source);
            if (maxVisible < 2) throw ElementSections.error(name, source, "needs max-visible of 2 or more");

            String panel = texture(section, "panel", name, source);
            String listPanel = section.contains("list-panel") ? texture(section, "list-panel", name, source) : panel;
            String selectedPanel = section.contains("selected-panel") ? texture(section, "selected-panel", name, source) : null;
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, options.getFirst().id());
            return new DropdownWidget(settings, options, width, height, panel, listPanel, selectedPanel, border, WidgetPart.read(section, "arrow", true, name, source),
                    optionHeight, maxVisible, section.getString("previous-text", "<gray>« %value%"), section.getString("next-text", "<gray>%value% »"),
                    section.getString("font", "default"), WidgetPart.read(section, "text", false, name, source),
                    WidgetPart.read(section, "list", false, name, source), WidgetPart.read(section, "option-text", false, name, source));
        }
    }

    /**
     * Reads {@code type: list}.
     */
    public static final class ListLoader implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("entries", "separator", "per-page", "direction", "columns", "filter", "filter-on", "slot-width",
                "slot-height", "gap", "template",
                "empty-text", "font", "previous", "next", "page-text");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(ListWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            List<String> entries = section.isList("entries") ? section.getStringList("entries") : List.of();
            String entriesSource = section.isList("entries") ? null : section.getString("entries");
            if (entriesSource == null && !section.isList("entries")) throw ElementSections.error(name, source, "needs entries: a list or a placeholder");
            String direction = section.getString("direction", "column").toLowerCase(Locale.ROOT);
            if (!direction.equals("row") && !direction.equals("column")) throw ElementSections.error(name, source, "needs direction: row or column");
            int perPage = positive(section, "per-page", 4, name, source);
            int columns = section.contains("columns") ? positive(section, "columns", 1, name, source) : direction.equals("row") ? perPage : 1;

            ConfigurationSection templateSection = section.getConfigurationSection("template");
            if (templateSection == null) throw ElementSections.error(name, source, "needs a template element");
            CanvasLayoutable template = template(plugin, name, templateSection, source);

            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "1");
            return new ListWidget(settings, PageNavigation.read(section, name, source), entries, entriesSource, section.getString("separator", ","),
                    perPage, columns, positive(section, "slot-width", 100, name, source),
                    positive(section, "slot-height", 12, name, source), section.contains("gap") ? ElementSections.requireInt(section, "gap", name, source) : 0,
                    template, section.getString("empty-text"), section.getString("font", "default"), section.getString("filter"),
                    section.getString("filter-on", "%entry%"));
        }

        private static @NotNull CanvasLayoutable template(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section,
                                                          @NotNull String source) {
            CanvasManager canvasManager = plugin.getCanvasManager();
            if (canvasManager == null) throw ElementSections.error(name, source, "needs dialog canvases to be enabled");
            String type = section.getString("type");
            if (type == null) throw ElementSections.error(name, source, "needs a template type");
            CanvasElementLoader loader = canvasManager.getElementLoader(type)
                    .orElseThrow(() -> ElementSections.error(name, source, "has the unknown template type " + type));
            if (!section.contains("x")) section.set("x", 0);
            if (!section.contains("y")) section.set("y", 0);
            CanvasElement element = ConditionalElement.load(plugin, loader, name + ".template", section, source);
            if (!(ConditionalElement.unwrap(element) instanceof CanvasLayoutable) || !(element instanceof CanvasLayoutable layoutable)) {
                throw ElementSections.error(name, source, "has a template of type " + type + ", which cannot be placed in a slot");
            }
            return layoutable;
        }
    }

    /**
     * Reads {@code type: pages}.
     */
    public static final class Pages implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("pages", "previous", "next", "page-text", "next-requirement");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(PagesWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            ConfigurationSection pagesSection = section.getConfigurationSection("pages");
            if (pagesSection == null || pagesSection.getKeys(false).isEmpty()) throw ElementSections.error(name, source, "needs pages");
            Map<String, List<CanvasElement>> pages = new LinkedHashMap<>();
            for (String page : pagesSection.getKeys(false)) {
                ConfigurationSection pageSection = pagesSection.getConfigurationSection(page);
                if (pageSection == null) throw ElementSections.error(name, source, "needs the page " + page + " as a section of elements");
                pages.put(page, elements(plugin, name, pageSection, source));
            }
            Map<String, VisibleCondition> nextRequirements = new LinkedHashMap<>();
            ConfigurationSection requirements = section.getConfigurationSection("next-requirement");
            if (section.contains("next-requirement") && requirements == null) {
                throw ElementSections.error(name, source, "needs next-requirement as a section of page names");
            }
            if (requirements != null) {
                for (String page : requirements.getKeys(false)) {
                    if (!pages.containsKey(page)) throw ElementSections.error(name, source, "has a next-requirement for the unknown page " + page);
                    nextRequirements.put(page, VisibleCondition.parse(String.valueOf(requirements.get(page))));
                }
            }
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "1");
            return new PagesWidget(settings, PageNavigation.read(section, name, source), pages, nextRequirements);
        }
    }

    /**
     * Reads {@code type: modal}.
     */
    public static final class Modal implements CanvasElementLoader {

        private static final Set<String> KEYS = keys("width", "height", "panel", "border", "dim", "close-on-outside", "elements");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key(ModalWidget.TYPE);
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            int width = positive(section, "width", 160, name, source);
            int height = positive(section, "height", 80, name, source);
            int border = section.contains("border") ? ElementSections.requireInt(section, "border", name, source) : 4;
            if (border < 0 || border * 2 > Math.min(width, height)) throw ElementSections.error(name, source, "has a border larger than its window");
            ConfigurationSection elements = section.getConfigurationSection("elements");
            WidgetSettings settings = WidgetSettings.read(plugin, name, section, source, "false");
            return new ModalWidget(settings, width, height, texture(section, "panel", name, source), border,
                    section.contains("dim") ? texture(section, "dim", name, source) : null, section.getBoolean("close-on-outside", true),
                    elements == null ? List.of() : elements(plugin, name, elements, source));
        }
    }
}
