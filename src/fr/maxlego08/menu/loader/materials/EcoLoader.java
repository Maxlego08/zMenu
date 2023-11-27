package fr.maxlego08.menu.loader.materials;

import com.willfp.eco.core.items.Items;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class EcoLoader implements MaterialLoader {
    @Override
    public String getKey() {
        return "eco";
    }

    @Override
    public ItemStack load(YamlConfiguration yamlConfiguration, String path, String materialString) {
        try {
            //eco item lookup system:
            //https://plugins.auxilor.io/all-plugins/the-item-lookup-system
            return Items.lookup(materialString).getItem();
        } catch (Exception ignored) {
            return null;
        }
    }
}