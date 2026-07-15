package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WritableBookContentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotWritableBookContentItemComponentLoader extends ItemComponentLoader {

    public SpigotWritableBookContentItemComponentLoader(){
        super("writable-book-content");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        List<Map<?, ?>> rawPagesList = componentSection.getMapList("pages");
        ResolvableString title = null;
        List<ResolvableString> pages = new ArrayList<>();
        for (Map<?, ?> rawPageMap : rawPagesList){
            @SuppressWarnings("unchecked")
            Map<String, Object> rawPage = (Map<String, Object>) rawPageMap;
            Object pageTitle = rawPage.get("title");
            if (pageTitle instanceof String pageTitleStr){
                title = ResolvableString.auto(pageTitleStr);
            }
            Object rawPageContent = rawPage.get("raw");
            if (rawPageContent instanceof String pageContentStr){
                pages.add(ResolvableString.auto(pageContentStr));
            }
        }
        return new WritableBookContentComponent(title, pages);
    }
}
