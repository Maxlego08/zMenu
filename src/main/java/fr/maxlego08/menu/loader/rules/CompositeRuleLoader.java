package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.registry.ZRuleLoaderRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CompositeRuleLoader implements RuleLoader {

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        List<Map<String, Object>> rulesConfig = RuleConfigHelper.getMapList(configuration, "rules");
        if (rulesConfig.isEmpty()) return null;

        ZRuleLoaderRegistry instance = ZRuleLoaderRegistry.getInstance();
        List<Rule> rules = rulesConfig.stream()
                .map(instance::loadRule)
                .filter(Objects::nonNull)
                .toList();

        if (rules.isEmpty()) return null;
        if (rules.size() == 1) return rules.getFirst();
        return createRule(rules);
    }

    protected abstract Rule createRule(@NotNull List<@NotNull Rule> rules);
}
