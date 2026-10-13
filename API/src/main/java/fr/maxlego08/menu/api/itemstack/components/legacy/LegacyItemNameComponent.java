package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.ItemNameComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyItemNameComponent extends ItemNameComponent {

    public LegacyItemNameComponent(@NotNull ResolvableString itemName) {
        super(itemName);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setItemName, this.getItemName());

        itemStack.setItemMeta(itemMeta);
    }
}
