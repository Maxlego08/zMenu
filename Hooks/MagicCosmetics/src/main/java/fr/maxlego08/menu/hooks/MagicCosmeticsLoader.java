package fr.maxlego08.menu.hooks;

import com.francobm.magicosmetics.api.MagicAPI;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.Material;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("MagicCosmetics")
public class MagicCosmeticsLoader extends MaterialLoader {

    public MagicCosmeticsLoader() {
        super("magic_cosmetics");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        ItemStack itemStack = MagicAPI.getEquipped(player.getName(), materialString);
        if (itemStack == null){
            return new ItemStack(Material.AIR);
        }
        return itemStack;
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
