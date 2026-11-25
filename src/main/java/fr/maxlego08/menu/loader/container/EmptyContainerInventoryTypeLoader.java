package fr.maxlego08.menu.loader.container;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZInventory;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class EmptyContainerInventoryTypeLoader implements ContainerInventoryTypeLoader<ContainerInventorySetter> {

    public EmptyContainerInventoryTypeLoader() {
    }

    @Override
    public @NonNull ZInventory load(@NotNull MenuPlugin menuPlugin, @NotNull Plugin plugin, @NotNull String inventoryName, @NotNull String fileName, int size, @NotNull List<Button> buttons, @NotNull YamlDocument configuration, @NotNull String path, File file) {
        return new ZInventory(plugin, inventoryName, fileName, size, buttons);
    }
}
