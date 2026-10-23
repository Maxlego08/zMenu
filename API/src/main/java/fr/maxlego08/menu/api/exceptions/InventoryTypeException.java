package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryTypeException extends InventoryException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InventoryTypeException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventoryTypeException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public InventoryTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public InventoryTypeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InventoryTypeException(Throwable cause) {
        super(cause);
    }

}
