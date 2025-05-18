package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.francobm.magicosmetics.api.MagicAPI;

public class MagicCosmeticsLoader extends MaterialLoader {

    public MagicCosmeticsLoader() {
        super("magic_cosmetics");
    }

    @Override
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        ItemStack itemStack = MagicAPI.getEquipped(player.getName(), materialString);
        if (itemStack == null){
            return new ItemStack(Material.AIR);
        }
        return itemStack;
    }
}
