package fr.maxlego08.menu.api.exceptions;

public class InventorySizeException extends InventoryException {

    /**
     *
     */
    private static final long serialVersionUID = 8685275095501686794L;

    /**
     *
     */
    public InventorySizeException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventorySizeException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InventorySizeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InventorySizeException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InventorySizeException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
