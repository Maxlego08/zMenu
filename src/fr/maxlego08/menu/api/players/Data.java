package fr.maxlego08.menu.api.players;

/**
 * <p>A data is composed of a key, a string, and a value, an object</p>
 */
public interface Data {

    /**
     * The key
     *
     * @return key
     */
    String getKey();

    /**
     * The value that is stored
     *
     * @return value
     */
    Object getValue();

    /**
     * Permet de savoir quand la valeur doit expirer. If the value is 0 then it never exits
     *
     * @return expired at
     */
    long getExpiredAt();

    /**
     * Allows to know if a data is expired
     *
     * @return boolean
     */
    boolean isExpired();

}
