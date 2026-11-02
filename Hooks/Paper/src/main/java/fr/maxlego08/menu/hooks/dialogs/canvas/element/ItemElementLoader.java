package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.MenuItemStack;
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
import java.util.Locale;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Reads {@link ItemElement}s, written {@code type: item}.
 */
public final class ItemElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "x", "y", "y-values", "item", "glyph", "glyph-values", "glyph-default", "height", "show-amount",
            "actions", "click-requirement", "refresh", "click");

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("item");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        if (!section.isConfigurationSection("item")) throw ElementSections.error(name, source, "needs an item section, in the zMenu item format");
        if (!(section.getRoot() instanceof YamlConfiguration configuration) || section.getCurrentPath() == null) {
            throw ElementSections.error(name, source, "must be read from a YAML file");
        }
        String path = section.getCurrentPath();
        File file = new File(source);

        String material = section.getString("item.material", "");
        String defaultGlyph = material.isBlank() || material.contains(":") || material.contains("%") ? null : "minecraft:item/" + material.toLowerCase(Locale.ROOT);
        if (defaultGlyph == null && !section.contains("glyph")) {
            throw ElementSections.error(name, source, "needs a glyph: its material " + material + " has no vanilla item texture zMenu can guess"
                    + " (a placeholder material needs glyph with glyph-values)");
        }
        PackValue glyph = PackValue.read(section, "glyph", defaultGlyph, UnaryOperator.identity(), name, source);

        Integer height = section.contains("height") ? ElementSections.requireInt(section, "height", name, source) : null;
        if (height != null && (height < 1 || height > PanelElementLoader.MAX_HEIGHT)) {
            throw ElementSections.error(name, source, "needs a height from 1 to " + PanelElementLoader.MAX_HEIGHT);
        }

        MenuItemStack item = plugin.loadItemStack(configuration, path + ".item", file);
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

        return new ItemElement(name, PositionValue.readX(section, "x", name, source), PositionValue.readY(section, "y", name, source), item, glyph, height,
                section.getBoolean("show-amount", true), actions, clickRequirements, section.getBoolean("refresh", false), click);
    }
}
