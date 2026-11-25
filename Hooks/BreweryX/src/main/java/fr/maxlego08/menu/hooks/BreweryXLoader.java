package fr.maxlego08.menu.hooks;

import com.dre.brewery.api.BreweryApi;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("BreweryX")
public class BreweryXLoader extends MaterialLoader {
    public BreweryXLoader(){
        super("breweryx");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        String[] parts = materialString.split(":",2);
        int quality;
        try {
            quality = Integer.parseInt(parts[1]);
        } catch (Exception e){
            quality = 1;
        }

        return BreweryApi.createBrewItem(parts[0],quality,player);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
