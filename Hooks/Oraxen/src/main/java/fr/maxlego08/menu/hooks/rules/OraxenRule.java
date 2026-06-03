package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OraxenRule extends AbstractPluginItemRule {

    public OraxenRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    @Nullable
    protected String resolveId(@NotNull ItemStack itemStack) {
        return OraxenItems.getIdByItem(itemStack);
    }
}
