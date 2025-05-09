package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NameSimilar implements ItemStackSimilar {
    @Override
    public String getName() {
        return "name";
    }

    @Override
    public boolean isSimilar(ItemStack itemStackA, ItemStack itemStackB) {
        if (itemStackA == null || itemStackB == null) return false;
        ItemMeta metaA = itemStackA.getItemMeta();
        ItemMeta metaB = itemStackB.getItemMeta();
        if (metaA == null || metaB == null) return false;
        return metaA.hasDisplayName() && metaB.hasDisplayName() && metaA.getDisplayName().equals(metaB.getDisplayName());
    }
}
