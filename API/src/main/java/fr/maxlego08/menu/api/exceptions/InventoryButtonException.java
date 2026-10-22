package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryButtonException extends InventoryException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8733148642557198564L;

    /**
     *
     */
    public InventoryButtonException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventoryButtonException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public InventoryButtonException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public InventoryButtonException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InventoryButtonException(Throwable cause) {
        super(cause);
    }

}
