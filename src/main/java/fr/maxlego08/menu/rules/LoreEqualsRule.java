package fr.maxlego08.menu.rules;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoreEqualsRule extends LoreRule {
    public LoreEqualsRule(@NotNull List<@NotNull String> values, boolean ignoreCase) {
        super(values, ignoreCase);
    }

    @Override
    protected boolean matchesLine(@NotNull String line, @NotNull String value) {
        return line.equals(value);
    }
}