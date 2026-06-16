package fr.maxlego08.menu.hooks.itemsadder.rules;

import dev.lone.itemsadder.api.CustomStack;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemsAdderRule extends AbstractPluginItemRule {
    public ItemsAdderRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    @Nullable
    protected String resolveId(@NotNull ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        return customStack != null ? customStack.getNamespacedID() : null;
    }
}
