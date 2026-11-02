package fr.maxlego08.menu.hooks.dialogs.canvas.element;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElementLoader;
import fr.maxlego08.menu.api.dialog.canvas.CanvasValue;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Reads {@link HeadElement}s ({@code type: head}) and {@link SpriteElement}s ({@code type: sprite}).
 */
public final class ObjectElementLoaders {

    private ObjectElementLoaders() {
    }

    private static int line(@NotNull ConfigurationSection section, @NotNull String name, @NotNull String source) {
        int line = ElementSections.requireInt(section, "line", name, source);
        if (line < 0) throw ElementSections.error(name, source, "cannot have a negative line");
        return line;
    }

    /**
     * Reads {@code type: head}.
     */
    public static final class Head implements CanvasElementLoader {

        private static final Set<String> KEYS = Set.of("type", "player", "texture", "hat", "x", "line");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key("head");
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            String where = "The canvas element " + name + " of " + source;
            String player = section.getString("player");
            CanvasValue<String> texture = section.contains("texture") ? CanvasValue.text(section, "texture", "", where) : null;
            if (player != null && texture != null && !texture.isPlaceholder()) throw ElementSections.error(name, source, "needs player or texture, not both");
            return new HeadElement(name, player, texture, CanvasValue.bool(section, "hat", true, where), PositionValue.readX(section, "x", name, source),
                    line(section, name, source));
        }
    }

    /**
     * Reads {@code type: sprite}.
     */
    public static final class Sprite implements CanvasElementLoader {

        private static final Set<String> KEYS = Set.of("type", "atlas", "sprite", "x", "line");

        @Override
        public @NotNull Key key() {
            return ZMenuKeys.key("sprite");
        }

        @Override
        public @NotNull CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source) {
            ElementSections.rejectUnknownKeys(section, KEYS, name, source);
            String where = "The canvas element " + name + " of " + source;
            ElementSections.requireString(section, "sprite", name, source);
            return new SpriteElement(name, CanvasValue.key(section, "atlas", SpriteElement.DEFAULT_ATLAS, where),
                    CanvasValue.key(section, "sprite", SpriteElement.MISSING_SPRITE, where), PositionValue.readX(section, "x", name, source), line(section, name, source));
        }
    }
}
