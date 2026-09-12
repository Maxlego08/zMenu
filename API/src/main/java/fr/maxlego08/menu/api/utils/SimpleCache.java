package fr.maxlego08.menu.api.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.maxlego08.menu.api.configuration.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * A small bounded cache.
 *
 * <p>This used to be a raw {@link java.util.concurrent.ConcurrentHashMap} with no size limit, no
 * expiry and no eviction. That is fine for a fixed set of keys, but the component cache is keyed by
 * text that has already had its placeholders resolved, so a single lore line containing a player
 * placeholder produces one entry per player and keeps it for the whole uptime. On a server with
 * many unique players that grows without bound until the heap is exhausted.</p>
 *
 * <p>The bounds come from the configuration by default, so they can be tuned or turned off without
 * a code change. A size or expiry of {@code 0} disables that particular bound.</p>
 */
public class SimpleCache<K, V> {

    /**
     * When true, the bounds are read from {@link Configuration} every time the cache is built,
     * so a change to {@code component-cache} in the config applies on reload. When false, the
     * values passed to the constructor are used and the configuration is ignored.
     */
    private final boolean useConfiguredBounds;

    private final int maxSize;
    private final long expireMinutes;

    /**
     * Built lazily so the configuration is read at first use rather than at class initialisation,
     * and rebuilt by {@link #clear()} so a reload picks up new bounds.
     */
    private volatile Cache<K, V> cache;

    /**
     * Creates a cache bounded by the {@code component-cache} values in the configuration.
     *
     * <p>This is the constructor to use inside the plugin, so the bounds stay tunable without a
     * code change.</p>
     */
    public SimpleCache() {
        this.useConfiguredBounds = true;
        this.maxSize = 0;
        this.expireMinutes = 0;
    }

    /**
     * Creates a cache with fixed bounds, ignoring the configuration.
     *
     * @param maxSize       Maximum number of entries, or 0 for no limit.
     * @param expireMinutes Drop entries unused for this many minutes, or 0 to never expire.
     */
    public SimpleCache(int maxSize, long expireMinutes) {
        this.useConfiguredBounds = false;
        this.maxSize = maxSize;
        this.expireMinutes = expireMinutes;
    }

    private Cache<K, V> delegate() {
        Cache<K, V> current = this.cache;
        if (current != null) {
            return current;
        }

        synchronized (this) {
            if (this.cache == null) {
                this.cache = this.build();
            }
            return this.cache;
        }
    }

    private Cache<K, V> build() {
        int size = this.useConfiguredBounds ? Configuration.componentCacheMaxSize : this.maxSize;
        long expire = this.useConfiguredBounds ? Configuration.componentCacheExpireMinutes : this.expireMinutes;

        CacheBuilder<Object, Object> builder = CacheBuilder.newBuilder();
        if (size > 0) {
            builder.maximumSize(size);
        }
        if (expire > 0) {
            builder.expireAfterAccess(expire, TimeUnit.MINUTES);
        }
        return builder.build();
    }

    /**
     * Returns the cached value for a key, loading and storing it when absent.
     *
     * <p>Implemented as a lookup followed by a load and a put rather than through
     * {@link Cache#get(Object, java.util.concurrent.Callable)}, for two reasons. A Guava cache
     * cannot hold null, and some loaders legitimately return null to mean "there is nothing to
     * cache here", which the callable form would turn into an exception. It also keeps the loader
     * outside any cache lock.</p>
     *
     * <p>The trade off is that two threads asking for the same missing key can both run the loader.
     * Every loader used here is a pure transformation, so the only cost is doing the work twice.</p>
     *
     * @param key    The key.
     * @param loader Produces the value when it is not cached. May return null.
     * @return The cached or freshly loaded value, possibly null.
     */
    public V get(K key, Loader<V> loader) {
        Cache<K, V> delegate = this.delegate();

        V value = delegate.getIfPresent(key);
        if (value != null) {
            return value;
        }

        value = loader.load();
        if (value != null) {
            delegate.put(key, value);
        }
        return value;
    }

    /**
     * Empties the cache. The next access rebuilds it, so bounds changed in the configuration take
     * effect after a reload.
     */
    public void clear() {
        synchronized (this) {
            Cache<K, V> current = this.cache;
            if (current != null) {
                current.invalidateAll();
            }
            this.cache = null;
        }
    }

    /**
     * @return The number of entries currently cached.
     */
    public long size() {
        Cache<K, V> current = this.cache;
        return current == null ? 0L : current.size();
    }

    public interface Loader<V> {
        V load();
    }
}
