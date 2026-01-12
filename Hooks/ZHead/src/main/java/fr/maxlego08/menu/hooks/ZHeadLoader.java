package fr.maxlego08.menu.hooks;

import fr.maxlego08.head.api.HeadManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class ZHeadLoader extends MaterialLoader {

    private final HeadManager headManager;

    public ZHeadLoader(MenuPlugin plugin) {
        super("zhd");
        this.headManager = plugin.getProvider(HeadManager.class);
    }

    @Override
    public ItemStack load(@NonNull Player player, @NonNull YamlConfiguration configuration, @NonNull String path, @NonNull String materialString) {
        return this.headManager.createItemStack(materialString);
    }
}
