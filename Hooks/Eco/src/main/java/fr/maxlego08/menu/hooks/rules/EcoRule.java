package fr.maxlego08.menu.hooks.rules;

import com.willfp.eco.core.items.CustomItem;
import com.willfp.eco.core.items.Items;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EcoRule extends AbstractPluginItemRule {

    public EcoRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        CustomItem customItem = Items.getCustomItem(itemStack);
        if (customItem != null) {
            return customItem.getKey().toString();
        }
        return null;
    }
}
