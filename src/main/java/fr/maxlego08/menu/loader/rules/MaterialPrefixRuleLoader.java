package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.MaterialPrefixRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class MaterialPrefixRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "material-prefix";
    }

    @Override
    public Rule load(@NotNull Map<String, Object> configuration) {
        List<String> prefixes = RuleConfigHelper.getStringList(configuration, "prefixes");
        if (prefixes.isEmpty()) {
            return null;
        }
        return new MaterialPrefixRule(prefixes);
    }
}
