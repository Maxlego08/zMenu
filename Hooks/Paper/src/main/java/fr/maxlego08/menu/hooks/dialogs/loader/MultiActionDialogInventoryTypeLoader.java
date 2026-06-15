package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.dialogs.record.ActionButtonRecord;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZMultiActionDialogInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MultiActionDialogInventoryTypeLoader implements DialogInventoryTypeLoader<ZMultiActionDialogInventory> {
    @Override
    public ZMultiActionDialogInventory load(@NotNull MenuPlugin menuPlugin, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String name, @NotNull String externalTitle) {
        int numberOfColumns = configuration.getInt("number-of-columns", 3);
        ConfigurationSection multiSection = configuration.getConfigurationSection("multi-actions");
        if (multiSection == null) {
            return null;
        }
        Set<String> keys = multiSection.getKeys(false);
        if (keys.isEmpty()) {
            if (Configuration.enableDebug){
                Logger.info("A minimum of one action button is required for multi-action dialogs.", Logger.LogType.WARNING);
            }
            return null;
        }
        List<ActionButtonRecord> actionButtons = new ArrayList<>();
        for (String key : keys) {
            actionButtons.add(this.loadActionButtonRecord(menuPlugin, configuration, "multi-actions." + key, file));
        }
        ActionButtonRecord exitButton = null;
        if (configuration.isConfigurationSection("exit-button")){
            exitButton = this.loadActionButtonRecord(menuPlugin, configuration, "exit-button", file);
        }
        return new ZMultiActionDialogInventory(menuPlugin, name, file.getName(), externalTitle, actionButtons, numberOfColumns, exitButton);
    }
}
