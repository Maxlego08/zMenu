package fr.maxlego08.menu.api.event;

import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;

/**
 * Allows you to listen to the events of the plugin without using the Bukkit API, you gain performance
 */
public interface FastEvent {

    /**
     * Event when a button will be loaded
     *
     * @param event {@link ButtonLoadEvent}
     */
    void onButtonLoad(ButtonLoadEvent event);

    /**
     * Event when an inventory will be loaded
     *
     * @param event {@link InventoryLoadEvent}
     */
    void onInventoryLoad(InventoryLoadEvent event);

    /**
     * Event when a player open an inventory
     *
     * @param event {@link PlayerOpenInventoryEvent}
     */
    void onPlayerOpenInventory(PlayerOpenInventoryEvent event);
}
