package fr.maxlego08.menu.hooks.rules;

import com.nexomc.nexo.api.NexoItems;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NexoRule extends AbstractPluginItemRule {

    public NexoRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        return NexoItems.idFromItem(itemStack);
    }
}
