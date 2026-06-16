package fr.maxlego08.menu.api.utils;

/**
 * Generic tuple class storing two values of different types.
 * Useful for returning pairs from methods where a dedicated class is unnecessary.
 *
 * @param <T>    Type of the first element
 * @param <U>    Type of the second element
 * @param first  The first element in the tuple.
 * @param second The second element in the tuple.
 */
public record Tuples<T, U>(T first, U second) {
}
