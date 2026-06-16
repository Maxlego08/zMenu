package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlimeFunRule extends AbstractPluginItemRule {

    public SlimeFunRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        return slimefunItem != null ? slimefunItem.getId() : null;
    }
}
