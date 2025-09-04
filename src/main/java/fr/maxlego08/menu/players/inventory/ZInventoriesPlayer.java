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
import java.util.function.BiConsumer;
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

        ZInventoryPlayer inventoryPlayer = new ZInventoryPlayer(this.plugin);
        inventoryPlayer.storeInventory(player);
        inventories.put(player.getUniqueId(), inventoryPlayer);

        this.plugin.getStorageManager().storeInventory(player.getUniqueId(), inventoryPlayer);
    }

    private void restoreInventory(Player player, BiConsumer<InventoryPlayer, Player> restoreAction) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(player.getUniqueId());
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            restoreAction.accept(inventoryPlayer, player);
            inventories.remove(player.getUniqueId());
            this.plugin.getStorageManager().removeInventory(player.getUniqueId());
        }
    }

    @Override
    public void giveInventory(Player player) {
        restoreInventory(player, InventoryPlayer::giveInventory);
    }

    @Override
    public void forceGiveInventory(Player player) {
        restoreInventory(player, InventoryPlayer::forceGiveInventory);
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
            return inventoryPlayer.getItemStacks();
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
            this.forceGiveInventory(player);
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
        this.inventories.putAll(this.plugin.getStorageManager().loadInventories().stream().collect(Collectors.toMap(
                InventoryDTO::player_id,
                inventory -> {
                    var inventoryPlayer = new ZInventoryPlayer(this.plugin);
                    Arrays.stream(inventory.inventory().split(";"))
                            .map(s -> s.split(":"))
                            .filter(parts -> parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty())
                            .forEach(parts -> {
                                try {
                                    int slot = Integer.parseInt(parts[0]);
                                    inventoryPlayer.getItems().put(slot, parts[1]);
                                } catch (NumberFormatException ignored) {
                                }
                            });
                    return inventoryPlayer;
                }
        )));
    }
}
