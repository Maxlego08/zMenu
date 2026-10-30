package fr.maxlego08.menu.storage.memory;

import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Variables kept in memory. Every change is made under one lock, so increments are atomic.
 */
final class MemoryVariableStore implements VariableStore {

    private static final CompletableFuture<Void> DONE = CompletableFuture.completedFuture(null);

    private final Map<VariableRef, Variable> variables = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    private BigDecimal increment(VariableRef ref, BigDecimal delta, long expiresAtIfCreated) {
        Variable current = this.variables.get(ref);
        BigDecimal base = current == null || current.number() == null ? BigDecimal.ZERO : current.number();
        long expiresAt = current == null ? expiresAtIfCreated : current.expiresAt();
        BigDecimal value = base.add(delta);
        this.variables.put(ref, Variable.ofNumber(ref, value, expiresAt));
        return value;
    }

    @Override
    public @NotNull CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner) {
        List<Variable> found = new ArrayList<>();
        this.variables.forEach((ref, variable) -> {
            if (ref.scope() == scope && ref.owner().equals(owner)) found.add(variable);
        });
        return CompletableFuture.completedFuture(found);
    }

    @Override
    public @NotNull CompletableFuture<Optional<Variable>> load(@NotNull VariableRef ref) {
        return CompletableFuture.completedFuture(Optional.ofNullable(this.variables.get(ref)));
    }

    @Override
    public @NotNull CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations) {
        synchronized (this.lock) {
            for (VariableMutation mutation : mutations) {
                switch (mutation) {
                    case VariableMutation.Set set -> this.variables.put(set.ref(), set.variable());
                    case VariableMutation.Increment increment -> this.increment(increment.ref(), increment.delta(), increment.expiresAtIfCreated());
                    case VariableMutation.Delete delete -> this.variables.remove(delete.ref());
                }
            }
        }
        return DONE;
    }

    @Override
    public @NotNull CompletableFuture<BigDecimal> incrementAndGet(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated) {
        synchronized (this.lock) {
            return CompletableFuture.completedFuture(this.increment(ref, delta, expiresAtIfCreated));
        }
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name) {
        this.variables.keySet().removeIf(ref -> ref.scope() == scope && ref.name().equals(name));
        return DONE;
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
        this.variables.keySet().removeIf(ref -> ref.scope() == scope && ref.owner().equals(owner));
        return DONE;
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
        this.variables.keySet().removeIf(ref -> ref.scope() == scope);
        return DONE;
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> names(@NotNull VariableScope scope) {
        Set<String> names = new HashSet<>();
        this.variables.keySet().forEach(ref -> {
            if (ref.scope() == scope) names.add(ref.name());
        });
        return CompletableFuture.completedFuture(names);
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteExpired(long now) {
        this.variables.values().removeIf(variable -> variable.expiresAt() != 0 && variable.expiresAt() < now);
        return DONE;
    }
}
