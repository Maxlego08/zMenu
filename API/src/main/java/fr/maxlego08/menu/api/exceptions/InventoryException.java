package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryException extends Exception {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2498115281127927055L;

    public InventoryException() {
        // TODO Auto-generated constructor stub
    }

    public InventoryException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public InventoryException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public InventoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
