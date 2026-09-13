package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.annotations.AutoItemStackSimilar;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

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

        if (itemMetaA == null || itemMetaB == null) return itemMetaA == itemMetaB;
        if (itemMetaA.hasItemModel() != itemMetaB.hasItemModel()) return false;

        return Objects.equals(itemMetaA.getItemModel(), itemMetaB.getItemModel());
    }
}
