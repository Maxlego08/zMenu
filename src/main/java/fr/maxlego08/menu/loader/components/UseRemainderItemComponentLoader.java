package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.itemstack.components.UseRemainderComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class UseRemainderItemComponentLoader extends MenuItemStackListComponentLoaderBase {

    public UseRemainderItemComponentLoader(MenuPlugin plugin){
        super("use_remainder", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(true);
        MenuItemStack menuItemStack = loadItemStack(values, file);
        return menuItemStack == null ? null : new UseRemainderComponent(menuItemStack);
    }
}
