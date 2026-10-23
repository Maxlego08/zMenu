package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class InventoryAlreadyExistException extends Error {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5611455794293458580L;

    public InventoryAlreadyExistException() {
        super();
    }

    public InventoryAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InventoryAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryAlreadyExistException(String message) {
        super(message);
    }

    public InventoryAlreadyExistException(Throwable cause) {
        super(cause);
    }

}
