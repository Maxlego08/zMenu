package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.ContainsLoreRule;
import fr.maxlego08.menu.rules.LoreEqualsRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class LoreRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "lore";
    }

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        MatchType matchType = RuleConfigHelper.getEnum(configuration, "match-type", MatchType.class, MatchType.CONTAINS);
        List<String> values = RuleConfigHelper.getStringList(configuration, "values");
        boolean ignoreCase = RuleConfigHelper.getBoolean(configuration, "ignore-case", true);
        return switch (matchType) {
            case EQUALS -> new LoreEqualsRule(values, ignoreCase);
            case CONTAINS -> new ContainsLoreRule(values, ignoreCase);
        };
    }

    private enum MatchType {
        EQUALS,
        CONTAINS
    }
}
