package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface DialogActionLoader {

    @NotNull
    String getType();

    @NotNull
    ZDialogAction load(@NotNull MenuPlugin plugin, @NotNull YamlConfiguration configuration, @NotNull String path, @NotNull File file);
}
