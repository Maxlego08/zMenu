/**
 *
 */
package fr.maxlego08.menu.api.exceptions;

/**
 * Represents an exception thrown when there is an error related to item enchanting.
 */
public class ItemEnchantException extends Exception {
    /**
     * {@inheritDoc}
     */
    public ItemEnchantException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public ItemEnchantException(String message) {
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public ItemEnchantException(Throwable cause) {
        super(cause);
    }

    /**
     * {@inheritDoc}
     */
    public ItemEnchantException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public ItemEnchantException(String message, Throwable cause, boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
