package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class ItemFlagException extends Exception {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public ItemFlagException() {
    }

    public ItemFlagException(String message) {
        super(message);
    }

    public ItemFlagException(Throwable cause) {
        super(cause);
    }

    public ItemFlagException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemFlagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
