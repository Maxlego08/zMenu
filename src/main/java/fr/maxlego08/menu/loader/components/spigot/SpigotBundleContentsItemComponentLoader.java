package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BundleContentsComponent;
import fr.maxlego08.menu.loader.components.AbstractMenuItemStackListComponentLoaderBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

public class SpigotBundleContentsItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public SpigotBundleContentsItemComponentLoader(MenuPlugin plugin){
        super("bundle-contents", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);

        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<MenuItemStack> contents = loadItemStackList(mapList, file);
        return contents.isEmpty() ? null : new BundleContentsComponent(contents);
    }
}
