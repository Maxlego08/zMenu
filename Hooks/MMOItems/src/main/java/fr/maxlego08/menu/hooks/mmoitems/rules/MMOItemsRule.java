package fr.maxlego08.menu.hooks.mmoitems.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MMOItemsRule extends AbstractPluginItemRule {
    
    public MMOItemsRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        NBTItem nbtItem = NBTItem.get(itemStack);
        if (nbtItem.hasType()) {
            String itemType = nbtItem.getType();
            String id = nbtItem.getString("MMOITEMS_ITEM_ID");
            if (id != null && !id.isEmpty()) {
                return itemType + ":" + id;
            }
        }
        return null;
    }
}
