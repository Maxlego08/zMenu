package fr.maxlego08.menu.api.variable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Reads and writes variables of every scope: player data, per-server values and values shared by
 * every server.
 *
 * <p>Server and global variables are always in memory. A player's variables are in memory while
 * they are online and for a few minutes after an offline lookup; use {@link #load(VariableScope, String)}
 * before reading an offline player.</p>
 *
 * <p>Changes are visible at once on this server. Player variables are written to the database in
 * batches, server and global variables at once.</p>
 */
@ApiStatus.Experimental
public interface VariableService {

    /**
     * @return the id of this server, the owner of its {@link VariableScope#SERVER} variables
     */
    @NotNull
    String serverId();

    /**
     * @return the variable, if its owner is in memory and it has not expired
     */
    @NotNull
    Optional<Variable> getCached(@NotNull VariableRef ref);

    /**
     * Reads the owner of the variable first when it is not in memory.
     */
    @NotNull
    CompletableFuture<Optional<Variable>> get(@NotNull VariableRef ref);

    /**
     * Reads every variable of an owner, unless they are already in memory, and keeps them.
     *
     * @return the variables of the owner
     */
    @NotNull
    CompletableFuture<Collection<Variable>> load(@NotNull VariableScope scope, @NotNull String owner);

    /**
     * @return the variables of an owner in memory, without reading the database
     */
    @NotNull
    Collection<Variable> getCachedAll(@NotNull VariableScope scope, @NotNull String owner);

    /**
     * Replaces a value. A text that reads as a number is kept as a number.
     *
     * @param ttl how long the value lasts, or null for ever
     */
    void set(@NotNull VariableRef ref, @NotNull String value, @Nullable Duration ttl);

    /**
     * Replaces a value, keeping its expiry time as given.
     *
     * @param expiresAt when it expires, in milliseconds since the epoch, or 0 for never
     */
    void set(@NotNull VariableRef ref, @NotNull String value, long expiresAt);

    /**
     * Adds to a numeric value. The database adds the amount itself, so two servers adding at the
     * same time never lose each other's change. A missing value counts as 0; a text that is not a
     * number is replaced by the amount.
     *
     * @param delta the amount, negative to subtract
     * @param ttl   how long a value created by this call lasts, or null for ever
     * @return completes with the new value as this server sees it
     */
    @NotNull
    CompletableFuture<BigDecimal> increment(@NotNull VariableRef ref, @NotNull BigDecimal delta, @Nullable Duration ttl);

    void delete(@NotNull VariableRef ref);

    /**
     * Deletes a name for every owner of a scope.
     */
    @NotNull
    CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name);

    /**
     * Deletes every variable of an owner.
     */
    @NotNull
    CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner);

    /**
     * Deletes every variable of a scope.
     */
    @NotNull
    CompletableFuture<Void> deleteScope(@NotNull VariableScope scope);

    /**
     * @return every name used in a scope, by any owner
     */
    @NotNull
    Set<String> names(@NotNull VariableScope scope);
}
