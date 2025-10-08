package fr.maxlego08.menu.hooks.mythicmobs;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MythicMobsItemsLoader extends MaterialLoader {
    private final ItemExecutor itemManager;

    public MythicMobsItemsLoader() {
        super("mythicmobs");
        this.itemManager = MythicBukkit.inst().getItemManager();
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        Optional<MythicItem> mythicItem = itemManager.getItem(materialString);
        return mythicItem.map(MythicItem::getCachedBaseItem).orElse(null);
    }
}
