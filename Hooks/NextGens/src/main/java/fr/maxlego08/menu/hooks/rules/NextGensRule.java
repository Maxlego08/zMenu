package fr.maxlego08.menu.hooks.rules;

import com.muhammaddaffa.nextgens.NextGens;
import com.muhammaddaffa.nextgens.generators.Generator;
import fr.maxlego08.menu.api.rules.AbstractPluginItemRule;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NextGensRule extends AbstractPluginItemRule {

    public NextGensRule(@NotNull List<String> itemIds, boolean ignoreCase) {
        super(itemIds, ignoreCase);
    }

    @Override
    protected @Nullable String resolveId(@NotNull ItemStack itemStack) {
        Generator generator = NextGens.getApi().getGenerator(itemStack);
        return generator != null ? generator.id() : null;
    }
}
