package fr.maxlego08.menu.api.rules;

import org.jetbrains.annotations.NotNull;

public interface Rule {

    boolean matches(@NotNull ItemRuleContext context);

    default boolean isValid() {
        return true;
    }
}
