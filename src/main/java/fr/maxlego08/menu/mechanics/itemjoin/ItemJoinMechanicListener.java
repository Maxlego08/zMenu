package fr.maxlego08.menu.mechanics.itemjoin;

import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ItemJoinMechanicListener extends MechanicListener {
    private final ItemJoinMechanicFactory itemJoinMechanicFactory;
    private final ItemManager itemManager;
    private final MenuPlugin plugin;
    private final Map<UUID, Set<String>> visitedWorlds = new ConcurrentHashMap<>();

    public ItemJoinMechanicListener(ItemJoinMechanicFactory itemJoinMechanicFactory, MenuPlugin plugin) {
        this.itemJoinMechanicFactory = itemJoinMechanicFactory;
        this.itemManager = plugin.getItemManager();
        this.plugin = plugin;
    }

    /**
     * Gets the ItemJoinMechanic for the given item, if it exists and prevents inventory changes.
     *
     * @param item the item to check
     * @return Optional containing the mechanic if it prevents inventory changes, empty otherwise
     */
    private Optional<ItemJoinMechanic> getProtectedMechanic(ItemStack item) {
        if (item == null) return Optional.empty();

        Optional<String> itemId = this.itemManager.getItemId(item);
        if (itemId.isEmpty()) return Optional.empty();

        ItemJoinMechanic mechanic = this.itemJoinMechanicFactory.getMechanic(itemId.get());
        if (mechanic != null && mechanic.preventsInventoryChanges() && mechanic.getFixedSlot().isPresent()) {
            return Optional.of(mechanic);
        }
        return Optional.empty();
    }

    /**
     * Checks if the item should be protected from inventory changes.
     *
     * @param item the item to check
     * @return true if the item is protected, false otherwise
     */
    private boolean isProtectedItem(ItemStack item) {
        return this.getProtectedMechanic(item).isPresent();
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            for (Map.Entry<String, ItemJoinMechanic> entry : this.itemJoinMechanicFactory.getAllMechanics()) {
                ItemJoinMechanic mechanic = entry.getValue();
                if (mechanic.shouldGrantOnFirstJoin()) {
                    this.itemManager.giveItem(player, entry.getKey());
                }
            }
        }

        this.loadVisitedWorlds(player);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        this.grantFirstWorldJoinItems(event.getPlayer(), event.getPlayer().getWorld().getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.visitedWorlds.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Reads the worlds a player has already visited, off the main thread, then grants the items
     * of the world they joined in.
     *
     * <p>The read used to run on the main thread on every join and every world change. The worlds
     * are now read once per session and kept until the player leaves.</p>
     */
    private void loadVisitedWorlds(Player player) {
        if (!this.hasFirstWorldJoinMechanics()) return;

        UUID uniqueId = player.getUniqueId();
        this.plugin.getScheduler().runAsync(task -> {
            Set<String> worlds = ConcurrentHashMap.newKeySet();
            worlds.addAll(this.plugin.getStorageManager().getVisitedWorlds(uniqueId));

            this.plugin.getScheduler().runAtEntity(player, entityTask -> {
                if (!player.isOnline()) return;
                this.visitedWorlds.put(uniqueId, worlds);
                this.grantFirstWorldJoinItems(player, player.getWorld().getName());
            });
        });
    }

    private boolean hasFirstWorldJoinMechanics() {
        for (Map.Entry<String, ItemJoinMechanic> entry : this.itemJoinMechanicFactory.getAllMechanics()) {
            if (entry.getValue().hasFirstWorldJoinWorlds()) return true;
        }
        return false;
    }

    /**
     * Grants the items of a world the player enters for the first time. Does nothing until the
     * visited worlds have been read, the read grants the items of the current world itself.
     */
    private void grantFirstWorldJoinItems(Player player, String worldName) {
        Set<String> visited = this.visitedWorlds.get(player.getUniqueId());
        if (visited == null || visited.contains(worldName)) return;

        boolean granted = false;
        for (Map.Entry<String, ItemJoinMechanic> entry : this.itemJoinMechanicFactory.getAllMechanics()) {
            ItemJoinMechanic mechanic = entry.getValue();
            if (!mechanic.hasFirstWorldJoinWorlds()) continue;
            if (!mechanic.getFirstWorldJoinWorlds().contains(worldName)) continue;

            this.itemManager.giveItem(player, entry.getKey());
            granted = true;
        }

        if (granted) {
            visited.add(worldName);
            this.plugin.getStorageManager().markWorldVisited(player.getUniqueId(), worldName);
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (this.isProtectedItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }

        Optional<ItemJoinMechanic> mechanic = this.getProtectedMechanic(event.getCurrentItem());
        if (mechanic.isPresent() && mechanic.get().getFixedSlot().isPresent() && event.getSlot() == mechanic.get().getFixedSlot().getAsInt()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }

        for (ItemStack draggedItem : event.getNewItems().values()) {
            Optional<ItemJoinMechanic> mechanic = this.getProtectedMechanic(draggedItem);
            if (mechanic.isPresent() && mechanic.get().getFixedSlot().isPresent() && event.getRawSlots().contains(mechanic.get().getFixedSlot().getAsInt())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (this.isProtectedItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame)) return;

        ItemStack frameItem = frame.getItem();
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (frameItem.getType() == Material.AIR && itemInHand.getType() != Material.AIR) {
            if (this.isProtectedItem(itemInHand)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onItemGive(@NonNull Player player, @NonNull ItemStack item, @NonNull String itemId) {
        ItemJoinMechanic mechanic = this.itemJoinMechanicFactory.getMechanic(itemId);
        if (mechanic != null && mechanic.preventsInventoryChanges() && mechanic.getFixedSlot().isPresent()) {
            int slot = mechanic.getFixedSlot().getAsInt();
            player.getInventory().setItem(slot, item);
            return true;
        }
        return false;
    }
}
