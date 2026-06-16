package fr.maxlego08.menu.hooks.executableitems.rules;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExecutableItemsRule extends AbstractPluginItemRule {

    protected ExecutableItemsRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        Optional<ExecutableItemInterface> executableItem = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack);
        return executableItem.map(ExecutableItemInterface::getId).orElse(null);
    }
}
