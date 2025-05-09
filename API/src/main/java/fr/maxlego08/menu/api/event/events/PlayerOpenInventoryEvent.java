package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.CancelledMenuEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerOpenInventoryEvent extends CancelledMenuEvent {

    private final Player player;
    private final Inventory inventory;
    private int page;
    private List<Inventory> oldInventories;

    public PlayerOpenInventoryEvent(Player player, Inventory inventory, int page, List<Inventory> oldInventories) {
        this.player = player;
        this.inventory = inventory;
        this.page = page;
        this.oldInventories = oldInventories;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Inventory> getOldInventories() {
        return oldInventories;
    }

    public void setOldInventories(List<Inventory> oldInventories) {
        this.oldInventories = oldInventories;
    }
}
