package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.paper.PaperMaxStackSizeComponent;
import fr.maxlego08.menu.itemstack.components.spigot.SpigotMaxStackSizeComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class MaxStackSizeItemComponentLoader extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public MaxStackSizeItemComponentLoader(MenuPlugin plugin){
        super("max-stack-size");
        this.plugin = plugin;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        int maxStackSize = configuration.getInt(path);
        if (maxStackSize > 0) {
            return this.plugin.isPaperOrFolia() ? new PaperMaxStackSizeComponent(maxStackSize) : new SpigotMaxStackSizeComponent(maxStackSize);
        }
        return null;
    }
}
