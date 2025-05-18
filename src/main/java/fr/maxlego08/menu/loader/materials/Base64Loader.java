package fr.maxlego08.menu.loader.materials;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Base64Loader extends MaterialLoader {
    
    public Base64Loader() {
        super("base64");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        return ItemStackUtils.deserializeItemStack(materialString);
    }
}
