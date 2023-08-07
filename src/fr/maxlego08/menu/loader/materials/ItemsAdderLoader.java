package fr.maxlego08.menu.loader.materials;

import dev.lone.itemsadder.api.CustomStack;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderLoader implements MaterialLoader {
    @Override
    public String getKey() {
        return "itemsadder";
    }

    @Override
    public ItemStack load(YamlConfiguration configuration, String path, String materialString) {
        return CustomStack.getInstance(materialString).getItemStack();
    }
}
