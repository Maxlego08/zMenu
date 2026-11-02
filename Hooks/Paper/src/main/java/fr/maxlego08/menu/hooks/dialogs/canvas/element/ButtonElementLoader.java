package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Reads {@link ButtonElement}s, written {@code type: button}. Actions and requirements use the same format
 * as the buttons of an inventory, and {@code click} the format of the actions of dialog buttons.
 */
public final class ButtonElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "x", "y", "y-values", "width", "height", "glyph", "glyph-values", "glyph-default", "text", "font", "font-values", "font-default", "hover", "click-requirement", "actions", "refresh", "click", "escape-placeholders");
    private static final int MAX_HEIGHT = 256;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("button");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        PositionValue x = PositionValue.readX(section, "x", name, source);
        PositionValue y = PositionValue.readY(section, "y", name, source);
        int width = ElementSections.requireInt(section, "width", name, source);
        int height = ElementSections.requireInt(section, "height", name, source);
        if (width < 1) throw ElementSections.error(name, source, "needs a width of at least 1");
        if (height < 1 || height > MAX_HEIGHT) throw ElementSections.error(name, source, "needs a height from 1 to " + MAX_HEIGHT);

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

        ClickValue click = ClickValue.read(plugin, section, name, source);

        PackValue glyph = section.contains("glyph") ? PackValue.read(section, "glyph", null, UnaryOperator.identity(), name, source) : null;
        PackValue font = PackValue.read(section, "font", "default", UnaryOperator.identity(), name, source);
        return new ButtonElement(name, x, y, width, height, glyph, section.getString("text"), font,
                section.getStringList("hover"), actions, clickRequirements, section.getBoolean("refresh", false), click,
                section.getBoolean("escape-placeholders", true));
    }
}
