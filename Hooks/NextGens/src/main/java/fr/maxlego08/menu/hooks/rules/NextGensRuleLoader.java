package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoRuleLoader
@RequiresPlugin("NextGens")
public class NextGensRuleLoader extends AbstractPluginRuleLoader {
    @Override
    protected @NotNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new NextGensRule(items, ignoreCase);
    }

    @Override
    public @NotNull String getType() {
        return "nextgens";
    }
}
