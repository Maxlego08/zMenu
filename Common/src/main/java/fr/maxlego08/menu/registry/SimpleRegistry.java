package fr.maxlego08.menu.registry;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.RegistryType;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A registry filled while plugins start, then frozen. Reads follow aliases.
 *
 * <p>Registering is meant for the main thread during startup. After {@link #freeze()}, the
 * entries never change, so any thread may read them.</p>
 *
 * @param <T> the type of the entries
 */
public final class SimpleRegistry<T extends Keyed> implements KeyedRegistry<T>, WritableRegistry<T> {

    private final RegistryType<T> type;
    private final Map<Key, T> entries = new LinkedHashMap<>();
    private final Map<Key, String> owners = new HashMap<>();
    private final Map<Key, Key> aliases = new LinkedHashMap<>();
    private final Map<Key, String> aliasOwners = new HashMap<>();
    private final List<Consumer<Key>> listeners = new CopyOnWriteArrayList<>();
    private volatile List<T> valueList;
    private volatile boolean frozen;

    public SimpleRegistry(@NotNull RegistryType<T> type) {
        this.type = type;
    }

    @Override
    public @NotNull RegistryType<T> type() {
        return this.type;
    }

    /**
     * Calls {@code listener} with every key and alias registered from now on, once it is readable.
     *
     * @param listener the listener
     */
    public void addListener(@NotNull Consumer<Key> listener) {
        this.listeners.add(listener);
    }

    @Override
    public <V extends T> @NotNull V register(@NotNull String owner, @NotNull V value) {
        Key key = value.key();
        synchronized (this) {
            this.checkWritable(key);
            if (!this.type.valueType().isInstance(value)) {
                throw new RegistrationException(key.asString() + " is a " + value.getClass().getName() + ", but registry " + this.type + " holds " + this.type.valueType().getName());
            }
            this.checkFree(key, owner);

            this.entries.put(key, value);
            this.owners.put(key, owner);
            this.valueList = null;
        }
        this.notifyListeners(key);
        return value;
    }

    @Override
    public void alias(@NotNull String owner, @NotNull Key from, @NotNull Key to) {
        synchronized (this) {
            this.checkWritable(from);
            this.checkFree(from, owner);
            if (!this.entries.containsKey(to)) {
                throw new RegistrationException("Alias " + from.asString() + " points to " + to.asString() + ", which is not in registry " + this.type);
            }
            this.aliases.put(from, to);
            this.aliasOwners.put(from, owner);
        }
        this.notifyListeners(from);
    }

    private void notifyListeners(Key key) {
        for (Consumer<Key> listener : this.listeners) {
            listener.accept(key);
        }
    }

    @Override
    public synchronized boolean unregister(@NotNull Key key) {
        this.checkWritable(key);
        if (this.aliases.remove(key) != null) {
            this.aliasOwners.remove(key);
            return true;
        }
        if (this.entries.remove(key) == null) return false;
        this.owners.remove(key);
        this.aliases.values().removeIf(key::equals);
        this.aliasOwners.keySet().retainAll(this.aliases.keySet());
        this.valueList = null;
        return true;
    }

    @Override
    public synchronized int unregisterAll(@NotNull String owner) {
        if (this.frozen) {
            throw new RegistrationException("Registry " + this.type + " is frozen, the entries of " + owner + " cannot be removed");
        }
        int removed = 0;
        for (Key alias : List.copyOf(this.aliasOwners.keySet())) {
            if (owner.equals(this.aliasOwners.get(alias)) && this.unregister(alias)) removed++;
        }
        for (Key key : List.copyOf(this.owners.keySet())) {
            if (owner.equals(this.owners.get(key)) && this.unregister(key)) removed++;
        }
        return removed;
    }

    /**
     * @return a registry that is not frozen, holding the same entries, owners and aliases
     */
    public synchronized @NotNull SimpleRegistry<T> copy() {
        SimpleRegistry<T> copy = new SimpleRegistry<>(this.type);
        copy.entries.putAll(this.entries);
        copy.owners.putAll(this.owners);
        copy.aliases.putAll(this.aliases);
        copy.aliasOwners.putAll(this.aliasOwners);
        return copy;
    }

    private void checkWritable(Key key) {
        if (this.frozen) {
            throw new RegistrationException("Registry " + this.type + " is frozen, " + key.asString() + " cannot be registered. Register from onEnable, before the server has finished loading");
        }
    }

    private void checkFree(Key key, String owner) {
        String existing = this.owners.containsKey(key) ? this.owners.get(key) : this.aliasOwners.get(key);
        if (existing != null) {
            throw new RegistrationException(key.asString() + " is already registered in " + this.type + " by " + existing + ", " + owner + " cannot register it again");
        }
    }

    /**
     * Stops accepting entries.
     */
    public synchronized void freeze() {
        this.frozen = true;
        this.valueList = List.copyOf(this.entries.values());
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    @Override
    public synchronized @NotNull Optional<T> get(@NotNull Key id) {
        T value = this.entries.get(id);
        if (value == null) {
            Key target = this.aliases.get(id);
            value = target == null ? null : this.entries.get(target);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public synchronized boolean contains(@NotNull Key id) {
        return this.entries.containsKey(id) || this.aliases.containsKey(id);
    }

    @Override
    public synchronized boolean containsValue(@NotNull T value) {
        return this.entries.get(value.key()) == value;
    }

    @Override
    public synchronized @NotNull Optional<String> owner(@NotNull Key id) {
        String owner = this.owners.get(id);
        return Optional.ofNullable(owner != null ? owner : this.aliasOwners.get(id));
    }

    @Override
    public synchronized @NotNull Map<Key, Key> aliases() {
        return Map.copyOf(this.aliases);
    }

    @Override
    public synchronized @NotNull Set<Key> keys() {
        return Set.copyOf(this.entries.keySet());
    }

    @Override
    public @NotNull List<T> values() {
        List<T> list = this.valueList;
        if (list != null) return list;
        synchronized (this) {
            return this.valueList = List.copyOf(this.entries.values());
        }
    }
}
