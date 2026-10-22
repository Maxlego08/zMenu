package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WrittenBookContentComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyWrittenBookContentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import io.papermc.paper.text.Filtered;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class WrittenBookContentItemComponentLoader extends ItemComponentLoader {

    public WrittenBookContentItemComponentLoader(){
        super("written-book-content");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        ResolvableString title = ResolvableString.autoOrNull(componentSection.getString("title"));
        ResolvableString author = ResolvableString.autoOrNull(componentSection.getString("author"));
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
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new WrittenBookContentComponent(title, author, generation, pages)
                : new LegacyWrittenBookContentComponent(title, author, generation, pages);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        WrittenBookContent content = itemStack.getData(DataComponentTypes.WRITTEN_BOOK_CONTENT);
        if (content == null || !content.resolved() || content.title().filtered() != null) return null;

        String title = content.title().raw();
        String author = content.author();
        if (Resolvable.isExpression(title) || Resolvable.isExpression(author)) return null;

        BookMeta.Generation[] generations = BookMeta.Generation.values();
        if (content.generation() < 0 || content.generation() >= generations.length) return null;

        List<ResolvableString> pages = new ArrayList<>(content.pages().size());
        for (Filtered<Component> page : content.pages()) {
            if (page.filtered() != null) return null;
            String text = toLegacyText(page.raw());
            if (text == null || Resolvable.isExpression(text)) return null;
            pages.add(ResolvableString.of(text));
        }

        return new WrittenBookContentComponent(ResolvableString.of(title), ResolvableString.of(author), ResolvableEnum.of(BookMeta.Generation.class, generations[content.generation()]), pages);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.WRITTEN_BOOK) return null;
        if (!(itemStack.getItemMeta() instanceof BookMeta bookMeta)) return null;
        if (!bookMeta.hasTitle() && !bookMeta.hasAuthor() && !bookMeta.hasGeneration() && !bookMeta.hasPages()) return null;

        String title = bookMeta.hasTitle() ? bookMeta.getTitle() : null;
        String author = bookMeta.hasAuthor() ? bookMeta.getAuthor() : null;
        if ((title != null && Resolvable.isExpression(title)) || (author != null && Resolvable.isExpression(author))) return null;
        BookMeta.Generation generation = bookMeta.hasGeneration() ? bookMeta.getGeneration() : null;

        List<ResolvableString> pages = new ArrayList<>(bookMeta.getPageCount());
        for (int page = 1; page <= bookMeta.getPageCount(); page++) {
            String text = toLegacyText(bookMeta.page(page));
            if (text == null || Resolvable.isExpression(text)) return null;
            pages.add(ResolvableString.of(text));
        }

        return new LegacyWrittenBookContentComponent(
                title == null ? null : ResolvableString.of(title),
                author == null ? null : ResolvableString.of(author),
                generation == null ? null : ResolvableEnum.of(BookMeta.Generation.class, generation),
                pages
        );
    }
}
