package fr.maxlego08.menu.api.interfaces;

/**
 * @deprecated use {@link java.util.function.Function}{@code <T, String>}.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
@FunctionalInterface
public interface StringConsumer<T> {

    String accept(T t);

}
