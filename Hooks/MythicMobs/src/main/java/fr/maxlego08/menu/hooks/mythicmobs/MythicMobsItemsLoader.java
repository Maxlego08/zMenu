package fr.maxlego08.menu.hooks.mythicmobs;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@AutoMaterialLoader
@RequiresPlugin("MythicMobs")
public class MythicMobsItemsLoader extends MaterialLoader {
    private final ItemExecutor itemManager;

    public MythicMobsItemsLoader() {
        super("mythicmobs");
        this.itemManager = MythicBukkit.inst().getItemManager();
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        Optional<MythicItem> mythicItem = this.itemManager.getItem(materialString);
        if (mythicItem.isEmpty()) {

            if (Configuration.enableDebug)
                Logger.info("MythicMobsItemsLoader: Mythic item '" + materialString + "' not found, available items: " + this.itemManager.getItemNames());
            return null;
        }
        return mythicItem.get().getCachedBaseItem();
    }
}
