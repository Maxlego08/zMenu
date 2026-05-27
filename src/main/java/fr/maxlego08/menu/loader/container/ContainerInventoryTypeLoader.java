package fr.maxlego08.menu.loader.container;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public interface ContainerInventoryTypeLoader<T extends ContainerInventory> {

    @NotNull
    T load(@NotNull MenuPlugin menuPlugin, @NotNull Plugin plugin, @NotNull String inventoryName, @NotNull String fileName, int size, @NotNull List<Button> buttons, @NotNull YamlConfiguration configuration, @NotNull String path, File file);
}
