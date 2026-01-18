package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.ChargedProjectilesComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SpigotChargedProjectilesItemComponentLoader extends MenuItemStackListComponentLoaderBase {

    public SpigotChargedProjectilesItemComponentLoader(MenuPlugin plugin){
        super("charged_projectiles", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<MenuItemStack> projectiles = loadItemStackList(mapList, file);
        return projectiles.isEmpty() ? null : new ChargedProjectilesComponent(projectiles);
    }
}
