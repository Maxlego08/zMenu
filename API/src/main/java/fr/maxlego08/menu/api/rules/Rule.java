package fr.maxlego08.menu.api.rules;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface Rule {

    boolean matches(@NotNull ItemRuleContext context);

    default boolean isValid() {
        return true;
    }

    /**
     * Writes this rule back as the map its {@link fr.maxlego08.menu.api.rules.loader.RuleLoader} reads,
     * starting with the loader's {@code type}.
     *
     * @return the rule configuration
     * @throws UnsupportedOperationException if this rule cannot be written back
     */
    default @NotNull Map<String, Object> serialize() {
        throw new UnsupportedOperationException("The rule " + this.getClass().getName() + " cannot be serialized");
    }
}
