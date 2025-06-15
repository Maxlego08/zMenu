package fr.maxlego08.menu.api.event;

import fr.maxlego08.menu.api.event.events.ButtonLoadEvent;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;

/**
 * Allows you to listen to the events of the plugin without using the Bukkit API, you gain performance
 */
public abstract class FastEvent {

    /**
     * Event when a button will be loaded
     *
     * @param event {@link ButtonLoadEvent}
     */
    public void onButtonLoad(ButtonLoadEvent event){

    }

    /**
     * Event when an inventory will be loaded
     *
     * @param event {@link InventoryLoadEvent}
     */
    public void onInventoryLoad(InventoryLoadEvent event){

    }

    /**
     * Event when a player open an inventory
     *
     * @param event {@link PlayerOpenInventoryEvent}
     */
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event){

    }
}
