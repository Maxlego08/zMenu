package fr.maxlego08.menu.api.engine;

/**
 * Why an inventory is being opened.
 *
 * <p>Passed to the engine among the opening arguments, so that it can tell a genuine open from a
 * menu redrawing itself. Anything that re-opens the inventory a player is already looking at, such
 * as a page change or a {@code refresh inventory} action, is a {@link #REFRESH}.</p>
 */
public enum OpenCause {

    /**
     * The player is opening the inventory, from closed or from another menu.
     */
    OPEN,

    /**
     * The inventory the player is already looking at is being redrawn.
     */
    REFRESH
}
