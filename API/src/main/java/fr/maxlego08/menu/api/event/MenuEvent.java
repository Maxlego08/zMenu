package fr.maxlego08.menu.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MenuEvent extends Event {

    private final static HandlerList handlers = new HandlerList();

    /**
     *
     */
    public MenuEvent() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param isAsync
     */
    public MenuEvent(boolean isAsync) {
        super(isAsync);
        // TODO Auto-generated constructor stub
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * @return the handlers
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

}
