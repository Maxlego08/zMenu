package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.api.ItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

public class ItemUpdaterListener extends ListenerAdapter{
    private final ItemManager itemManager;

    public ItemUpdaterListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    @Override
    protected void onConnect(PlayerJoinEvent event, Player player) {
        itemManager.executeCheckInventoryItems(player);
    }

}
