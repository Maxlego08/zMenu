package fr.maxlego08.menu.players.inventory;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class ZInventoriesPlayer implements InventoriesPlayer {

    private static final long PRE_LOGIN_TIMEOUT_SECONDS = 10;

    private final Map<UUID, InventoryPlayer> inventories = new ConcurrentHashMap<>();
    private final ZMenuPlugin plugin;
    private long lastSave;

    public ZInventoriesPlayer(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void storeInventory(@NonNull Player player) {
        if (this.hasSavedInventory(player.getUniqueId())) {
            return;
        }

        ZInventoryPlayer inventoryPlayer = new ZInventoryPlayer(this.plugin);
        inventoryPlayer.storeInventory(player);
        this.inventories.put(player.getUniqueId(), inventoryPlayer);

        this.plugin.getStorageManager().storeInventory(player.getUniqueId(), inventoryPlayer);
    }

    @Override
    public void storeInventoryTemporary(@NonNull Player player) {
        if (this.hasSavedInventory(player.getUniqueId())) {
            return;
        }

        ZInventoryPlayer inventoryPlayer = new ZInventoryPlayer(this.plugin);
        inventoryPlayer.storeInventory(player, true);
        this.inventories.put(player.getUniqueId(), inventoryPlayer);
    }

    @Override
    public void storeInventoryTemporaryOrClear(@NotNull Player player) {
        Optional<InventoryPlayer> playerInventory = this.getPlayerInventory(player.getUniqueId());
        if (playerInventory.isPresent()) {
            playerInventory.get().clearInventory(player);
        } else {
            this.storeInventoryTemporary(player);
        }
    }

    private void restoreInventory(Player player, BiConsumer<InventoryPlayer, Player> restoreAction) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(player.getUniqueId());
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            restoreAction.accept(inventoryPlayer, player);
            this.inventories.remove(player.getUniqueId());
            this.plugin.getStorageManager().removeInventory(player.getUniqueId());
        }
    }

    @Override
    public void giveInventory(@NonNull Player player) {
        this.restoreInventory(player, InventoryPlayer::giveInventory);
    }

    @Override
    public void forceGiveInventory(@NonNull Player player) {
        this.restoreInventory(player, InventoryPlayer::forceGiveInventory);
    }

    @Override
    public boolean hasSavedInventory(@NonNull UUID uniqueId) {
        return this.inventories.containsKey(uniqueId);
    }

    @Override
    public @NonNull Optional<InventoryPlayer> getPlayerInventory(@NonNull UUID uniqueId) {
        return Optional.ofNullable(this.inventories.getOrDefault(uniqueId, null));
    }

    @Override
    public @NonNull List<ItemStack> getInventory(@NonNull UUID uniqueId) {
        Optional<InventoryPlayer> optional = this.getPlayerInventory(uniqueId);
        if (optional.isPresent()) {
            InventoryPlayer inventoryPlayer = optional.get();
            return inventoryPlayer.getItemStacks();
        }
        return Collections.emptyList();
    }

    @Override
    public void clearInventorie(@NonNull UUID uniqueId) {
        this.inventories.remove(uniqueId);
        this.plugin.getStorageManager().removeInventory(uniqueId);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Optional<InventoryPlayer> playerInventory = this.getPlayerInventory(player.getUniqueId());
        if (playerInventory.isPresent()) {
            if (playerInventory.get().isPermanent())
                this.forceGiveInventory(player);
            else
                this.clearInventorie(player.getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Optional<InventoryPlayer> playerInventory = this.getPlayerInventory(player.getUniqueId());
        if (playerInventory.isPresent()) {
            if (playerInventory.get().isPermanent())
                this.giveInventory(player);
        }
    }

    /**
     * Reads the saved inventories of the players already online, after a reload. Other players are
     * read just before they join.
     */
    @Override
    public void loadInventories() {
        ZStorageManager storageManager = (ZStorageManager) this.plugin.getStorageManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uniqueId = player.getUniqueId();
            storageManager.loadSavedInventory(uniqueId).whenComplete((inventory, throwable) -> {
                if (throwable != null) Logger.error("Unable to load the saved inventory of " + player.getName(), throwable);
                else inventory.ifPresent(encoded -> this.inventories.putIfAbsent(uniqueId, this.decode(encoded)));
            });
        }
    }

    /**
     * Reads the inventory saved for a player before they join, so it can be given back on join.
     * Saved inventories used to be read for every player at startup.
     *
     * <p>If it cannot be read the player is turned away: a menu clearing their inventory would
     * otherwise replace the saved copy, and the items in it would be lost.</p>
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

        UUID uniqueId = event.getUniqueId();
        try {
            ((ZStorageManager) this.plugin.getStorageManager()).loadSavedInventory(uniqueId).get(PRE_LOGIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .ifPresent(encoded -> this.inventories.putIfAbsent(uniqueId, this.decode(encoded)));
        } catch (Exception exception) {
            if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
            Logger.error("Unable to load the saved inventory of " + event.getName() + " (" + uniqueId + ")", exception);
            String message = Message.PLAYERS_DATA_LOAD_ERROR.getMessage();
            Component reason = this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater ? paperMetaUpdater.getComponent(message) : Component.text(message);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, reason);
        }
    }

    /**
     * Forgets the inventory read for a login another listener refused. It stays saved in the
     * database for the next login.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLoginResult(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED || Bukkit.getPlayer(event.getUniqueId()) != null) return;
        this.inventories.remove(event.getUniqueId());
    }

    private ZInventoryPlayer decode(String encoded) {
        ZInventoryPlayer inventoryPlayer = new ZInventoryPlayer(this.plugin);
        for (String serializedItem : encoded.split(";")) {
            String[] parts = serializedItem.split(":");
            if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
                try {
                    inventoryPlayer.getItems().put(Integer.parseInt(parts[0]), parts[1]);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return inventoryPlayer;
    }

    /**
     * Gives every online player their saved inventory back, on shutdown.
     *
     * <p>The saved copy is deleted from the database for each player who got it back. Keeping it
     * gave the same items a second time when the player next joined. Offline players keep their
     * saved copy, they get it back on join.</p>
     */
    @Override
    public void restoreAllInventories() {
        new HashMap<>(this.inventories).forEach((uuid, inventoryPlayer) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                inventoryPlayer.forceGiveInventory(player);
                this.plugin.getStorageManager().removeInventory(uuid);
            }
            this.inventories.remove(uuid);
        });
    }
}
