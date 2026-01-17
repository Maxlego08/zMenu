package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record WrittenBookContentComponent(
    @Nullable String title,
    @Nullable String author,
    @Nullable BookMeta.Generation generation,
    @NotNull List<@NotNull String> pages
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BookMeta.class, bookMeta -> {
            bookMeta.setTitle(this.title);
            bookMeta.setAuthor(this.author);
            bookMeta.setGeneration(this.generation);
            bookMeta.setPages(this.pages);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply WritableBookContentComponent to item: " + itemStack.getType().name());
    }
}
