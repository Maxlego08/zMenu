package fr.maxlego08.menu.api.storage.spi;

import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

/**
 * One change to a variable, applied by {@link VariableStore#apply(java.util.List)}.
 */
@ApiStatus.Experimental
public sealed interface VariableMutation {

    @NotNull
    VariableRef ref();

    /**
     * Replaces the value.
     */
    record Set(@NotNull Variable variable) implements VariableMutation {
        @Override
        public @NotNull VariableRef ref() {
            return this.variable.ref();
        }
    }

    /**
     * Adds to the value in the database itself, so concurrent increments from several servers add
     * up.
     *
     * @param delta              the amount, negative to subtract
     * @param expiresAtIfCreated the expiry time of a value this creates, 0 for never; an existing
     *                           value keeps its own; an expired value is replaced, as if it did
     *                           not exist
     */
    record Increment(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated) implements VariableMutation {
    }

    record Delete(@NotNull VariableRef ref) implements VariableMutation {
    }
}
