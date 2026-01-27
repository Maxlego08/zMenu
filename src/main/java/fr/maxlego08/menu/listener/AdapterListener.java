package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AdapterListener extends ZUtils implements Listener {

    private final ZMenuPlugin plugin;

    public AdapterListener(ZMenuPlugin template) {
        this.plugin = template;
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onConnect(event, event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onQuit(event, event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onInventoryClick(event, (Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onInventoryDrag(event, (Player) event.getWhoClicked());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onInventoryClose(event, (Player) event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onPickUp(event, player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onDeath(event, player);
        }
    }
}
