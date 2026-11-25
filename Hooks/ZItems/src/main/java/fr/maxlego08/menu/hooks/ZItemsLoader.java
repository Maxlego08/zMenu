package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.registries.ItemsRegistry;
import fr.traqueur.items.api.registries.Registry;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("zItems")
public class ZItemsLoader extends MaterialLoader {

    public ZItemsLoader() {
        super("zitems");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        try {
            ItemsRegistry registry = Registry.get(ItemsRegistry.class);
            if (registry == null) return null;

            Item item = registry.getById(materialString);
            return item == null ? null : item.build(player, 1);
        } catch (LinkageError error) {
            // @RequiresPlugin only matches on the plugin name, and the older zItems by
            // Maxlego08 shares it while exposing a completely different API. Degrade to
            // "unknown material" instead of breaking the whole inventory.
            return null;
        }
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
