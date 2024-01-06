package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ModelIdSimilar implements ItemStackSimilar {
    @Override
    public String getName() {
        return "modelId";
    }

    @Override
    public boolean isSimilar(ItemStack itemStackA, ItemStack itemStackB) {
        if (itemStackA == null || itemStackB == null) return false;
        ItemMeta metaA = itemStackA.getItemMeta();
        ItemMeta metaB = itemStackB.getItemMeta();
        if (metaA == null || metaB == null) return false;
        return metaA.hasCustomModelData() && metaB.hasCustomModelData() && metaA.getCustomModelData() == metaB.getCustomModelData();
    }
}
