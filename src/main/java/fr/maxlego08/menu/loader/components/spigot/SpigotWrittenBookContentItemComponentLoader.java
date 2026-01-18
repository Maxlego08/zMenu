package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.WrittenBookContentComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpigotWrittenBookContentItemComponentLoader extends ItemComponentLoader {

    public SpigotWrittenBookContentItemComponentLoader(){
        super("written_book_content");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String title = componentSection.getString("title");
        String author = componentSection.getString("author");
        BookMeta.Generation generation = null;
        String generationString = componentSection.getString("generation");
        if (generationString != null) {
            try {
                generation = BookMeta.Generation.valueOf(generationString.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        List<Map<?, ?>> rawPagesList = componentSection.getMapList("pages");
        List<String> pages = new ArrayList<>();
        for (Map<?, ?> rawPageMap : rawPagesList){
            @SuppressWarnings("unchecked")
            Map<String, Object> rawPage = (Map<String, Object>) rawPageMap;
            Object rawPageContent = rawPage.get("raw");
            if (rawPageContent instanceof String pageContentStr){
                pages.add(pageContentStr);
            }
        }
        return new WrittenBookContentComponent(title, author, generation, pages);
    }
}
