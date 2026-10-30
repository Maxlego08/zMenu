package fr.maxlego08.menu.api.storage.spi;

import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * The variables of every scope: player data, per-server values and values shared by every server.
 */
@ApiStatus.Experimental
public interface VariableStore {

    /**
     * @return every variable of an owner, expired ones included
     */
    @NotNull
    CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner);

    /**
     * Applies changes in the order given.
     */
    @NotNull
    CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations);

    /**
     * Adds to a value in the database and reads it back, in one transaction.
     *
     * @return the value after the addition
     */
    @NotNull
    CompletableFuture<BigDecimal> incrementAndGet(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated);

    /**
     * Deletes a name for every owner of a scope.
     */
    @NotNull
    CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name);

    @NotNull
    CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner);

    @NotNull
    CompletableFuture<Void> deleteScope(@NotNull VariableScope scope);

    /**
     * @return every name used in a scope
     */
    @NotNull
    CompletableFuture<Set<String>> names(@NotNull VariableScope scope);

    /**
     * Deletes the values that expired before a time.
     *
     * @param now the time, in milliseconds since the epoch
     */
    @NotNull
    CompletableFuture<Void> deleteExpired(long now);
}
