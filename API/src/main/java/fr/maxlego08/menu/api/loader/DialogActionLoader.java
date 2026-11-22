package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import net.kyori.adventure.key.Keyed;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public interface DialogActionLoader extends Keyed {

    /**
     * @deprecated use {@link #load(MenuPlugin, YamlDocument, String, File)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @NotNull
    ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file);

    /**
     * Loads a dialog action. zMenu calls this method, override it.
     * By default it reads a Bukkit copy with the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    @NotNull
    default ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlDocument configuration, @NotNull String path, @NotNull File file) {
        return this.load(plugin, YamlSections.toBukkit(configuration), path, file);
    }
}
