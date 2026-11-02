package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * Reads {@link ImageElement}s, written {@code type: image}.
 */
public final class ImageElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "glyph", "glyph-values", "glyph-default", "x", "y", "y-values", "height");

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("image");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        PackValue glyph = PackValue.read(section, "glyph", null, UnaryOperator.identity(), name, source);
        PositionValue x = PositionValue.readX(section, "x", name, source);
        PositionValue y = section.contains("y") ? PositionValue.readY(section, "y", name, source) : null;
        Integer height = section.contains("height") ? ElementSections.requireInt(section, "height", name, source) : null;
        if (height != null && (height < 1 || height > PanelElementLoader.MAX_HEIGHT)) {
            throw ElementSections.error(name, source, "needs a height from 1 to " + PanelElementLoader.MAX_HEIGHT);
        }
        return new ImageElement(name, glyph, x, y, height);
    }
}
