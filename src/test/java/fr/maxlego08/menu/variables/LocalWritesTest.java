package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.memory.MemoryStorageProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A value read for another server's change never hides a write of this server, and changes of an
 * offline player reach the store with the next batch.
 */
class LocalWritesTest {

    private static final VariableRef KILLS = VariableRef.global("kills");

    private VariableStore memory;
    private HeldStore store;
    private ZVariableService service;

    @BeforeEach
    void start() {
        this.memory = new MemoryStorageProvider().variables();
        this.store = new HeldStore(this.memory);
        this.service = new ZVariableService(null, this.store, "a", null, null, 0);
        this.memory.apply(List.of(new VariableMutation.Set(Variable.of(KILLS, "10", 0)))).join();
        this.service.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
    }

    private String cached(VariableRef ref) {
        return this.service.getCached(ref).map(Variable::text).orElse(null);
    }

    private void changedByB(VariableRef ref, int delta) {
        this.memory.apply(List.of(new VariableMutation.Increment(ref, BigDecimal.valueOf(delta), 0))).join();
        this.service.onEvent(new StorageEvent.VariableChanged("b", ref));
    }

    @Test
    void aChangeOfAnotherServerWaitsForTheLocalWriteInProgress() {
        CompletableFuture<Void> gate = this.store.holdIncrements();
        CompletableFuture<BigDecimal> local = this.service.increment(KILLS, BigDecimal.ONE, null);

        this.changedByB(KILLS, 5);
        assertEquals("11", this.cached(KILLS));

        gate.complete(null);
        assertEquals(0, BigDecimal.valueOf(16).compareTo(local.join()));
        assertEquals("16", this.cached(KILLS));
    }

    @Test
    void aValueReadBeforeALocalWriteIsReadAgain() {
        CompletableFuture<Void> gate = this.store.holdReads();
        this.changedByB(KILLS, 5);

        this.service.increment(KILLS, BigDecimal.ONE, null).join();
        assertEquals("16", this.cached(KILLS));

        this.store.releaseReads();
        gate.complete(null);
        assertEquals("16", this.cached(KILLS));
    }

    @Test
    void aSetForAnOfflinePlayerIsWrittenByTheNextBatch() {
        VariableRef rank = VariableRef.player(UUID.randomUUID(), "rank");
        this.service.set(rank, "vip", 0);

        assertTrue(this.service.isLoaded(VariableScope.PLAYER, rank.owner()));
        this.service.flush(false).join();

        assertFalse(this.service.hasPending(VariableScope.PLAYER, rank.owner()));
        assertEquals(Optional.of("vip"), this.memory.load(rank).join().map(Variable::text));
    }

    @Test
    void anIncrementOfAnOfflinePlayerCompletesWithTheStoredValue() {
        VariableRef coins = VariableRef.player(UUID.randomUUID(), "coins");
        this.memory.apply(List.of(new VariableMutation.Set(Variable.of(coins, "100", 0)))).join();

        assertEquals(0, BigDecimal.valueOf(105).compareTo(this.service.increment(coins, BigDecimal.valueOf(5), null).join()));
        assertEquals("105", this.cached(coins));
    }

    /**
     * Holds the additions until a gate opens, or completes the reads with the value they read
     * once a gate opens, as a slow database would.
     */
    private static final class HeldStore implements VariableStore {

        private final VariableStore delegate;
        private volatile CompletableFuture<Void> incrementGate;
        private volatile CompletableFuture<Void> readGate;

        private HeldStore(VariableStore delegate) {
            this.delegate = delegate;
        }

        private CompletableFuture<Void> holdIncrements() {
            this.incrementGate = new CompletableFuture<>();
            return this.incrementGate;
        }

        private CompletableFuture<Void> holdReads() {
            this.readGate = new CompletableFuture<>();
            return this.readGate;
        }

        private void releaseReads() {
            this.readGate = null;
        }

        @Override
        public @NotNull CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner) {
            return this.delegate.loadOwner(scope, owner);
        }

        @Override
        public @NotNull CompletableFuture<Optional<Variable>> load(@NotNull VariableRef ref) {
            Optional<Variable> read = this.delegate.load(ref).join();
            CompletableFuture<Void> gate = this.readGate;
            return gate == null ? CompletableFuture.completedFuture(read) : gate.thenApply(ignored -> read);
        }

        @Override
        public @NotNull CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations) {
            return this.delegate.apply(mutations);
        }

        @Override
        public @NotNull CompletableFuture<BigDecimal> incrementAndGet(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated) {
            CompletableFuture<Void> gate = this.incrementGate;
            if (gate == null) return this.delegate.incrementAndGet(ref, delta, expiresAtIfCreated);
            return gate.thenCompose(ignored -> this.delegate.incrementAndGet(ref, delta, expiresAtIfCreated));
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name) {
            return this.delegate.deleteName(scope, name);
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
            return this.delegate.deleteOwner(scope, owner);
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
            return this.delegate.deleteScope(scope);
        }

        @Override
        public @NotNull CompletableFuture<Set<String>> owners(@NotNull VariableScope scope) {
            return this.delegate.owners(scope);
        }

        @Override
        public @NotNull CompletableFuture<Set<String>> names(@NotNull VariableScope scope) {
            return this.delegate.names(scope);
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteExpired(long now) {
            return this.delegate.deleteExpired(now);
        }
    }
}
