package fr.maxlego08.menu.loader.materials;

import dev.lone.itemsadder.api.CustomStack;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ItemsAdderLoader implements MaterialLoader {

    private final Plugin plugin;

    public ItemsAdderLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "itemsadder";
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        CustomStack customStack = CustomStack.getInstance(materialString);
        if (customStack == null) {
            plugin.getLogger().severe("Impossible to find the item " + materialString);
            return null;
        }
        return customStack.getItemStack().clone();
    }
}
