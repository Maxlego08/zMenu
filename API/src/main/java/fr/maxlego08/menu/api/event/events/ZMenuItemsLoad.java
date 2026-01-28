package fr.maxlego08.menu.api.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Called when zMenu custom items are loaded or reloaded.
 * This event is fired after all items and their mechanics have been processed.
 */
public class ZMenuItemsLoad extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Set<String> loadedItemIds;
    private final boolean isReload;

    /**
     * Constructor for ZMenuItemsLoad event.
     *
     * @param loadedItemIds Set of all loaded item IDs
     * @param isReload true if this is a reload, false if initial load
     */
    public ZMenuItemsLoad(@NotNull Set<String> loadedItemIds, boolean isReload) {
        this.loadedItemIds = loadedItemIds;
        this.isReload = isReload;
    }

    /**
     * Gets the set of all loaded item IDs.
     *
     * @return Set of item IDs that were loaded
     */
    @NotNull
    public Set<String> getLoadedItemIds() {
        return loadedItemIds;
    }

    /**
     * Checks if this event was triggered by a reload.
     *
     * @return true if this is a reload, false if initial load
     */
    @Contract(pure = true)
    public boolean isReload() {
        return isReload;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
