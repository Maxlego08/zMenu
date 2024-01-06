package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;

public class MaterialSimilar implements ItemStackSimilar {
    @Override
    public String getName() {
        return "material";
    }

    @Override
    public boolean isSimilar(ItemStack itemStackA, ItemStack itemStackB) {
        return itemStackA.getType() == itemStackB.getType();
    }
}
