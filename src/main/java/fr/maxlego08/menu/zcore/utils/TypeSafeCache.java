package fr.maxlego08.menu.zcore.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A cache of objects grouped by their type, used as a write buffer for the storage layer.
 *
 * <p>Entries are added from the main server thread and drained by the asynchronous storage batch
 * task, so every operation is guarded by a single lock. Guarding the map alone is not enough: an
 * earlier version used a concurrent map of plain lists, which left two holes. The lists themselves
 * were unguarded, so draining one while the main thread appended to it could throw
 * {@link java.util.ConcurrentModificationException}. Worse, a writer that had already looked its
 * list up could append to it after the drain had taken it out of the map, and that entry was then
 * silently never written to the database.</p>
 *
 * <p>The lock is only ever held for a list append, a copy or a map lookup, and the drain happens
 * once per batch interval, so contention is not a concern.</p>
 */
public class TypeSafeCache {

    private final Map<Class<?>, List<Object>> cache = new HashMap<>();
    private final Object lock = new Object();

    /**
     * Adds an object to the cache.
     */
    public void add(Object object) {
        synchronized (this.lock) {
            this.cache.computeIfAbsent(object.getClass(), k -> new ArrayList<>()).add(object);
        }
    }

    /**
     * Returns a snapshot of every object of a given type.
     *
     * <p>This is a copy, so mutating it does not affect the cache. Use
     * {@link #replaceMatching(Class, Predicate, Object)} to change what is buffered.</p>
     */
    @SuppressWarnings("unchecked")
    public @NotNull <T> List<T> get(Class<T> type) {
        synchronized (this.lock) {
            List<Object> list = this.cache.get(type);
            return list == null ? new ArrayList<>() : new ArrayList<>((List<T>) list);
        }
    }

    /**
     * Atomically removes every object of a type and returns them.
     *
     * <p>This is what a consumer that intends to persist and then discard the entries should use.
     * It replaces the previous "read the list, then clear it" pair, where anything added between
     * the two steps was thrown away without ever being consumed.</p>
     *
     * @param type The type to drain.
     * @return The objects that were cached, or an empty list.
     */
    @SuppressWarnings("unchecked")
    public @NotNull <T> List<T> drain(Class<T> type) {
        synchronized (this.lock) {
            List<Object> removed = this.cache.remove(type);
            return removed == null ? new ArrayList<>() : (List<T>) removed;
        }
    }

    /**
     * Atomically drops the buffered objects matching a filter and adds a replacement.
     *
     * <p>Exists so a caller can supersede a pending record without a read-modify-write across two
     * separate calls, which a concurrent drain could slice in half.</p>
     *
     * @param type     The type being buffered.
     * @param filter   Matches the entries to drop.
     * @param newValue The replacement to add.
     */
    @SuppressWarnings("unchecked")
    public <T> void replaceMatching(Class<T> type, Predicate<T> filter, T newValue) {
        synchronized (this.lock) {
            List<Object> list = this.cache.computeIfAbsent(type, k -> new ArrayList<>());
            list.removeIf(object -> filter.test((T) object));
            list.add(newValue);
        }
    }

    /**
     * Drops the buffered objects matching a filter.
     */
    @SuppressWarnings("unchecked")
    public <T> void removeMatching(Class<T> type, Predicate<T> filter) {
        synchronized (this.lock) {
            List<Object> list = this.cache.get(type);
            if (list != null) {
                list.removeIf(object -> filter.test((T) object));
            }
        }
    }

    /**
     * Removes every object of a given type.
     */
    public void clear(Class<?> type) {
        synchronized (this.lock) {
            this.cache.remove(type);
        }
    }

    /**
     * Removes everything.
     */
    public void clearAll() {
        synchronized (this.lock) {
            this.cache.clear();
        }
    }

    /**
     * Removes one specific object.
     */
    public boolean remove(Object object) {
        synchronized (this.lock) {
            List<Object> list = this.cache.get(object.getClass());
            return list != null && list.remove(object);
        }
    }
}
