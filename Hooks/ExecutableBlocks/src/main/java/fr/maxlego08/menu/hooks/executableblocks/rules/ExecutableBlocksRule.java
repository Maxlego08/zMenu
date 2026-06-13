package fr.maxlego08.menu.hooks.executableblocks.rules;

import com.ssomar.score.api.executableblocks.ExecutableBlocksAPI;
import com.ssomar.score.api.executableblocks.config.ExecutableBlockInterface;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ExecutableBlocksRule extends AbstractPluginItemRule {

    public ExecutableBlocksRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        Optional<ExecutableBlockInterface> executableBlock = ExecutableBlocksAPI.getExecutableBlocksManager().getExecutableBlock(itemStack);
        return executableBlock.map(ExecutableBlockInterface::getId).orElse(null);
    }
}
