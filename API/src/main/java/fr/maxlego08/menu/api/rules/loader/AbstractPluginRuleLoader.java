package fr.maxlego08.menu.api.rules.loader;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class AbstractPluginRuleLoader implements RuleLoader {

    /**
     * Creates the plugin-specific rule instance.
     *
     * @param items      the list of item IDs (exact or wildcard)
     * @param ignoreCase whether matching should be case-insensitive
     * @return the constructed Rule
     */
    @NotNull
    protected abstract Rule createRule(@NotNull List<String> items, boolean ignoreCase);

    @Override
    @Nullable
    public Rule load(@NotNull Map<String, Object> configuration) {
        List<String> items = RuleConfigHelper.getStringList(configuration, "items");
        if (items.isEmpty()) return null;

        boolean ignoreCase = RuleConfigHelper.getBoolean(configuration, "ignore-case", true);
        return createRule(items, ignoreCase);
    }
}