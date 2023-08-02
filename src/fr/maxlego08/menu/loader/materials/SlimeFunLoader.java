package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class SlimeFunLoader implements MaterialLoader {
    @Override
    public String getKey() {
        return "slimefun";
    }

    @Override
    public ItemStack load(YamlConfiguration configuration, String path, String materialString) {
        return SlimefunItem.getById(materialString).getItem();
    }
}
