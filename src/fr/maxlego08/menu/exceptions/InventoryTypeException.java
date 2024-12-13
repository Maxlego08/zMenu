package fr.maxlego08.menu.exceptions;

public class InventoryTypeException extends InventoryException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InventoryTypeException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public InventoryTypeException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InventoryTypeException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InventoryTypeException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InventoryTypeException(Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
