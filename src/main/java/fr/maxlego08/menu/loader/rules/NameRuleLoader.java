package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.NameContainsRule;
import fr.maxlego08.menu.rules.NameExactRule;
import fr.maxlego08.menu.rules.NamePrefixRule;
import fr.maxlego08.menu.rules.NameSuffixRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class NameRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "name";
    }

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        List<String> names = RuleConfigHelper.getStringList(configuration, "names");

        if (names.isEmpty()) {
            return null;
        }

        boolean ignoreCase = RuleConfigHelper.getBoolean(configuration, "ignore-case", true);
        MatchType matchType = RuleConfigHelper.getEnum(configuration, "match-type", MatchType.class, MatchType.EXACT);
        return switch (matchType) {
            case EXACT -> new NameExactRule(names, ignoreCase);
            case PREFIX -> new NamePrefixRule(names, ignoreCase);
            case SUFFIX -> new NameSuffixRule(names, ignoreCase);
            case CONTAINS -> new NameContainsRule(names, ignoreCase);
        };
    }

    private enum MatchType {
        EXACT,
        PREFIX,
        SUFFIX,
        CONTAINS
    }
}
