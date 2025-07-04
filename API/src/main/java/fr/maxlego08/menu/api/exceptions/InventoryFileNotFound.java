package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryFileNotFound extends InventoryException {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5794114572465101305L;

    /**
     *
     */
    public InventoryFileNotFound() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventoryFileNotFound(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InventoryFileNotFound(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InventoryFileNotFound(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InventoryFileNotFound(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
