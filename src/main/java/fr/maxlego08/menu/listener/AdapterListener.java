package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@AutoListener
public class AdapterListener extends ZUtils implements Listener {

    private final ZMenuPlugin plugin;

    public AdapterListener(MenuPlugin plugin) {
        this.plugin = (ZMenuPlugin) plugin;
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
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onInventoryOpen(event, player);
            }
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

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (event.getView().getPlayer() instanceof Player player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onPrepareAnvil(event, player);
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onMove(event, event.getPlayer());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
            adapter.onTeleport(event, event.getPlayer());
        }
    }

    /**
     * Handles both damage taken and damage dealt. EntityDamageByEntityEvent extends EntityDamageEvent,
     * so registering this single handler covers both, the damager is resolved below.
     */
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                adapter.onDamage(event, player);
            }
        }

        if (event instanceof EntityDamageByEntityEvent damageByEntityEvent) {
            Entity damager = damageByEntityEvent.getDamager();
            if (damager instanceof Player attacker) {
                for (ListenerAdapter adapter : this.plugin.getListenerAdapters()) {
                    adapter.onDamage(event, attacker);
                }
            }
        }
    }
}
