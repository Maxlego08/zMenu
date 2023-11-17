package fr.maxlego08.menu.zcore.utils;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache<K, V> {
    private final Map<K, V> cache;

    public SimpleCache() {
        this.cache = new HashMap<>();
    }

    public V get(K key, Loader<V> loader) {
        return cache.computeIfAbsent(key, k -> loader.load());
    }

    public interface Loader<V> {
        V load();
    }
}