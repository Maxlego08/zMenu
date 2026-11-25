package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("Slimefun")
public class SlimeFunLoader extends MaterialLoader {
    public SlimeFunLoader() {
        super("slimefun");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        return SlimefunItem.getById(materialString).getItem();
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
