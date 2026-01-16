package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.itemstack.components.LoreComponent;
import fr.maxlego08.menu.itemstack.paper.components.PaperLoreComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoreItemComponentLoader extends ItemComponentLoader {
    private final MetaUpdater metaUpdater;

    public LoreItemComponentLoader(MenuPlugin menuPlugin){
        super("lore");
        this.metaUpdater = menuPlugin.getMetaUpdater();
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        Object o = configuration.get(path);
        if (o instanceof String str) {
            return getItemComponent(List.of(str));
        } else if (o instanceof List<?> list) {
            List<String> loreLines = new ArrayList<>();
            for (Object line : list) {
                if (line instanceof String s) {
                    loreLines.add(s);
                }
            }
            return getItemComponent(loreLines);
        }
        return null;
    }

    private ItemComponent getItemComponent(@NotNull List<@NotNull String> lore){
        if (metaUpdater instanceof PaperMetaUpdater paperMetaUpdater)
            return new PaperLoreComponent(lore, paperMetaUpdater);
        return new LoreComponent(lore);
    }


}
