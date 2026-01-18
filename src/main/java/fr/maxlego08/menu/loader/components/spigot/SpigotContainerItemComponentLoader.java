package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.ContainerComponent;
import fr.maxlego08.menu.loader.components.AbstractMenuItemStackListComponentLoaderBase;
import fr.maxlego08.menu.zcore.utils.itemstack.ContainerSlot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotContainerItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public SpigotContainerItemComponentLoader(MenuPlugin plugin) {
        super("container", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);

        List<Map<?, ?>> mapList = configuration.getMapList(path);
        List<ContainerSlot> contents = new ArrayList<>();
        int index = 0;
        for (var rawMap : mapList){
            @SuppressWarnings("unchecked")
            Map<String, Object> itemMap = (Map<String, Object>) rawMap;
            MenuItemStack menuItemStack = loadItemStack(itemMap, file);
            if (menuItemStack != null) {
                int slot = itemMap.containsKey("slot") ? (int) itemMap.get("slot") : index++;
                contents.add(new ContainerSlot(menuItemStack, slot));
            }

        }
        return contents.isEmpty() ? null : new ContainerComponent(contents);
    }
}
