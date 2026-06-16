package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryOpenException extends Exception {


    @Serial
    private static final long serialVersionUID = 1L;

    public InventoryOpenException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InventoryOpenException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    public InventoryOpenException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public InventoryOpenException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public InventoryOpenException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
