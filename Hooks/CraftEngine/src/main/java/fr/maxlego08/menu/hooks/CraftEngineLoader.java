package fr.maxlego08.menu.hooks;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class CraftEngineLoader extends MaterialLoader {

    public CraftEngineLoader() {
        super("craftengine");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        Key key = Key.of(materialString);
        CustomItem<ItemStack> custom = CraftEngineItems.byId(key);
        if (custom == null) return null;
        return custom.buildItemStack();
    }
}
