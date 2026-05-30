package fr.maxlego08.menu.hooks.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;

public class ItemsAdderLoader extends MaterialLoader {

    private final Plugin plugin;

    public ItemsAdderLoader(Plugin plugin) {
        super("itemsadder");
        this.plugin = plugin;
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        CustomStack customStack = CustomStack.getInstance(materialString);
        if (customStack == null) {
            if (Configuration.enableDebug)
                Logger.info("ItemsAdderLoader: Item '" + materialString + "' not found, available items: " + CustomStack.getNamespacedIdsInRegistry());
            return null;
        }
        return customStack.getItemStack().clone();
    }
}
