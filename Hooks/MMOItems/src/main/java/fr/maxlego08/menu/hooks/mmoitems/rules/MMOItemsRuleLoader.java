package fr.maxlego08.menu.hooks.mmoitems.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

@AutoRuleLoader
@RequiresPlugin("MMOItems")
public class MMOItemsRuleLoader extends AbstractPluginRuleLoader {
    @Override
    protected @NotNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new MMOItemsRule(items, ignoreCase);
    }

    @Override
    public @NonNull List<String> getAliases() {
        return List.of("mmo-items", "mmo_items");
    }

    @Override
    public @NotNull String getType() {
        return "mmoitems";
    }
}
