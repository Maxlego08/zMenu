package fr.maxlego08.menu.api.utils;

import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, V> cache;

    public SimpleCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public V get(K key, Loader<V> loader) {
        return cache.computeIfAbsent(key, k -> loader.load());
    }

    public interface Loader<V> {
        V load();
    }
}