package fr.maxlego08.menu.loader.container;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.inventory.setter.AnvilInventorySetter;
import fr.maxlego08.menu.inventory.zinv.ZAnvilInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class AnvilInventoryTypeLoader implements ContainerInventoryTypeLoader<AnvilInventorySetter> {

    @Override
    public @NonNull AnvilInventorySetter load(@NotNull MenuPlugin menuPlugin, @NotNull Plugin plugin, @NotNull String inventoryName, @NotNull String fileName, int size, @NotNull List<Button> buttons, @NotNull YamlConfiguration configuration, @NotNull String path, File file) {
        List<Requirement> renameRequirements;
        try {
            renameRequirements = menuPlugin.getButtonManager().loadRequirements(configuration, "rename-requirements", file);
        } catch (InventoryException e) {
            Logger.info("An error occurred while loading the inventory " + inventoryName + " from file " + fileName + ": " + e.getMessage());
            renameRequirements = Collections.emptyList();
        }

        return new ZAnvilInventory(plugin, inventoryName, fileName, size, buttons, renameRequirements);
    }
}
