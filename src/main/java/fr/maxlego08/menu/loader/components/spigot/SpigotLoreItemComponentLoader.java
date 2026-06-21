package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LoreComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.itemstack.components.paper.PaperLoreComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotLoreItemComponentLoader extends ItemComponentLoader {
    private final MetaUpdater metaUpdater;

    public SpigotLoreItemComponentLoader(MenuPlugin menuPlugin){
        super("lore");
        this.metaUpdater = menuPlugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        Object o = configuration.get(path);
        if (o instanceof String str) {
            return this.getItemComponent(List.of(ResolvableString.auto(str)));
        } else if (o instanceof List<?> list) {
            List<ResolvableString> loreLines = new ArrayList<>();
            for (Object line : list) {
                if (line instanceof String s) {
                    loreLines.add(ResolvableString.auto(s));
                }
            }
            return this.getItemComponent(loreLines);
        }
        return null;
    }

    private ItemComponent getItemComponent(@NotNull List<@NotNull ResolvableString> lore){
        if (this.metaUpdater instanceof PaperMetaUpdater paperMetaUpdater)
            return new PaperLoreComponent(lore, paperMetaUpdater);
        return new LoreComponent(lore);
    }


}
