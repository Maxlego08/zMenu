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
 * Reads {@link ProgressElement}s, written {@code type: progress}.
 */
public final class ProgressElementLoader implements CanvasElementLoader {

    private static final Set<String> KEYS = Set.of("type", "texture", "texture-values", "texture-default", "background", "background-values",
            "background-default", "value", "max", "steps", "x", "y", "y-values");
    private static final int MAX_STEPS = 256;

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("progress");
    }

    @Override
    public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
        ElementSections.rejectUnknownKeys(section, KEYS, name, source);
        PackValue texture = PackValue.read(section, "texture", null, ElementSections::texture, name, source);
        PackValue background = section.contains("background") ? PackValue.read(section, "background", null, UnaryOperator.identity(), name, source) : null;
        String value = ElementSections.requireString(section, "value", name, source);
        String max = section.isString("max") ? section.getString("max") : String.valueOf(section.getDouble("max", 100));
        int steps = section.contains("steps") ? ElementSections.requireInt(section, "steps", name, source) : 0;
        if (steps < 0 || steps > MAX_STEPS) throw ElementSections.error(name, source, "needs steps from 1 to " + MAX_STEPS);
        return new ProgressElement(name, texture, background, value, max, steps,
                PositionValue.readX(section, "x", name, source), PositionValue.readY(section, "y", name, source));
    }
}
