package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class NameRule implements Rule {
    protected final List<String> names;
    protected final boolean ignoreCase;

    protected NameRule(@NotNull List<@NotNull String> names, boolean ignoreCase) {
        this.names = names;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        String displayName = context.getDisplayName();
        if (displayName == null) return false;
        String normalizedDisplayName = this.ignoreCase ? displayName.toLowerCase(Locale.ROOT) : displayName;
        for (String name : this.names) {
            String normalizedName = this.ignoreCase ? name.toLowerCase(Locale.ROOT) : name;
            if (this.matchesName(normalizedDisplayName, normalizedName)) return true;
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.names.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "name");
        String matchType = this.getMatchType();
        if (!matchType.equals("exact")) map.put("match-type", matchType);
        map.put("names", this.names);
        if (!this.ignoreCase) map.put("ignore-case", false);
        return map;
    }

    /**
     * @param displayName the item display name, lower-cased when the rule ignores case
     * @param name        the configured name, lower-cased when the rule ignores case
     * @return whether the display name matches the configured name
     */
    protected abstract boolean matchesName(@NotNull String displayName, @NotNull String name);

    /**
     * @return the {@code match-type} the name rule loader reads to build this rule
     */
    protected abstract @NotNull String getMatchType();
}
