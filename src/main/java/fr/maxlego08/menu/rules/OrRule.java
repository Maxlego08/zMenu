package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrRule implements Rule {
    private final List<Rule> children;

    public OrRule(@NotNull List<@NotNull Rule> children) {
        this.children = children;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        for (Rule child : this.children) {
            if (child.matches(context)) return true;
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.children.isEmpty();
    }
}
