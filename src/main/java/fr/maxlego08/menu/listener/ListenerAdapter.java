package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class ListenerAdapter extends ZUtils {

    protected void onConnect(PlayerJoinEvent event, Player player) {
    }

    protected void onQuit(PlayerQuitEvent event, Player player) {
    }

    protected void onInventoryClick(InventoryClickEvent event, Player player) {
    }

    protected void onInventoryClose(InventoryCloseEvent event, Player player) {
    }

    protected void onInventoryDrag(InventoryDragEvent event, Player player) {
    }

    public void onPickUp(EntityPickupItemEvent event, Player player) {
    }

    protected void onDeath(PlayerDeathEvent event, Player player) {
    }
}
