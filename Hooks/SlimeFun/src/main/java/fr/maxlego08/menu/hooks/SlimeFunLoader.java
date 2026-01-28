package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.loader.MaterialLoader;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class SlimeFunLoader extends MaterialLoader {
    public SlimeFunLoader() {
        super("slimefun");
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        return SlimefunItem.getById(materialString).getItem();
    }
}
