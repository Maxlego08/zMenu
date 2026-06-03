package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.nova.api.Nova;
import xyz.xenondevs.nova.api.item.NovaItem;

import java.util.List;

public class NovaRule extends AbstractPluginItemRule {

    public NovaRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        NovaItem novaItem = Nova.getNova().getItemRegistry().getOrNull(itemStack);
        return novaItem != null ? novaItem.getId().toString() : null;
    }
}
