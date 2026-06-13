package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.rules.AndRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AutoRuleLoader
public class AndRuleLoader extends CompositeRuleLoader {

    @Override
    public @NotNull String getType() {
        return "and";
    }

    @Override
    protected Rule createRule(@NotNull List<@NotNull Rule> rules) {
        return new AndRule(rules);
    }
}