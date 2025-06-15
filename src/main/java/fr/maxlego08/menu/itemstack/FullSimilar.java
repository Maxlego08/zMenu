package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;

public class FullSimilar implements ItemStackSimilar {
    @Override
    public String getName() {
        return "full";
    }

    @Override
    public boolean isSimilar(ItemStack itemStackA, ItemStack itemStackB) {
        if (itemStackA == null || itemStackB == null) return false;
        return itemStackA.isSimilar(itemStackB);
    }
}
