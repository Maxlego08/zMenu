package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.storage.spi.MessageBroker;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.api.variable.VariableService;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * The variables of every scope, kept in memory per owner and written through the storage
 * provider.
 *
 * <p>Server and global variables are read when zMenu starts and written at once. A player's
 * variables are read just before they join, kept while they are online and for
 * {@link #OFFLINE_TTL_MILLIS} after an offline lookup, and written in batches: changes waiting for
 * the next batch are merged, so ten clicks adding 1 become one addition of 10.</p>
 *
 * <p>Additions reach the database as additions, never as the value this server computed, so two
 * servers adding to the same value at the same time both count.</p>
 */
public class ZVariableService implements VariableService, Listener {

    private static final long OFFLINE_TTL_MILLIS = TimeUnit.MINUTES.toMillis(5);
    private static final long PRE_LOGIN_TIMEOUT_SECONDS = 10;
    private static final long PURGE_INTERVAL_MINUTES = 10;
    private static final long HEARTBEAT_SECONDS = 10;
    private static final long STALE_MILLIS = TimeUnit.SECONDS.toMillis(30);
    private static final long HANDOFF_POLL_MILLIS = 250;

    private final MenuPlugin plugin;
    private final VariableStore store;
    private final String serverId;
    private final String instanceId = UUID.randomUUID().toString();
    private final @Nullable MessageBroker broker;
    private final @Nullable SessionStore sessions;
    private final long handoffTimeoutMillis;
    private final Map<UUID, CompletableFuture<Void>> releases = new ConcurrentHashMap<>();
    private final Map<OwnerKey, OwnerCache> owners = new ConcurrentHashMap<>();
    private final Map<OwnerKey, CompletableFuture<Collection<Variable>>> loading = new ConcurrentHashMap<>();
    private final Map<VariableScope, Set<String>> names = new EnumMap<>(VariableScope.class);

    /**
     * Player changes waiting for the next batch.
     */
    private final Map<VariableRef, VariableMutation> pending = new ConcurrentHashMap<>();

    /**
     * Held while handing changes to the store, so a delete queued by one thread can never be
     * overtaken by changes another thread took out of {@link #pending} just before it.
     */
    private final Object writeLock = new Object();

    /**
     * @param broker               carries changes between servers, or null on a single server
     * @param sessions             tracks which server each player is on, or null on a single server
     * @param handoffTimeoutMillis how long a joining player waits for the server they left
     */
    public ZVariableService(@NotNull MenuPlugin plugin, @NotNull VariableStore store, @NotNull String serverId,
                            @Nullable MessageBroker broker, @Nullable SessionStore sessions, long handoffTimeoutMillis) {
        this.plugin = plugin;
        this.store = store;
        this.serverId = serverId;
        this.broker = broker;
        this.sessions = sessions;
        this.handoffTimeoutMillis = handoffTimeoutMillis;
        for (VariableScope scope : VariableScope.values()) {
            this.names.put(scope, ConcurrentHashMap.newKeySet());
        }
        if (broker != null) broker.subscribe(this::onEvent);
    }

    /**
     * Reads the server and global variables, the variables of the players already online and the
     * names used in each scope, then starts the eviction and purge tasks.
     */
    public void enable() {
        for (VariableScope scope : List.of(VariableScope.SERVER, VariableScope.GLOBAL)) {
            try {
                this.load(scope, this.ownerOf(scope, null)).join();
            } catch (Exception exception) {
                Logger.error("Unable to load the " + scope.id() + " variables", exception);
            }
        }

        for (VariableScope scope : VariableScope.values()) {
            this.store.names(scope).whenComplete((found, throwable) -> {
                if (throwable != null) Logger.error("Unable to load the " + scope.id() + " variable names", throwable);
                else this.names.get(scope).addAll(found);
            });
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.load(VariableScope.PLAYER, player.getUniqueId().toString()).thenRun(() -> this.cache(VariableScope.PLAYER, player.getUniqueId().toString()).setOnline(true));
        }

        this.plugin.getScheduler().runTimerAsync(this::evictOfflinePlayers, 1, 1, TimeUnit.MINUTES);
        this.plugin.getScheduler().runTimerAsync(this::purgeExpired, 1, PURGE_INTERVAL_MINUTES, TimeUnit.MINUTES);

        if (this.sessions != null) {
            this.checkDuplicateServerId();
            this.heartbeat();
            this.plugin.getScheduler().runTimerAsync(this::heartbeat, HEARTBEAT_SECONDS, HEARTBEAT_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * Marks this server as stopped, so its players can join another server at once and the next
     * start is not taken for a second server with the same id.
     */
    public void disable() {
        if (this.sessions == null) return;
        this.logFailure(this.sessions.heartbeat(this.serverId, this.instanceId, 0), "mark this server as stopped");
    }

    /**
     * Reports another running server with the same {@code server-id}: their server variables, saved
     * inventories and player sessions would mix.
     */
    void checkDuplicateServerId() {
        if (this.sessions == null) return;
        try {
            this.sessions.loadServer(this.serverId).join().ifPresent(instance -> {
                if (!instance.instanceId().equals(this.instanceId) && System.currentTimeMillis() - instance.heartbeat() < STALE_MILLIS) {
                    Logger.error("Another server is running with the server-id " + this.serverId + ". Every server sharing the database needs its own server-id in config.yml.", null);
                }
            });
        } catch (Exception exception) {
            Logger.error("Unable to check the server-id " + this.serverId, exception);
        }
    }

    private void heartbeat() {
        if (this.sessions == null) return;
        this.logFailure(this.sessions.heartbeat(this.serverId, this.instanceId, System.currentTimeMillis()), "send the server heartbeat");
    }

    private void publishCleared(VariableScope scope, StorageEvent event) {
        if (scope != VariableScope.SERVER) this.publish(List.of(event));
    }

    private void publish(List<StorageEvent> events) {
        if (this.broker == null || events.isEmpty()) return;
        this.logFailure(this.broker.publish(events), "send the storage events");
    }

    /**
     * Server variables belong to one server, so only player and global changes are sent.
     */
    private void publishChanged(Collection<VariableRef> refs) {
        List<StorageEvent> events = new ArrayList<>();
        for (VariableRef ref : refs) {
            if (ref.scope() != VariableScope.SERVER) events.add(new StorageEvent.VariableChanged(this.serverId, ref));
        }
        this.publish(events);
    }

    /**
     * Applies a change made by another server. The value is read again from the store, and a
     * change of this server still waiting to be written is laid over it: an addition is added to
     * the new value, a replacement or a removal wins.
     */
    void onEvent(@NotNull StorageEvent event) {
        if (event.origin().equals(this.serverId)) return;
        switch (event) {
            case StorageEvent.VariableChanged changed -> {
                VariableRef ref = changed.ref();
                if (!this.isLoaded(ref.scope(), ref.owner())) return;
                this.store.load(ref).whenComplete((loaded, throwable) -> {
                    if (throwable != null) {
                        Logger.error("Unable to read the variable " + ref.name() + " changed by " + event.origin(), throwable);
                        return;
                    }
                    this.applyRemote(ref, loaded.orElse(null));
                });
            }
            case StorageEvent.NameCleared cleared -> this.owners.forEach((key, cache) -> {
                if (key.scope() == cleared.scope()) this.removeUnlessPending(cache, new VariableRef(key.scope(), key.owner(), cleared.name()));
            });
            case StorageEvent.OwnerCleared cleared -> {
                OwnerCache cache = this.owners.get(new OwnerKey(cleared.scope(), cleared.owner()));
                if (cache != null) this.clearUnlessPending(cache, cleared.scope(), cleared.owner());
            }
            case StorageEvent.ScopeCleared cleared -> this.owners.forEach((key, cache) -> {
                if (key.scope() == cleared.scope()) this.clearUnlessPending(cache, key.scope(), key.owner());
            });
            case StorageEvent.SessionReleased released -> {
                CompletableFuture<Void> waiting = this.releases.get(released.playerId());
                if (waiting != null) waiting.complete(null);
            }
        }
    }

    private void applyRemote(VariableRef ref, @Nullable Variable loaded) {
        OwnerCache cache = this.owners.get(new OwnerKey(ref.scope(), ref.owner()));
        if (cache == null) return;
        synchronized (cache) {
            VariableMutation waiting = this.pending.get(ref);
            if (waiting instanceof VariableMutation.Increment increment) {
                long expiresAt = loaded == null ? increment.expiresAtIfCreated() : loaded.expiresAt();
                cache.values.put(ref.name(), Variable.ofNumber(ref, base(loaded).add(increment.delta()), expiresAt));
            } else if (waiting == null) {
                if (loaded == null) cache.values.remove(ref.name());
                else {
                    cache.values.put(ref.name(), loaded);
                    this.names.get(ref.scope()).add(ref.name());
                }
            }
        }
    }

    private void removeUnlessPending(OwnerCache cache, VariableRef ref) {
        if (!this.pending.containsKey(ref)) cache.values.remove(ref.name());
    }

    private void clearUnlessPending(OwnerCache cache, VariableScope scope, String owner) {
        cache.values.keySet().removeIf(name -> !this.pending.containsKey(new VariableRef(scope, owner, name)));
    }

    /**
     * Waits for the server a player comes from to store their data, then records that they are on
     * this server.
     *
     * <p>When the player was last on another server, the variables this server kept from an
     * earlier visit are forgotten, so they are read again.</p>
     */
    void prepareLogin(@NotNull UUID playerId) {
        if (this.sessions == null) return;

        Optional<SessionStore.PlayerSession> session = this.sessions.load(playerId).join();
        if (session.isPresent() && !session.get().serverId().equals(this.serverId)) {
            if (this.isHeldElsewhere(session.get()) && !this.waitForRelease(playerId)) {
                Logger.error("The server " + session.get().serverId() + " did not release " + playerId + " within " + this.handoffTimeoutMillis
                        + " ms, their variables are read anyway.", null);
            }
            String owner = playerId.toString();
            if (!this.hasPending(VariableScope.PLAYER, owner)) this.owners.remove(new OwnerKey(VariableScope.PLAYER, owner));
        }

        this.sessions.claim(playerId, this.serverId, System.currentTimeMillis()).join();
    }

    private boolean isHeldElsewhere(SessionStore.PlayerSession session) {
        return session.online() && !session.serverId().equals(this.serverId) && System.currentTimeMillis() - session.heartbeat() < STALE_MILLIS;
    }

    /**
     * @return whether the player was released before the timeout
     */
    private boolean waitForRelease(UUID playerId) {
        CompletableFuture<Void> released = this.releases.computeIfAbsent(playerId, id -> new CompletableFuture<>());
        long deadline = System.currentTimeMillis() + this.handoffTimeoutMillis;
        try {
            while (System.currentTimeMillis() < deadline) {
                try {
                    released.get(Math.min(HANDOFF_POLL_MILLIS, Math.max(1, deadline - System.currentTimeMillis())), TimeUnit.MILLISECONDS);
                    return true;
                } catch (java.util.concurrent.TimeoutException ignored) {
                }
                Optional<SessionStore.PlayerSession> session = this.sessions.load(playerId).join();
                if (session.isEmpty() || !this.isHeldElsewhere(session.get())) return true;
            }
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception exception) {
            Logger.error("Unable to wait for the release of " + playerId, exception);
            return false;
        } finally {
            this.releases.remove(playerId, released);
        }
    }

    /**
     * Stores what a leaving player changed, then releases them, so the server they join next reads
     * up-to-date values.
     */
    @NotNull
    CompletableFuture<Void> releasePlayer(@NotNull UUID playerId) {
        String owner = playerId.toString();
        CompletableFuture<Void> stored = this.flushOwner(VariableScope.PLAYER, owner);
        if (this.sessions == null) return stored;
        return stored.handle((ignored, throwable) -> null)
                .thenCompose(ignored -> this.sessions.release(playerId, this.serverId))
                .thenRun(() -> this.publish(List.of(new StorageEvent.SessionReleased(this.serverId, playerId))));
    }

    private CompletableFuture<Void> flushOwner(VariableScope scope, String owner) {
        synchronized (this.writeLock) {
            List<VariableMutation> mutations = this.drain(ref -> ref.scope() == scope && ref.owner().equals(owner));
            return this.applyAndPublish(mutations);
        }
    }

    private CompletableFuture<Void> applyAndPublish(List<VariableMutation> mutations) {
        if (mutations.isEmpty()) return CompletableFuture.completedFuture(null);
        List<VariableRef> refs = new ArrayList<>(mutations.size());
        mutations.forEach(mutation -> refs.add(mutation.ref()));
        return this.logFailure(this.store.apply(mutations), "write the variables").thenRun(() -> this.publishChanged(refs));
    }

    /**
     * @return the owner of a scope: the given player for {@link VariableScope#PLAYER}, this server
     * for {@link VariableScope#SERVER}, everyone for {@link VariableScope#GLOBAL}
     */
    public @NotNull String ownerOf(@NotNull VariableScope scope, @Nullable UUID playerId) {
        return switch (scope) {
            case PLAYER -> {
                if (playerId == null) throw new IllegalArgumentException("A player variable needs a player");
                yield playerId.toString();
            }
            case SERVER -> this.serverId;
            case GLOBAL -> VariableRef.GLOBAL_OWNER;
        };
    }

    public @NotNull VariableRef ref(@NotNull VariableScope scope, @Nullable UUID playerId, @NotNull String name) {
        return new VariableRef(scope, this.ownerOf(scope, playerId), name);
    }

    @Override
    public @NotNull String serverId() {
        return this.serverId;
    }

    private OwnerCache cache(VariableScope scope, String owner) {
        return this.owners.computeIfAbsent(new OwnerKey(scope, owner), key -> new OwnerCache());
    }

    private @Nullable OwnerCache existing(VariableRef ref) {
        OwnerCache cache = this.owners.get(new OwnerKey(ref.scope(), ref.owner()));
        if (cache != null) cache.touch();
        return cache;
    }

    /**
     * @return whether the variables of an owner are in memory
     */
    public boolean isLoaded(@NotNull VariableScope scope, @NotNull String owner) {
        OwnerCache cache = this.owners.get(new OwnerKey(scope, owner));
        return cache != null && cache.loaded;
    }

    @Override
    public @NotNull Optional<Variable> getCached(@NotNull VariableRef ref) {
        OwnerCache cache = this.existing(ref);
        if (cache == null) return Optional.empty();
        Variable variable = cache.values.get(ref.name());
        if (variable == null) return Optional.empty();
        if (variable.isExpired()) {
            this.delete(ref);
            return Optional.empty();
        }
        return Optional.of(variable);
    }

    @Override
    public @NotNull CompletableFuture<Optional<Variable>> get(@NotNull VariableRef ref) {
        return this.load(ref.scope(), ref.owner()).thenApply(loaded -> this.getCached(ref));
    }

    @Override
    public @NotNull Collection<Variable> getCachedAll(@NotNull VariableScope scope, @NotNull String owner) {
        OwnerCache cache = this.owners.get(new OwnerKey(scope, owner));
        if (cache == null) return List.of();
        cache.touch();
        List<Variable> variables = new ArrayList<>();
        cache.values.values().forEach(variable -> {
            if (!variable.isExpired()) variables.add(variable);
        });
        return variables;
    }

    /**
     * @return every variable of a scope in memory, for every owner
     */
    public @NotNull List<Variable> getCachedScope(@NotNull VariableScope scope) {
        List<Variable> variables = new ArrayList<>();
        this.owners.forEach((key, cache) -> {
            if (key.scope() == scope) cache.values.values().forEach(variable -> {
                if (!variable.isExpired()) variables.add(variable);
            });
        });
        return variables;
    }

    /**
     * Reads an owner unless it is in memory. Two calls for the same owner share one read. Values
     * changed in memory before the read completes are kept, and additions still waiting to be
     * written are added to what was read.
     */
    @Override
    public @NotNull CompletableFuture<Collection<Variable>> load(@NotNull VariableScope scope, @NotNull String owner) {
        OwnerKey key = new OwnerKey(scope, owner);
        OwnerCache existing = this.owners.get(key);
        if (existing != null && existing.loaded) {
            existing.touch();
            return CompletableFuture.completedFuture(this.getCachedAll(scope, owner));
        }

        CompletableFuture<Collection<Variable>> created = new CompletableFuture<>();
        CompletableFuture<Collection<Variable>> running = this.loading.putIfAbsent(key, created);
        if (running != null) return running;

        this.store.loadOwner(scope, owner).whenComplete((variables, throwable) -> {
            this.loading.remove(key, created);
            if (throwable != null) {
                created.completeExceptionally(throwable);
                return;
            }
            OwnerCache cache = this.cache(scope, owner);
            synchronized (cache) {
                for (Variable variable : variables) {
                    this.names.get(scope).add(variable.ref().name());
                    VariableMutation waiting = this.pending.get(variable.ref());
                    if (waiting instanceof VariableMutation.Increment increment) {
                        cache.values.put(variable.ref().name(), Variable.ofNumber(variable.ref(), base(variable).add(increment.delta()), variable.expiresAt()));
                    } else if (waiting == null) {
                        cache.values.putIfAbsent(variable.ref().name(), variable);
                    }
                }
                cache.loaded = true;
            }
            cache.touch();
            created.complete(this.getCachedAll(scope, owner));
        });
        return created;
    }

    private static BigDecimal base(@Nullable Variable variable) {
        return variable == null || variable.number() == null ? BigDecimal.ZERO : variable.number();
    }

    private static long expiresAt(@Nullable Duration ttl) {
        return ttl == null || ttl.isZero() || ttl.isNegative() ? 0 : System.currentTimeMillis() + ttl.toMillis();
    }

    @Override
    public void set(@NotNull VariableRef ref, @NotNull String value, @Nullable Duration ttl) {
        this.set(ref, value, expiresAt(ttl));
    }

    @Override
    public void set(@NotNull VariableRef ref, @NotNull String value, long expiresAt) {
        Variable variable = Variable.of(ref, value, expiresAt);
        this.cache(ref.scope(), ref.owner()).values.put(ref.name(), variable);
        this.names.get(ref.scope()).add(ref.name());
        this.write(new VariableMutation.Set(variable));
    }

    @Override
    public @NotNull CompletableFuture<BigDecimal> increment(@NotNull VariableRef ref, @NotNull BigDecimal delta, @Nullable Duration ttl) {
        long expiresAtIfCreated = expiresAt(ttl);
        OwnerCache cache = this.cache(ref.scope(), ref.owner());
        Variable updated;
        synchronized (cache) {
            Variable current = cache.values.get(ref.name());
            if (current != null && current.isExpired()) current = null;
            updated = Variable.ofNumber(ref, base(current).add(delta), current == null ? expiresAtIfCreated : current.expiresAt());
            cache.values.put(ref.name(), updated);
        }
        this.names.get(ref.scope()).add(ref.name());

        VariableMutation.Increment increment = new VariableMutation.Increment(ref, delta, expiresAtIfCreated);
        if (ref.scope() == VariableScope.PLAYER) {
            synchronized (this.writeLock) {
                this.pending.merge(ref, increment, ZVariableService::merge);
            }
            if (!cache.loaded) this.load(ref.scope(), ref.owner());
            return CompletableFuture.completedFuture(updated.number());
        }

        CompletableFuture<BigDecimal> result;
        synchronized (this.writeLock) {
            result = this.store.incrementAndGet(ref, delta, expiresAtIfCreated);
        }
        return result.whenComplete((value, throwable) -> {
            if (throwable != null) {
                Logger.error("Unable to add to the variable " + ref.name(), throwable);
                return;
            }
            Variable stored = Variable.ofNumber(ref, value, cache.values.getOrDefault(ref.name(), updated).expiresAt());
            cache.values.put(ref.name(), stored);
            this.publishChanged(List.of(ref));
        });
    }

    @Override
    public void delete(@NotNull VariableRef ref) {
        OwnerCache cache = this.owners.get(new OwnerKey(ref.scope(), ref.owner()));
        if (cache != null) cache.values.remove(ref.name());
        this.write(new VariableMutation.Delete(ref));
    }

    /**
     * Combines a change with the one already waiting for the same variable.
     */
    static VariableMutation merge(VariableMutation waiting, VariableMutation next) {
        if (!(next instanceof VariableMutation.Increment increment)) return next;
        return switch (waiting) {
            case VariableMutation.Increment previous -> new VariableMutation.Increment(previous.ref(), previous.delta().add(increment.delta()), previous.expiresAtIfCreated());
            case VariableMutation.Set set -> new VariableMutation.Set(Variable.ofNumber(set.ref(), base(set.variable()).add(increment.delta()), set.variable().expiresAt()));
            case VariableMutation.Delete delete -> new VariableMutation.Set(Variable.ofNumber(delete.ref(), increment.delta(), increment.expiresAtIfCreated()));
        };
    }

    /**
     * Player changes wait for the next batch; server and global changes are written at once.
     */
    private void write(VariableMutation mutation) {
        synchronized (this.writeLock) {
            if (mutation.ref().scope() == VariableScope.PLAYER) {
                this.pending.merge(mutation.ref(), mutation, ZVariableService::merge);
                return;
            }
            this.applyAndPublish(List.of(mutation));
        }
    }

    /**
     * Hands the waiting player changes to the store.
     *
     * <p>Changes of a player whose variables are still being read are kept for a later batch: the
     * read adds them to the values it finds, which is only right if the database does not have them
     * yet. When zMenu stops, everything is written.</p>
     *
     * @param all whether to also write the changes of players not read yet, when zMenu stops
     * @return completes once they are stored
     */
    public @NotNull CompletableFuture<Void> flush(boolean all) {
        synchronized (this.writeLock) {
            List<VariableMutation> mutations = this.drain(ref -> all || this.isLoaded(ref.scope(), ref.owner()));
            return this.applyAndPublish(mutations);
        }
    }

    private List<VariableMutation> drain(Predicate<VariableRef> filter) {
        List<VariableMutation> drained = new ArrayList<>();
        for (Map.Entry<VariableRef, VariableMutation> entry : this.pending.entrySet()) {
            if (filter.test(entry.getKey()) && this.pending.remove(entry.getKey(), entry.getValue())) {
                drained.add(entry.getValue());
            }
        }
        return drained;
    }

    /**
     * @return whether a change of this owner waits for the next batch
     */
    public boolean hasPending(@NotNull VariableScope scope, @NotNull String owner) {
        for (VariableRef ref : this.pending.keySet()) {
            if (ref.scope() == scope && ref.owner().equals(owner)) return true;
        }
        return false;
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name) {
        this.owners.forEach((key, cache) -> {
            if (key.scope() == scope) cache.values.remove(name);
        });
        this.names.get(scope).remove(name);
        synchronized (this.writeLock) {
            this.drain(ref -> ref.scope() == scope && ref.name().equals(name));
            return this.logFailure(this.store.deleteName(scope, name), "delete the variable " + name)
                    .thenRun(() -> this.publishCleared(scope, new StorageEvent.NameCleared(this.serverId, scope, name)));
        }
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
        OwnerCache cache = this.owners.get(new OwnerKey(scope, owner));
        if (cache != null) cache.values.clear();
        synchronized (this.writeLock) {
            this.drain(ref -> ref.scope() == scope && ref.owner().equals(owner));
            return this.logFailure(this.store.deleteOwner(scope, owner), "delete the variables of " + owner)
                    .thenRun(() -> this.publishCleared(scope, new StorageEvent.OwnerCleared(this.serverId, scope, owner)));
        }
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
        this.owners.forEach((key, cache) -> {
            if (key.scope() == scope) cache.values.clear();
        });
        this.names.get(scope).clear();
        synchronized (this.writeLock) {
            this.drain(ref -> ref.scope() == scope);
            return this.logFailure(this.store.deleteScope(scope), "delete the " + scope.id() + " variables")
                    .thenRun(() -> this.publishCleared(scope, new StorageEvent.ScopeCleared(this.serverId, scope)));
        }
    }

    @Override
    public @NotNull Set<String> names(@NotNull VariableScope scope) {
        Set<String> found = new HashSet<>(this.names.get(scope));
        this.owners.forEach((key, cache) -> {
            if (key.scope() == scope) found.addAll(cache.values.keySet());
        });
        return found;
    }

    /**
     * Reads a player's variables before they join, so a menu never sees a player whose values are
     * missing. If they cannot be read the player is turned away, since adding to a value that was
     * not read would replace it.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        try {
            this.prepareLogin(event.getUniqueId());
            this.load(VariableScope.PLAYER, event.getUniqueId().toString()).get(PRE_LOGIN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
            Logger.error("Unable to load the player data of " + event.getName() + " (" + event.getUniqueId() + ")", exception);
            String message = Message.PLAYERS_DATA_LOAD_ERROR.getMessage();
            Component reason = this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater ? paperMetaUpdater.getComponent(message) : Component.text(message);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, reason);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        String owner = event.getPlayer().getUniqueId().toString();
        this.cache(VariableScope.PLAYER, owner).setOnline(true);
        this.load(VariableScope.PLAYER, owner);
    }

    /**
     * Keeps the variables of a player who leaves for {@link #OFFLINE_TTL_MILLIS}, so a player who
     * reconnects at once never reads values older than the ones still waiting to be written.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        OwnerCache cache = this.owners.get(new OwnerKey(VariableScope.PLAYER, playerId.toString()));
        if (cache != null) cache.setOnline(false);
        this.releasePlayer(playerId);
    }

    /**
     * Forgets offline players nobody looked up recently, once nothing of theirs waits to be
     * written.
     */
    void evictOfflinePlayers() {
        long limit = System.currentTimeMillis() - OFFLINE_TTL_MILLIS;
        this.owners.entrySet().removeIf(entry -> {
            OwnerKey key = entry.getKey();
            OwnerCache cache = entry.getValue();
            return key.scope() == VariableScope.PLAYER && !cache.online && cache.lastAccess < limit && !this.hasPending(key.scope(), key.owner());
        });
    }

    private void purgeExpired() {
        this.logFailure(this.store.deleteExpired(System.currentTimeMillis()), "delete the expired variables");
    }

    /**
     * @return how many owners have their variables in memory
     */
    public int getCachedOwners() {
        return this.owners.size();
    }

    private <T> CompletableFuture<T> logFailure(CompletableFuture<T> future, String action) {
        return future.whenComplete((result, throwable) -> {
            if (throwable != null) Logger.error("Unable to " + action, throwable);
        });
    }

    private record OwnerKey(VariableScope scope, String owner) {
    }

    private static final class OwnerCache {

        private final Map<String, Variable> values = new ConcurrentHashMap<>();
        private volatile boolean loaded;
        private volatile boolean online;
        private volatile long lastAccess = System.currentTimeMillis();

        private void touch() {
            this.lastAccess = System.currentTimeMillis();
        }

        private void setOnline(boolean online) {
            this.online = online;
            this.touch();
        }
    }
}
