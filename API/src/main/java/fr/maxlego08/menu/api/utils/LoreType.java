package fr.maxlego08.menu.api.utils;

/**
 * Enum specifying the strategy for modifying item lore text.
 */
public enum LoreType {

    /**
     * Replace the existing lore entirely.
     */
    REPLACE,

    /**
     * Append additional lines to the existing lore.
     */
    APPEND,

    /**
     * Add lines before the current lore.
     */
    PREPEND

}
