package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoRuleLoader
@RequiresPlugin("CraftEngine")
public class CraftEngineRuleLoader extends AbstractPluginRuleLoader {
    @Override
    protected @NotNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new CraftEngineRule(items, ignoreCase);
    }

    @Override
    @NotNull
    public List<@NotNull String> getAliases() {
        return List.of("craft-engine", "craft_engine", "ce");
    }

    @Override
    public @NotNull String getType() {
        return "craftengine";
    }
}
