package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.rules.OrRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoRuleLoader
public class OrRuleLoader extends CompositeRuleLoader {

    @Override
    public @NotNull String getType() {
        return "or";
    }

    @Override
    protected Rule createRule(@NotNull List<@NotNull Rule> rules) {
        return new OrRule(rules);
    }
}