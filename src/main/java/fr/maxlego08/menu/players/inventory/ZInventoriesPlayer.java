package fr.maxlego08.menu.players.inventory;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ZInventoriesPlayer implements InventoriesPlayer {

    private static Map<UUID, ZInventoryPlayer> inventories = new HashMap<>();
    private transient final ZMenuPlugin plugin;
    private transient long lastSave;

    public ZInventoriesPlayer(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    public void autoSave() {
        if (System.currentTimeMillis() > this.lastSave) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                this.save();
                this.lastSave = System.currentTimeMillis() + (Config.secondsSavePlayerInventories * 1000L);
            });
        }
    }

    @Override
    public void storeInventory(Player player) {
        if (hasSavedInventory(player.getUniqueId())) {
            // Something is wrong
            // Logger.info("The plugin tries to save an inventory while the player already has an inventory saved!");
            return;
        }

        ZInventoryPlayer inventoryPlayer = new ZInventoryPlayer();
        inventoryPlayer.storeInventory(player);
        inventories.put(player.getUniqueId(), inventoryPlayer);

        if (Config.autoSaveFileInventoryOnUpdate) {
            autoSave();
        }
    }

    @Override
    public void giveInventory(Player player) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(player.getUniqueId());
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            inventoryPlayer.giveInventory(player);
            inventories.remove(player.getUniqueId());

            if (Config.autoSaveFileInventoryOnUpdate) {
                autoSave();
            }
        }
    }

    @Override
    public boolean hasSavedInventory(UUID uniqueId) {
        return inventories.containsKey(uniqueId);
    }

    @Override
    public Optional<InventoryPlayer> getPlayerInventory(UUID uniqueId) {
        return Optional.ofNullable(inventories.getOrDefault(uniqueId, null));
    }

    public void save() {
        this.plugin.getPersist().save(this, "players-inventory");
    }

    public void load() {
        this.plugin.getPersist().loadOrSaveDefault(this, ZInventoriesPlayer.class, "players-inventory");
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (hasSavedInventory(player.getUniqueId())) {
            this.giveInventory(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (hasSavedInventory(player.getUniqueId())) {
            this.giveInventory(player);
        }
    }
}
