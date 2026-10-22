package fr.maxlego08.menu.rules;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NamePrefixRule extends NameRule {
    public NamePrefixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase);
    }

    @Override
    protected boolean matchesName(@NotNull String displayName, @NotNull String name) {
        return displayName.startsWith(name);
    }

    @Override
    protected @NotNull String getMatchType() {
        return "prefix";
    }
}