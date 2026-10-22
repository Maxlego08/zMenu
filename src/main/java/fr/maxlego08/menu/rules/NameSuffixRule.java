package fr.maxlego08.menu.rules;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NameSuffixRule extends NameRule {
    public NameSuffixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase);
    }

    @Override
    protected boolean matchesName(@NotNull String displayName, @NotNull String name) {
        return displayName.endsWith(name);
    }

    @Override
    protected @NotNull String getMatchType() {
        return "suffix";
    }
}