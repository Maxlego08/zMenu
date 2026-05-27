package fr.maxlego08.menu.api.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Registry<T, R> {
    private final Map<T, R> registry = new HashMap<>();

    public void register(T key, R value) {
        registry.put(key, value);
    }

    public boolean contains(T key) {
        return registry.containsKey(key);
    }

    public void unregister(T key) {
        registry.remove(key);
    }

    public Optional<R> get(T key) {
        return Optional.ofNullable(registry.get(key));
    }
}
