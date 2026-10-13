package fr.maxlego08.menu.api.loader;

import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ClassRegistry<T, P extends Plugin> {

    private final List<ConstructorStrategy<P>> strategies = new ArrayList<>();
    private final Consumer<T> registrar;
    private final Class<T> expectedType;
    private Consumer<String> errorLogger;

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

    public ClassRegistry<T, P> errorLogger(Consumer<String> errorLogger) {
        this.errorLogger = errorLogger;
        return this;
    }

    public ClassRegistry<T, P> tryNoArgsConstructor() {
        return this.tryConstructor((clazz, plugin) -> clazz.getDeclaredConstructor().newInstance());
    }

    public boolean load(P plugin, Class<?> clazz) {

        Throwable failure = null;

        for (ConstructorStrategy<P> strategy : this.strategies) {
            try {
                Object instance = strategy.instantiate(clazz, plugin);
                if (this.expectedType.isInstance(instance)) {
                    this.registrar.accept(this.expectedType.cast(instance));
                    return true;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (InvocationTargetException exception) {
                if (failure == null) failure = exception.getCause() == null ? exception : exception.getCause();
            } catch (Throwable throwable) {
                if (failure == null) failure = throwable;
            }
        }

        if (this.errorLogger != null) {
            if (failure != null) {
                this.errorLogger.accept("Could not load " + clazz.getName() + ", its constructor threw " + failure + ". This usually means the plugin it hooks into is missing or failed to start.");
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Could not find a valid constructor for ").append(clazz.getName()).append(". Available constructors: ");
                for (int i = 0; i < clazz.getDeclaredConstructors().length; i++) {
                    stringBuilder.append(clazz.getDeclaredConstructors()[i]);
                    if (i < clazz.getDeclaredConstructors().length - 1) {
                        stringBuilder.append(", ");
                    }
                }
                this.errorLogger.accept(stringBuilder.toString());
            }
        }

        return false;
    }

    public Class<T> getExpectedType() {
        return this.expectedType;
    }
}