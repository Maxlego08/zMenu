package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Reads {@link PanelElement}s, written {@code type: panel}.
 */
public final class PanelElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "texture", "texture-values", "texture-default", "border", "x", "y", "y-values", "width", "height");
    static final int MAX_HEIGHT = 4096;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("panel");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        PackValue texture = PackValue.read(section, "texture", null, ElementSections::texture, name, source);
        int border = section.contains("border") ? ElementSections.requireInt(section, "border", name, source) : 0;
        int width = ElementSections.requireInt(section, "width", name, source);
        int height = ElementSections.requireInt(section, "height", name, source);
        if (border < 0) throw ElementSections.error(name, source, "cannot have a negative border");
        if (width < 1 || width > 1024) throw ElementSections.error(name, source, "needs a width from 1 to 1024");
        if (height < 1 || height > MAX_HEIGHT) throw ElementSections.error(name, source, "needs a height from 1 to " + MAX_HEIGHT);
        if (border * 2 > Math.min(width, height)) throw ElementSections.error(name, source, "is smaller than its two borders");
        return new PanelElement(name, texture, border, PositionValue.readX(section, "x", name, source), PositionValue.readY(section, "y", name, source), width, height);
    }
}
