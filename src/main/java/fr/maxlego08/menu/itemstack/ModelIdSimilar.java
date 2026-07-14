package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.annotations.AutoItemStackSimilar;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

@AutoItemStackSimilar
public class ModelIdSimilar implements ItemStackSimilar {
    @Override
    public @NonNull String getName() {
        return "modelId";
    }

    @Override
    public boolean isSimilar(@NonNull ItemStack itemStackA, @NonNull ItemStack itemStackB) {
        ItemMeta metaA = itemStackA.getItemMeta();
        ItemMeta metaB = itemStackB.getItemMeta();
        if (metaA == null || metaB == null) return false;
        return metaA.hasCustomModelData() && metaB.hasCustomModelData() && metaA.getCustomModelData() == metaB.getCustomModelData();
    }
}
