package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WrittenBookContentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotWrittenBookContentItemComponentLoader extends ItemComponentLoader {

    public SpigotWrittenBookContentItemComponentLoader(){
        super("written-book-content");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableString title = ResolvableString.autoOrNull(componentSection.getString("title"));
        ResolvableString author = ResolvableString.autoOrNull(componentSection.getString("author"));
//         BookMeta.Generation generation = null;
//         String generationString = componentSection.getString("generation");
//         if (generationString != null) {
//             try {
//                 generation = BookMeta.Generation.valueOf(generationString.toUpperCase(Locale.ROOT));
//             } catch (IllegalArgumentException ignored) {
//             }
//         }
        ResolvableEnum<BookMeta.Generation> generation = ResolvableEnum.autoOrNull(BookMeta.Generation.class, componentSection.getString("generation"));
        List<Map<?, ?>> rawPagesList = componentSection.getMapList("pages");
        List<ResolvableString> pages = new ArrayList<>();
        for (Map<?, ?> rawPageMap : rawPagesList){
            @SuppressWarnings("unchecked")
            Map<String, Object> rawPage = (Map<String, Object>) rawPageMap;
            Object rawPageContent = rawPage.get("raw");
            if (rawPageContent instanceof String pageContentStr){
                pages.add(ResolvableString.autoOrNull(pageContentStr));
            }
        }
        return new WrittenBookContentComponent(title, author, generation, pages);
    }
}
