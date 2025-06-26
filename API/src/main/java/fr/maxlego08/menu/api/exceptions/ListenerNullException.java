package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class ListenerNullException extends Error {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public ListenerNullException() {
        // TODO Auto-generated constructor stub
    }

    public ListenerNullException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ListenerNullException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    public ListenerNullException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ListenerNullException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

}
