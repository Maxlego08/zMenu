package fr.maxlego08.menu.api.interfaces;

@FunctionalInterface
public interface ReturnConsumer<T, G> {
    G accept(T var1);
}
