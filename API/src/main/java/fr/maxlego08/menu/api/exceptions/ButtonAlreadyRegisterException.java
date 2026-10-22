package fr.maxlego08.menu.api.exceptions;

import java.io.Serial;

public class ButtonAlreadyRegisterException extends Error {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2426306444018640211L;

    /**
     *
     */
    public ButtonAlreadyRegisterException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ButtonAlreadyRegisterException(String message, Throwable cause, boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public ButtonAlreadyRegisterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ButtonAlreadyRegisterException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ButtonAlreadyRegisterException(Throwable cause) {
        super(cause);
    }

}
