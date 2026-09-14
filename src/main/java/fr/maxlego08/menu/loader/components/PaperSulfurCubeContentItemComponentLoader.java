package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.SulfurCubeContentComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("26.2")
public class PaperSulfurCubeContentItemComponentLoader extends AbstractMenuItemStackListComponentLoaderBase {

    public PaperSulfurCubeContentItemComponentLoader(MenuPlugin plugin) {
        super("sulfur-cube-content", plugin);
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> values = componentSection.getValues(true);
        MenuItemStack menuItemStack = this.loadItemStack(values, file);
        return menuItemStack == null ? null : new SulfurCubeContentComponent(menuItemStack);
    }
}
