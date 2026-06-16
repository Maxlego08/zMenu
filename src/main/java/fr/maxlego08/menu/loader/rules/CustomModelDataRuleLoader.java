package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.AndRule;
import fr.maxlego08.menu.rules.ModelDataRangeRule;
import fr.maxlego08.menu.rules.ModelDataRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@AutoRuleLoader
public class CustomModelDataRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "custom-model-data";
    }

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        List<Rule> rules = new ArrayList<>();

        List<Map<String, Object>> ranges = RuleConfigHelper.getMapList(configuration, "ranges");
        if (!ranges.isEmpty()) {
            for (Map<String, Object> range : ranges) {
                int min = RuleConfigHelper.getInt(range, "min", 0);
                int max = RuleConfigHelper.getInt(range, "max", Integer.MAX_VALUE);
                rules.add(new ModelDataRangeRule(min, max));
            }
        }

        List<Integer> values = RuleConfigHelper.getIntegerList(configuration, "values");
        if (!values.isEmpty()) {
            rules.add(new ModelDataRule(new HashSet<>(values)));
        }

        if (rules.isEmpty()) {
            return null;
        }

        if (rules.size() == 1) {
            return rules.getFirst();
        }
        return new AndRule(rules);
    }
}
