package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ElementSections;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.VisibleCondition;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateActions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The keys every widget accepts:
 *
 * <pre>
 * key: music                    # the state key, %canvas_music%; the element name by default
 * default: "true"               # the value before the first change, may be a placeholder
 * keep: open                    # open (default) or session, see CanvasStateService.Keep
 * x: 10
 * y: 20
 * enabled: "%player_level% >= 5"  # optional condition: when false the widget takes no click
 * hover:                        # optional tooltip, %value% is the current value
 *   - "&lt;gray&gt;Now: %value%"
 * escape-placeholders: true
 * on-change:                    # zMenu actions run after each change, with %value% and %previous%
 *   - type: message
 *     messages: ["Music: %value%"]
 * </pre>
 *
 * @param name               The element name.
 * @param key                The state key.
 * @param defaultValue       The value before the first change, as written.
 * @param keep               How long a change is kept.
 * @param x                  The x of the widget.
 * @param y                  The y of the widget.
 * @param enabled            The condition to take clicks, or null.
 * @param hover              The tooltip lines.
 * @param escapePlaceholders True to show the tags placeholders give as text.
 * @param onChange           The actions run after a change.
 */
public record WidgetSettings(@NotNull String name, @NotNull String key, @NotNull String defaultValue, @NotNull CanvasStateService.Keep keep, int x, int y,
                             @Nullable VisibleCondition enabled, @NotNull List<String> hover, boolean escapePlaceholders, @NotNull List<Action> onChange) {

    /**
     * The keys {@link #read} reads.
     */
    public static final Set<String> KEYS = Set.of("type", "key", "default", "keep", "x", "y", "enabled", "hover", "escape-placeholders", "on-change");

    public WidgetSettings {
        hover = List.copyOf(hover);
        onChange = List.copyOf(onChange);
    }

    /**
     * @param plugin       The plugin, to load the actions.
     * @param name         The element name.
     * @param section      The section of the widget.
     * @param source       Where it is read from, for error messages.
     * @param defaultValue The default value when the section has none.
     * @return The settings.
     */
    public static @NotNull WidgetSettings read(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source,
                                               @NotNull String defaultValue) {
        String key = section.getString("key", name);
        if (!CanvasStateActions.isValidKey(key)) {
            throw ElementSections.error(name, source, "needs a key without spaces, dots or placeholders, and not dialog");
        }
        int x = section.contains("x") ? ElementSections.requireInt(section, "x", name, source) : 0;
        int y = section.contains("y") ? ElementSections.requireInt(section, "y", name, source) : 0;
        String keep = section.getString("keep", "open").toLowerCase(Locale.ROOT);
        if (!keep.equals("open") && !keep.equals("session")) throw ElementSections.error(name, source, "needs keep: open or session");

        List<Action> onChange = List.of();
        if (section.contains("on-change")) {
            if (!(section.getRoot() instanceof YamlConfiguration configuration) || section.getCurrentPath() == null) {
                throw ElementSections.error(name, source, "must be read from a YAML file to use on-change");
            }
            onChange = plugin.getButtonManager().loadActions(configuration, section.getCurrentPath() + ".on-change", new File(source));
        }
        return new WidgetSettings(name, key, String.valueOf(section.get("default", defaultValue)), CanvasStateService.Keep.parse(keep), x, y,
                section.contains("enabled") ? VisibleCondition.parse(String.valueOf(section.get("enabled"))) : null,
                section.isList("hover") ? section.getStringList("hover") : section.contains("hover") ? List.of(section.getString("hover")) : List.of(),
                section.getBoolean("escape-placeholders", true), onChange);
    }

    /**
     * @param section       The section to write to.
     * @param type          The widget type.
     * @param defaultValue  The default value of the widget type, left out when unchanged.
     */
    public void serialize(@NotNull ConfigurationSection section, @NotNull String type, @NotNull String defaultValue) {
        section.set("type", type);
        if (!this.key.equals(this.name)) section.set("key", this.key);
        if (!this.defaultValue.equals(defaultValue)) section.set("default", this.defaultValue);
        if (this.keep != CanvasStateService.Keep.OPEN) section.set("keep", "session");
        section.set("x", this.x);
        section.set("y", this.y);
        if (this.enabled != null) section.set("enabled", this.enabled.raw());
        if (!this.hover.isEmpty()) section.set("hover", this.hover);
        if (!this.escapePlaceholders) section.set("escape-placeholders", false);
        if (!this.onChange.isEmpty()) section.set("on-change", this.onChange.stream().map(Action::serialize).toList());
    }
}
