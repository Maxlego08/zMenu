package fr.maxlego08.menu.api.utils;

public enum TextChangeType {
    /**
     * One char added
     */
    ADDED,
    /**
     * One char deleted
     */
    REMOVED,
    /**
     * Empty field
     */
    CLEARED,
    /**
     * Multiple chars changed at once
     */
    REPLACED,
    /**
     * No change
     */
    EQUAL
}
