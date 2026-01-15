package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MenuItemStackListComponentLoaderBase extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public MenuItemStackListComponentLoaderBase(@NotNull String componentName, MenuPlugin plugin) {
        super(componentName);
        this.plugin = plugin;
    }

    @NotNull
    protected List<@NotNull MenuItemStack> loadItemStackList(@NotNull List<Map<?,?>> rawList, @NotNull File file) {
        List<MenuItemStack> itemStacks = new ArrayList<>();
        MenuItemStackLoader menuItemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());
        for (var rawMap : rawList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> itemMap = (Map<String, Object>) rawMap;
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.createSection("item", itemMap);
            MenuItemStack menuItemStack = menuItemStackLoader.load(yamlConfiguration, "item.", file);
            if (menuItemStack != null) {
                itemStacks.add(menuItemStack);
            }
        }
        return itemStacks;
    }

    @Nullable
    protected MenuItemStack loadItemStack(@NotNull Map<String, Object> itemMap, @NotNull File file) {
        MenuItemStackLoader menuItemStackLoader = new MenuItemStackLoader(this.plugin.getInventoryManager());
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.createSection("item", itemMap);
        return menuItemStackLoader.load(yamlConfiguration, "item.", file);
    }

}
