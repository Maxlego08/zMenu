package fr.maxlego08.menu.hooks.itemsadder;

import dev.lone.itemsadder.api.CustomStack;
import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoMaterialLoader
@RequiresPlugin("ItemsAdder")
public class ItemsAdderLoader extends MaterialLoader {

    public ItemsAdderLoader() {
        super("itemsadder");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlDocument configuration, @NonNull String path, @NonNull String materialString) {
        CustomStack customStack = CustomStack.getInstance(materialString);
        if (customStack == null) {
            if (Configuration.enableDebug)
                Logger.info("ItemsAdderLoader: Item '" + materialString + "' not found, available items: " + CustomStack.getNamespacedIdsInRegistry());
            return null;
        }
        return customStack.getItemStack().clone();
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemStack load(Player player, YamlConfiguration configuration, String path, String materialString) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(player, configurationDocument, path, materialString);
    }
}
