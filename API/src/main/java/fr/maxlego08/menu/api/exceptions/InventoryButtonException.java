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
        // TODO Auto-generated constructor stub
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
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InventoryButtonException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InventoryButtonException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InventoryButtonException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
