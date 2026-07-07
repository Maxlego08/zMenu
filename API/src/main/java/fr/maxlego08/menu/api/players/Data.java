package fr.maxlego08.menu.api.players;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>A data is composed of a key, a string, and a value, an object</p>
 */
public interface Data {

    /**
     * The key
     *
     * @return key
     */
    @NotNull
    String getKey();

    /**
     * The value that is stored
     *
     * @return value
     */
    @Nullable
    Object getValue();

    /**
     * Permet de savoir quand la valeur doit expirer. If the value is 0 then it never exits
     *
     * @return expired at
     */
    long getExpiredAt();

    /**
     * Determines if the data is expired.
     *
     * @return true if expired, false if not
     */
    boolean isExpired();

    /**
     * Adds the specified amount to the current value.
     *
     * @param amount the amount to add
     */
    void add(int amount);

    /**
     * Removes the specified amount from the current value.
     *
     * @param amount the amount to remove
     */
    void remove(int amount);

    /**
     * Negates the current value.
     */
    void negate();

}
