package fr.maxlego08.menu.hooks.mmoitems;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoMaterialLoader
@RequiresPlugin("MMOItems")
public class MMOItemsLoader extends MaterialLoader {

    public MMOItemsLoader() {
        super("mmoitems");
    }

    @Override
    public @Nullable ItemStack load(@NotNull Player player, @Nullable YamlConfiguration configuration, @NotNull String path, @NotNull String materialString) {
        String[] split = materialString.split(":",2);
        if (split.length != 2) return null;
        try {
            final Type type = MMOItems.plugin.getTypes().get(split[0]);
            if (type == null) {
                if (Configuration.enableDebug)
                    Logger.info(String.format("Invalid MMOItems type '%s' for material '%s', available types: %s", split[0], materialString, MMOItems.plugin.getTypes().getAllTypeNames()));
                return null;
            }
            ItemStack item = MMOItems.plugin.getItem(type, split[1]);
            if (item == null) {
                if (Configuration.enableDebug)
                    Logger.info(String.format("Invalid MMOItems item '%s' for type '%s', available items: %s", split[1], split[0], MMOItems.plugin.getTemplates().getTemplateNames(type)));
                return null;
            }
            return item;
        } catch (Exception e) {
            if (Configuration.enableDebug)
                Logger.info(String.format("Error loading MMOItems material '%s': %s", materialString, e.getMessage()));
            return null;
        }
    }
}
