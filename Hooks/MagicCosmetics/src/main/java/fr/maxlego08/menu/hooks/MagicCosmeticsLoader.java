package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.francobm.magicosmetics.api.MagicAPI;
import org.jspecify.annotations.NonNull;

public class MagicCosmeticsLoader extends MaterialLoader {

    public MagicCosmeticsLoader() {
        super("magic_cosmetics");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        ItemStack itemStack = MagicAPI.getEquipped(player.getName(), materialString);
        if (itemStack == null){
            return new ItemStack(Material.AIR);
        }
        return itemStack;
    }
}
