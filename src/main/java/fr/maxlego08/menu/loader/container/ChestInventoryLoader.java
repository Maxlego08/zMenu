package fr.maxlego08.menu.loader.container;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.setter.ChestInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZChestInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;

public class ChestInventoryLoader implements ContainerInventoryTypeLoader<ChestInventorySetter> {
    @Override
    public @NonNull ChestInventorySetter load(@NotNull MenuPlugin menuPlugin, @NotNull Plugin plugin, @NotNull String inventoryName, @NotNull String fileName, int size, @NotNull List<Button> buttons, @NotNull YamlConfiguration configuration, @NotNull String path, File file) {
        return new ZChestInventory(plugin, inventoryName, fileName, size, buttons);
    }
}
