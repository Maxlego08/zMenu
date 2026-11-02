package fr.maxlego08.menu.api.dialog.canvas;

import fr.maxlego08.menu.api.MenuPlugin;
import net.kyori.adventure.key.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Reads one type of canvas element, selected by the {@code type} key of the element.
 * <p>
 * The value of {@link #key()} is the type: {@code zmenu:image} is written {@code type: image}.
 */
public interface CanvasElementLoader extends Keyed {

    /**
     * Reads an element. A loader rejects any key it does not know, so a typing mistake fails instead of
     * being ignored.
     *
     * @param plugin  The plugin.
     * @param name    The name of the element.
     * @param section The section of the element.
     * @param source  Where the element is written, for error messages.
     * @return The element.
     * @throws fr.maxlego08.menu.api.exceptions.DialogException If the section is not a valid element.
     */
    @NotNull
    CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source);
}
