package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class WritableBookContentComponent extends ItemComponent {
    private final @Nullable String title;
    private final @NotNull List<@NotNull String> pages;

    public WritableBookContentComponent(@Nullable String title, @NotNull List<@NotNull String> pages) {
        this.title = title;
        this.pages = pages;
    }

    public @Nullable String getTitle() {
        return this.title;
    }

    public @NotNull List<@NotNull String> getPages() {
        return this.pages;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BookMeta.class, bookMeta -> {
            bookMeta.setTitle(this.title);
            bookMeta.setPages(this.pages);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply WritableBookContentComponent to item: " + itemStack.getType().name());
    }
}
