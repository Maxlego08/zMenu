package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.MaterialContainsRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class MaterialContainsRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "material-contains";
    }

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        List<String> patterns = RuleConfigHelper.getStringList(configuration, "patterns");
        if (patterns.isEmpty()) {
            return null;
        }
        return new MaterialContainsRule(patterns);
    }
}
