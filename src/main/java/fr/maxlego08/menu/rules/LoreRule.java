package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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
                String normalizedLine = ignoreCase ? line.toLowerCase(Locale.ROOT) : line;
                if (matchesLine(normalizedLine, value)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.values.isEmpty();
    }

    protected abstract boolean matchesLine(@NotNull String line, @NotNull String value);
}
