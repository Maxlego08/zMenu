package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class WrittenBookContentComponent extends ItemComponent {
    private final @Nullable ResolvableString title;
    private final @Nullable ResolvableString author;
    private final @Nullable ResolvableEnum<BookMeta.Generation> generation;
    private final @NotNull List<@NotNull ResolvableString> pages;

    public WrittenBookContentComponent(@Nullable ResolvableString title, @Nullable ResolvableString author, @Nullable ResolvableEnum<BookMeta.Generation> generation, @NotNull List<@NotNull ResolvableString> pages) {
        this.title = title;
        this.author = author;
        this.generation = generation;
        this.pages = pages;
    }

    public @Nullable ResolvableString getTitle() {
        return this.title;
    }

    public @Nullable ResolvableString getAuthor() {
        return this.author;
    }

    public @Nullable ResolvableEnum<BookMeta.Generation> getGeneration() {
        return this.generation;
    }

    public @NotNull List<@NotNull ResolvableString> getPages() {
        return this.pages;
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.title != null) map.put("title", this.title.serialize());
        if (this.author != null) map.put("author", this.author.serialize());
        if (this.generation != null) map.put("generation", this.generation.serialize());
        if (!this.pages.isEmpty()) {
            List<Map<String, Object>> rawPages = new ArrayList<>();
            for (ResolvableString page : this.pages) {
                Map<String, Object> rawPage = new LinkedHashMap<>();
                rawPage.put("raw", page.serialize());
                rawPages.add(rawPage);
            }
            map.put("pages", rawPages);
        }
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        String resolvedTitle = Resolvable.resolve(context, this.title);
        String resolvedAuthor = Resolvable.resolve(context, this.author);
        WrittenBookContent.Builder builder = WrittenBookContent.writtenBookContent(resolvedTitle != null ? resolvedTitle : "", resolvedAuthor != null ? resolvedAuthor : "");

        Resolvable.applyResolvable(context, this.generation, generation -> builder.generation(generation.ordinal()));
        for (ResolvableString page : this.pages) {
            Resolvable.applyResolvable(context, page, resolvedPage -> builder.addPage(LegacyComponentSerializer.legacySection().deserialize(resolvedPage)));
        }
        builder.resolved(true);

        itemStack.setData(DataComponentTypes.WRITTEN_BOOK_CONTENT, builder.build());
    }
}
