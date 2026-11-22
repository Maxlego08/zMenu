package fr.maxlego08.menu.api.dialog.canvas;

import fr.maxlego08.menu.api.MenuPlugin;
import net.kyori.adventure.key.Keyed;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

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
     * @deprecated use {@link #load(MenuPlugin, String, Section, String)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @NotNull
    CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull ConfigurationSection section, @NotNull String source);

    /**
     * Reads an element of a canvas. zMenu calls this method, override it.
     * By default it reads a Bukkit copy with the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    @NotNull
    default CanvasElement load(@NotNull MenuPlugin plugin, @NotNull String name, @NotNull Section section, @NotNull String source) {
        return this.load(plugin, name, YamlSections.toBukkit(section), source);
    }
}
