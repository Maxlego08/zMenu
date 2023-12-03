package fr.maxlego08.menu.dupe;

import dev.triumphteam.gui.components.util.ItemNbt;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackCompound;
import org.bukkit.inventory.ItemStack;

public class NMSDupeManager implements DupeManager {

    @Override
    public ItemStack protectItem(ItemStack itemStack) {
        return ItemStackCompound.itemStackCompound.setBoolean(itemStack, DupeManager.KEY, true);
    }

    @Override
    public boolean isDupeItem(ItemStack itemStack) {
        return ItemStackCompound.itemStackCompound.isKey(itemStack, DupeManager.KEY);
    }
}
