package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OraxenLoader implements MaterialLoader {

    @Override
    public String getKey() {
        return "oraxen";
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        return OraxenItems.getItemById(materialString).build();
    }
}
