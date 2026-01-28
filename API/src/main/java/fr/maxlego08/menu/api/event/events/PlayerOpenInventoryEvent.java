package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.CancelledMenuEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerOpenInventoryEvent extends CancelledMenuEvent {

    private final Player player;
    private final Inventory inventory;
    private int page;
    private List<Inventory> oldInventories;

    public PlayerOpenInventoryEvent(@NotNull Player player,@NotNull Inventory inventory, int page,@NotNull List<Inventory> oldInventories) {
        this.player = player;
        this.inventory = inventory;
        this.page = page;
        this.oldInventories = oldInventories;
    }

    @Contract(pure = true)
    @NotNull
    public Player getPlayer() {
        return player;
    }

    @Contract(pure = true)
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    @Contract(pure = true)
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Contract(pure = true)
    @NotNull
    public List<Inventory> getOldInventories() {
        return oldInventories;
    }

    public void setOldInventories(@NotNull List<Inventory> oldInventories) {
        this.oldInventories = oldInventories;
    }
}
