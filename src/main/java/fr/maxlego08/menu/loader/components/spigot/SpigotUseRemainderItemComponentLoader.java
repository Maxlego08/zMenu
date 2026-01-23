package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.UseRemainderComponent;
import fr.maxlego08.menu.loader.components.AbstractMenuItemStackListComponentLoaderBase;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

public class SpigotUseRemainderItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public SpigotUseRemainderItemComponentLoader(MenuPlugin plugin){
        super("use-remainder", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(true);
        MenuItemStack menuItemStack = loadItemStack(values, file);
        return menuItemStack == null ? null : new UseRemainderComponent(menuItemStack);
    }
}
