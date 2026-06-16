package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftEngineRule extends AbstractPluginItemRule {

    public CraftEngineRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        BukkitItemDefinition bukkitItemDefinition = CraftEngineItems.byItemStack(itemStack);
        if (bukkitItemDefinition != null) {
            return bukkitItemDefinition.id().asString();
        }
        return null;
    }
}
