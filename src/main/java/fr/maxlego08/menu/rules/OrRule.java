package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrRule implements Rule {
    private final List<Rule> children;

    public OrRule(@NotNull List<@NotNull Rule> children) {
        this.children = children;
    }

    @Override
    public boolean matches(@NotNull RuleContext context) {
        for (Rule child : this.children) {
            if (child.matches(context)) return true;
        }
        return false;
    }
}
