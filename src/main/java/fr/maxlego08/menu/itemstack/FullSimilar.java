package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class FullSimilar implements ItemStackSimilar {
    @Override
    public @NonNull String getName() {
        return "full";
    }

    @Override
    public boolean isSimilar(@NonNull ItemStack itemStackA, @NonNull ItemStack itemStackB) {
        return itemStackA.isSimilar(itemStackB);
    }
}
