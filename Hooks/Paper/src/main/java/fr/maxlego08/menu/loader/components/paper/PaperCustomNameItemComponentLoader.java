package fr.maxlego08.menu.loader.components.paper;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.itemstack.components.paper.PaperCustomNameComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PaperCustomNameItemComponentLoader extends ItemComponentLoader {
    private final PaperMetaUpdater metaUpdater;

    public PaperCustomNameItemComponentLoader(MenuPlugin plugin){
        super("custom_name");
        this.metaUpdater = (PaperMetaUpdater) plugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String customName = configuration.getString(path);
        if (customName != null) {
            return new PaperCustomNameComponent(this.metaUpdater.getComponent(customName));
        }
        return null;
    }
}
