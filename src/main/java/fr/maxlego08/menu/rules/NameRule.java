package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiPredicate;

public abstract class NameRule implements Rule {
    protected final List<String> names;
    private final BiPredicate<String, String> comparator;

    protected NameRule(@NotNull List<@NotNull String> names,@NotNull BiPredicate<@NotNull String,@NotNull String> comparator) {
        this.names = names;
        this.comparator = comparator;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        String displayName = context.getDisplayName();
        if (displayName == null) return false;
        for (String name : this.names) {
            if (this.comparator.test(displayName, name)) return true;
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.names.isEmpty();
    }
}