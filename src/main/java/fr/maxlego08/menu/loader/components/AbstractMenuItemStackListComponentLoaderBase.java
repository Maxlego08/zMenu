package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.itemstack.MenuItemStackConversion;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.common.utils.itemstack.MenuItemStackFromItemStack;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractMenuItemStackListComponentLoaderBase extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public AbstractMenuItemStackListComponentLoaderBase(@NotNull String componentName, MenuPlugin plugin) {
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

    /**
     * @return The menu item of a nested item, or null when it cannot be written without {@code base64:}.
     */
    @Nullable
    protected MenuItemStack convertItemStack(@NotNull ItemStack itemStack) {
        MenuItemStackConversion conversion = MenuItemStackFromItemStack.convert(this.plugin.getInventoryManager(), itemStack);
        return conversion.isReadable() ? conversion.menuItemStack() : null;
    }

    /**
     * @return The menu items of nested items, or null when one of them cannot be written without {@code base64:}.
     */
    @Nullable
    protected List<@NotNull MenuItemStack> convertItemStackList(@NotNull List<ItemStack> itemStacks) {
        List<MenuItemStack> menuItemStacks = new ArrayList<>(itemStacks.size());
        for (ItemStack itemStack : itemStacks) {
            MenuItemStack menuItemStack = this.convertItemStack(itemStack);
            if (menuItemStack == null) return null;
            menuItemStacks.add(menuItemStack);
        }
        return menuItemStacks;
    }

    /**
     * Same as {@link #convertItemStack(ItemStack)}, reading the nested item through its {@link org.bukkit.inventory.meta.ItemMeta}.
     */
    @Nullable
    protected MenuItemStack convertItemStackFromItemMeta(@NotNull ItemStack itemStack) {
        MenuItemStackConversion conversion = MenuItemStackFromItemStack.convert(this.plugin.getInventoryManager(), itemStack, false);
        return conversion.isReadable() ? conversion.menuItemStack() : null;
    }

    /**
     * Same as {@link #convertItemStackList(List)}, reading the nested items through their {@link org.bukkit.inventory.meta.ItemMeta}.
     */
    @Nullable
    protected List<@NotNull MenuItemStack> convertItemStackListFromItemMeta(@NotNull List<ItemStack> itemStacks) {
        List<MenuItemStack> menuItemStacks = new ArrayList<>(itemStacks.size());
        for (ItemStack itemStack : itemStacks) {
            MenuItemStack menuItemStack = this.convertItemStackFromItemMeta(itemStack);
            if (menuItemStack == null) return null;
            menuItemStacks.add(menuItemStack);
        }
        return menuItemStacks;
    }

}
