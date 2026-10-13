package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.WritableBookContentComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyWritableBookContentComponent extends WritableBookContentComponent {

    public LegacyWritableBookContentComponent(@Nullable ResolvableString title, @NotNull List<ResolvableString> pages) {
        super(title, pages);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, WritableBookMeta.class, writableBookMeta -> {
            if (writableBookMeta instanceof BookMeta bookMeta) {
                Resolvable.applyResolvable(context, this.getTitle(), bookMeta::setTitle);
            }
            Resolvable.applyResolvable(context, this.getPages(), writableBookMeta::setPages);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply WritableBookContentComponent to item: " + itemStack.getType().name());
    }
}
