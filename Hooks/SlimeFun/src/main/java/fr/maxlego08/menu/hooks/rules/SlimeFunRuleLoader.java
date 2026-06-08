package fr.maxlego08.menu.hooks.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.AbstractPluginRuleLoader;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

@AutoRuleLoader
@RequiresPlugin("Slimefun")
public class SlimeFunRuleLoader extends AbstractPluginRuleLoader {
    @Override
    protected @NotNull Rule createRule(@NotNull List<String> items, boolean ignoreCase) {
        return new SlimeFunRule(items, ignoreCase);
    }

    @Override
    public @NonNull List<String> getAliases() {
        return List.of("slime-fun", "slime_fun", "sf");
    }

    @Override
    public @NotNull String getType() {
        return "slimefun";
    }
}
