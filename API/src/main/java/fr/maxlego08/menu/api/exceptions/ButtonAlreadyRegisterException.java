package fr.maxlego08.menu.api.exceptions;

public class ButtonAlreadyRegisterException extends Error {

    /**
     *
     */
    private static final long serialVersionUID = -2426306444018640211L;

    /**
     *
     */
    public ButtonAlreadyRegisterException() {
        super();
        // TODO Auto-generated constructor stub
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
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public ButtonAlreadyRegisterException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ButtonAlreadyRegisterException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public ButtonAlreadyRegisterException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
