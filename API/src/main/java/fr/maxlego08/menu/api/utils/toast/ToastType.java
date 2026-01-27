package fr.maxlego08.menu.api.utils.toast;

/**
 * Represents types of Toast notifications, typically used to indicate progress,
 * success, or objectives completed in the UI.
 */
public enum ToastType {
    /**
     * Indicates a standard task notification.
     */
    TASK,

    /**
     * Represents an achievement or goal reached.
     */
    GOAL,

    /**
     * Shows up on more challenging or milestone events.
     */
    CHALLENGE;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}