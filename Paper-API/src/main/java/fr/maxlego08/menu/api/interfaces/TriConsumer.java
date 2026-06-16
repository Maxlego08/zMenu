package fr.maxlego08.menu.api.interfaces;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}