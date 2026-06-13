package fr.maxlego08.menu.hooks.itemsadder.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

@RequiresPlugin("ItemsAdder")
@AutoRuleLoader
public class ItemsAdderRuleLoader extends AbstractPluginRuleLoader {

    @Override
    public @NonNull String getType() {
        return "itemsadder";
    }

    @Override
    protected @NonNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new ItemsAdderRule(items, ignoreCase);
    }
}