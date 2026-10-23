package fr.maxlego08.menu.api.interfaces;

/**
 * @deprecated use {@link java.util.function.Function}.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
@FunctionalInterface
public interface ReturnConsumer<T, G> {
    G accept(T var1);
}
