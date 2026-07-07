package fr.maxlego08.menu.api.interfaces;

@FunctionalInterface
public interface StringConsumer<T> {

    String accept(T t);

}
