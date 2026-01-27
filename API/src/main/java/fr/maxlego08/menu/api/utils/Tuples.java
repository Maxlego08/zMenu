package fr.maxlego08.menu.api.utils;

/**
 * Generic tuple class storing two values of different types.
 * Useful for returning pairs from methods where a dedicated class is unnecessary.
 *
 * @param <T> Type of the first element
 * @param <U> Type of the second element
 */
public class Tuples<T,U> {
    /**
     * The first element in the tuple.
     */
    public final T first;
    /**
     * The second element in the tuple.
     */
    public final U second;

    public Tuples(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public U getSecond() {
        return this.second;
    }
}
