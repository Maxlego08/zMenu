package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.head.api.HeadManager;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ZHeadLoader implements MaterialLoader {

    private final HeadManager headManager;

    public ZHeadLoader(MenuPlugin plugin) {
        this.headManager = plugin.getProvider(HeadManager.class);
    }

    @Override
    public String getKey() {
        return "zhd";
    }

    @Override
    public ItemStack load(YamlConfiguration configuration, String path, String materialString) {
        return this.headManager.createItemStack(materialString);
    }
}
