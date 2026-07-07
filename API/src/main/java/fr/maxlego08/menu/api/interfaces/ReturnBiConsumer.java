package fr.maxlego08.menu.api.interfaces;

@FunctionalInterface
public interface ReturnBiConsumer<T, G, C> {

    C accept(T t, G g);

}
