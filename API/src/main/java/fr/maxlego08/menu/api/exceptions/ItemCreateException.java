package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

/**
 * @deprecated never thrown by zMenu.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
public class ItemCreateException extends Error {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public ItemCreateException() {
        super();
    }

    public ItemCreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ItemCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemCreateException(String message) {
        super(message);
    }

    public ItemCreateException(Throwable cause) {
        super(cause);
    }


}
