package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.PaperCustomNameComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
@PaperOnly
public class PaperCustomNameItemComponentLoader extends ItemComponentLoader {
    private final PaperMetaUpdater metaUpdater;

    public PaperCustomNameItemComponentLoader(MenuPlugin plugin){
        super("custom-name");
        this.metaUpdater = (PaperMetaUpdater) plugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String customName = configuration.getString(path);
        ResolvableComponent resolvableString = ResolvableComponent.autoOrNull(customName, this.metaUpdater);
        return resolvableString != null ? new PaperCustomNameComponent(resolvableString) : null;
    }
}
