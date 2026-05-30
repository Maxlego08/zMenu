package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.MaterialSuffixRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class MaterialSuffixRuleLoader implements RuleLoader {

    @Override
    public @NotNull String getType() {
        return "material-suffix";
    }

    @Override
    public Rule load(@NotNull Map<String, Object> configuration) {
        List<String> suffixes = RuleConfigHelper.getStringList(configuration, "suffixes");
        if (suffixes.isEmpty()) {
            return null;
        }
        return new MaterialSuffixRule(suffixes);
    }
}
