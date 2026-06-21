package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BookMeta.class, bookMeta -> {
            Resolvable.applyResolvable(context, this.title, bookMeta::setTitle);
            Resolvable.applyResolvable(context, this.author, bookMeta::setAuthor);
            Resolvable.applyResolvable(context, this.generation, bookMeta::setGeneration);
            Resolvable.applyResolvable(context, this.pages, bookMeta::setPages);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply WritableBookContentComponent to item: " + itemStack.getType().name());
    }
}
