package fr.maxlego08.menu.api.loader;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClassRegistry<T, P extends Plugin> {

    private final List<ConstructorStrategy<P>> strategies = new ArrayList<>();
    private final Consumer<T> registrar;
    private final Class<T> expectedType;

    private ClassRegistry(Class<T> expectedType, Consumer<T> registrar) {
        this.expectedType = expectedType;
        this.registrar = registrar;
    }

    public static <T, P extends Plugin> ClassRegistry<T, P> of(Class<T> type, Consumer<T> registrar) {
        return new ClassRegistry<>(type, registrar);
    }

    public ClassRegistry<T, P> tryConstructor(ConstructorStrategy<P> strategy) {
        this.strategies.add(strategy);
        return this;
    }

    public ClassRegistry<T, P> tryNoArgsConstructor() {
        return tryConstructor((clazz, plugin) -> clazz.getDeclaredConstructor().newInstance());
    }

    public boolean load(P plugin, Class<?> clazz) {
        for (ConstructorStrategy<P> strategy : strategies) {
            try {
                Object instance = strategy.instantiate(clazz, plugin);
                if (expectedType.isInstance(instance)) {
                    registrar.accept(expectedType.cast(instance));
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public Class<T> getExpectedType() {
        return expectedType;
    }
}