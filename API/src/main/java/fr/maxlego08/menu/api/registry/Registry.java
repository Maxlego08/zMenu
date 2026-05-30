package fr.maxlego08.menu.api.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class Registry<T, R> {
    protected final Map<T, R> registry = new HashMap<>();

    public void register(@Nullable T key, @NotNull R value) {
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

    public Set<T> getAllKeys() {
        return registry.keySet();
    }
}
