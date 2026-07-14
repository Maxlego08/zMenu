package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.annotations.AutoItemStackSimilar;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

@AutoItemStackSimilar
@SinceVersion("1.21.4")
public class ItemModelSimilar implements ItemStackSimilar {
    @Override
    public @NonNull String getName() {
        return "itemModel";
    }

    @Override
    public boolean isSimilar(@NonNull ItemStack itemStackA, @NonNull ItemStack itemStackB) {
        ItemMeta itemMetaA = itemStackA.getItemMeta();
        ItemMeta itemMetaB = itemStackB.getItemMeta();
        return itemMetaA.hasItemModel() == itemMetaB.hasItemModel() && itemMetaA.getItemModel() == itemMetaB.getItemModel();
    }
}
