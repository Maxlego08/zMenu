package fr.maxlego08.menu.dupe;

import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.common.utils.nms.ItemStackCompound;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public class NMSDupeManager implements DupeManager {

    @Override
    public @NonNull ItemStack protectItem(@NonNull ItemStack itemStack) {
        return ItemStackCompound.itemStackCompound.setBoolean(itemStack, DupeManager.KEY, true);
    }

    @Override
    public boolean isDupeItem(@NonNull ItemStack itemStack) {
        return ItemStackCompound.itemStackCompound.isKey(itemStack, DupeManager.KEY);
    }
}
