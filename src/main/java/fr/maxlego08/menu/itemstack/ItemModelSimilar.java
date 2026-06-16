package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

public class ItemModelSimilar implements ItemStackSimilar {
    @Override
    public @NonNull String getName() {
        return "itemModel";
    }

    @Override
    public boolean isSimilar(@NonNull ItemStack itemStackA, @NonNull ItemStack itemStackB) {
        if (MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21.4"))) {
            ItemMeta itemMetaA = itemStackA.getItemMeta();
            ItemMeta itemMetaB = itemStackB.getItemMeta();
            return itemMetaA.hasItemModel() == itemMetaB.hasItemModel() && itemMetaA.getItemModel() == itemMetaB.getItemModel();
        }
        return false;
    }
}
