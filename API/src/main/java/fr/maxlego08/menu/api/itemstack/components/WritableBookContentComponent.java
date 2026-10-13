package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WritableBookContent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class WritableBookContentComponent extends ItemComponent {
    private final @Nullable ResolvableString title;
    private final @NotNull List<ResolvableString> pages;

    public WritableBookContentComponent(@Nullable ResolvableString title, @NotNull List<ResolvableString> pages) {
        this.title = title;
        this.pages = pages;
    }

    public @Nullable ResolvableString getTitle() {
        return this.title;
    }

    public @NotNull List<ResolvableString> getPages() {
        return this.pages;
    }

    @Override
    public @Nullable Object serialize() {
        List<Map<String, Object>> rawPages = new ArrayList<>();
        for (ResolvableString page : this.pages) {
            Map<String, Object> rawPage = new LinkedHashMap<>();
            rawPage.put("raw", page.serialize());
            rawPages.add(rawPage);
        }
        if (this.title != null) {
            if (rawPages.isEmpty()) rawPages.add(new LinkedHashMap<>());
            rawPages.getFirst().put("title", this.title.serialize());
        }

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("pages", rawPages);
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        WritableBookContent.Builder builder = WritableBookContent.writeableBookContent();

        Resolvable.applyResolvable(context, this.pages, builder::addPages);

        itemStack.setData(DataComponentTypes.WRITABLE_BOOK_CONTENT, builder.build());
    }
}
