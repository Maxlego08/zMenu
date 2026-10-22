package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.WrittenBookContentComponent;
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

public class LegacyWrittenBookContentComponent extends WrittenBookContentComponent {

    public LegacyWrittenBookContentComponent(@Nullable ResolvableString title, @Nullable ResolvableString author, @Nullable ResolvableEnum<BookMeta.Generation> generation, @NotNull List<@NotNull ResolvableString> pages) {
        super(title, author, generation, pages);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BookMeta.class, bookMeta -> {
            Resolvable.applyResolvable(context, this.getTitle(), bookMeta::setTitle);
            Resolvable.applyResolvable(context, this.getAuthor(), bookMeta::setAuthor);
            Resolvable.applyResolvable(context, this.getGeneration(), bookMeta::setGeneration);
            Resolvable.applyResolvable(context, this.getPages(), bookMeta::setPages);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply WrittenBookContentComponent to item: " + itemStack.getType().name());
    }
}
