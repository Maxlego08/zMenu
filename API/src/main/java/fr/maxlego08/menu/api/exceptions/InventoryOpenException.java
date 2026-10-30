package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryOpenException extends Exception {


    @Serial
    private static final long serialVersionUID = 1L;

    public InventoryOpenException() {
        super();
    }

    public InventoryOpenException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InventoryOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryOpenException(String message) {
        super(message);
    }

    public InventoryOpenException(Throwable cause) {
        super(cause);
    }

}
