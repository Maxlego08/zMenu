package fr.maxlego08.menu.api.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import net.kyori.adventure.key.Keyed;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface DialogActionLoader extends Keyed {

    @NotNull
    ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file);
}
