package fr.maxlego08.menu.hooks;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemManager;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Optional;

public class ZItemsLoader implements MaterialLoader {

    private final Plugin plugin;

    public ZItemsLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "zitems";
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        RegisteredServiceProvider<ItemManager> itemManagerRegisteredServiceProvider = plugin.getServer().getServicesManager().getRegistration(ItemManager.class);
        if (itemManagerRegisteredServiceProvider == null) return null;
        ItemManager itemManager = itemManagerRegisteredServiceProvider.getProvider();
        Optional<Item> optional = itemManager.getItem(materialString);
        return optional.map(item -> item.build(player, 1)).orElse(null);
    }
}
