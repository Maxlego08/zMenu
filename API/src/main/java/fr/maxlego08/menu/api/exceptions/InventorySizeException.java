package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventorySizeException extends InventoryException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8685275095501686794L;

    /**
     *
     */
    public InventorySizeException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventorySizeException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public InventorySizeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public InventorySizeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InventorySizeException(Throwable cause) {
        super(cause);
    }

}
