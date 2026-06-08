package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.annotations.AutoMaterialLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

@AutoMaterialLoader
@RequiresPlugin("CraftEngine")
public class CraftEngineLoader extends MaterialLoader {

    public CraftEngineLoader() {
        super("craftengine");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        BukkitItemDefinition custom = CraftEngineItems.byId(materialString);
        if (custom == null) return null;
        return custom.buildBukkitItem();
    }
}
