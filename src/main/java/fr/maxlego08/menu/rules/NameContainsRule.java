package fr.maxlego08.menu.rules;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NameContainsRule extends NameRule {
    public NameContainsRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase);
    }

    @Override
    protected boolean matchesName(@NotNull String displayName, @NotNull String name) {
        return displayName.contains(name);
    }

    @Override
    protected @NotNull String getMatchType() {
        return "contains";
    }
}