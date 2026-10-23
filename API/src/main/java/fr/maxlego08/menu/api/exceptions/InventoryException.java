package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryException extends Exception {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2498115281127927055L;

    public InventoryException() {
    }

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(Throwable cause) {
        super(cause);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
