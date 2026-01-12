package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class OraxenLoader extends MaterialLoader {

    public OraxenLoader() {
        super("oraxen");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        return OraxenItems.getItemById(materialString).build();
    }
}
