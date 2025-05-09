package fr.maxlego08.menu.zcore.utils.interfaces;

@FunctionalInterface
public interface ReturnConsumer<T, G> {
    G accept(T var1);
}
