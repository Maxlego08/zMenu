package fr.maxlego08.menu.players.inventory;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.storage.dto.InventoryDTO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ZInventoriesPlayer implements InventoriesPlayer {

    private final Map<UUID, InventoryPlayer> inventories = new HashMap<>();
    private final ZMenuPlugin plugin;
    private long lastSave;

    public ZInventoriesPlayer(ZMenuPlugin plugin) {
        this.plugin = plugin;
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

        this.plugin.getStorageManager().storeInventory(player.getUniqueId(), inventoryPlayer);
    }

    @Override
    public void giveInventory(Player player) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(player.getUniqueId());
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            inventoryPlayer.giveInventory(player);
            inventories.remove(player.getUniqueId());

            this.plugin.getStorageManager().removeInventory(player.getUniqueId());
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

    @Override
    public List<ItemStack> getInventory(UUID uniqueId) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(uniqueId);
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            List<ItemStack> itemStacks = inventoryPlayer.getItemStacks();
            return itemStacks;
        }
        return Collections.emptyList();
    }

    @Override
    public void clearInventorie(UUID uniqueId) {
        inventories.remove(uniqueId);
        this.plugin.getStorageManager().removeInventory(uniqueId);
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

    @Override
    public void loadInventories() {
        this.inventories.putAll(this.plugin.getStorageManager().loadInventories().stream().collect(Collectors.toMap(InventoryDTO::player_id, inventory -> {
            var inventoryPlayer = new ZInventoryPlayer();
            inventoryPlayer.getItems().putAll(Arrays.stream(inventory.inventory().split(";")).collect(Collectors.toMap(s -> Integer.parseInt(s.split(":")[0]), s -> s.split(":")[1])));
            return inventoryPlayer;
        })));
    }
}
