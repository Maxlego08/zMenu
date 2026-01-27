package fr.maxlego08.menu.hooks;

import com.dre.brewery.api.BreweryApi;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class BreweryXLoader extends MaterialLoader {
    public BreweryXLoader(){
        super("breweryx");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        String[] parts = materialString.split(":",2);
        int quality;
        try {
            quality = Integer.parseInt(parts[1]);
        } catch (Exception e){
            quality = 1;
        }

        return BreweryApi.createBrewItem(parts[0],quality,player);
    }
}
