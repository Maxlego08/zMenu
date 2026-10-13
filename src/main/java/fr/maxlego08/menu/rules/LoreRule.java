package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class LoreRule implements Rule {
    protected final List<String> values;
    protected final boolean ignoreCase;

    protected LoreRule(@NotNull List<@NotNull String> values, boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.values = ignoreCase
                ? values.stream().map(s -> s.toLowerCase(Locale.ROOT)).toList()
                : values;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        List<String> lore = context.getLore();
        for (String value : this.values) {
            for (String line : lore) {
                String normalizedLine = this.ignoreCase ? line.toLowerCase(Locale.ROOT) : line;
                if (this.matchesLine(normalizedLine, value)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.values.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "lore");
        String matchType = this.getMatchType();
        if (!matchType.equals("contains")) map.put("match-type", matchType);
        map.put("values", this.values);
        if (!this.ignoreCase) map.put("ignore-case", false);
        return map;
    }

    protected abstract boolean matchesLine(@NotNull String line, @NotNull String value);

    /**
     * @return the {@code match-type} the lore rule loader reads to build this rule
     */
    protected abstract @NotNull String getMatchType();
}
