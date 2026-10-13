package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.WritableBookContentComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyWritableBookContentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.text.Filtered;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AutoComponentLoader
public class WritableBookContentItemComponentLoader extends ItemComponentLoader {

    public WritableBookContentItemComponentLoader(){
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
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new WritableBookContentComponent(title, pages)
                : new LegacyWritableBookContentComponent(title, pages);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        WritableBookContent content = itemStack.getData(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (content == null) return null;

        List<ResolvableString> pages = new ArrayList<>(content.pages().size());
        for (Filtered<String> page : content.pages()) {
            if (page.filtered() != null || Resolvable.isExpression(page.raw())) return null;
            pages.add(ResolvableString.of(page.raw()));
        }
        return new WritableBookContentComponent(null, pages);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (itemStack.getType() != Material.WRITABLE_BOOK) return null;
        if (!(itemStack.getItemMeta() instanceof WritableBookMeta writableBookMeta) || !writableBookMeta.hasPages()) return null;

        List<ResolvableString> pages = new ArrayList<>(writableBookMeta.getPageCount());
        for (String page : writableBookMeta.getPages()) {
            if (Resolvable.isExpression(page)) return null;
            pages.add(ResolvableString.of(page));
        }
        return new LegacyWritableBookContentComponent(null, pages);
    }
}
