package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

@RequiresPlugin("Oraxen")
@AutoRuleLoader
public class OraxenRuleLoader extends AbstractPluginRuleLoader {

    @Override
    public String getType() {
        return "oraxen";
    }

    @Override
    protected @NonNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new OraxenRule(items, ignoreCase);
    }
}