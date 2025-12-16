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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public class ItemJoinMechanicListener extends MechanicListener {
    private final ItemJoinMechanicFactory itemJoinMechanicFactory;
    private final ItemManager itemManager;

    public ItemJoinMechanicListener(ItemJoinMechanicFactory itemJoinMechanicFactory, MenuPlugin plugin) {
        this.itemJoinMechanicFactory = itemJoinMechanicFactory;
        this.itemManager = plugin.getItemManager();
    }

    /**
     * Gets the ItemJoinMechanic for the given item, if it exists and prevents inventory changes.
     *
     * @param item the item to check
     * @return Optional containing the mechanic if it prevents inventory changes, empty otherwise
     */
    private Optional<ItemJoinMechanic> getProtectedMechanic(ItemStack item) {
        if (item == null) return Optional.empty();

        Optional<String> itemId = itemManager.getItemId(item);
        if (itemId.isEmpty()) return Optional.empty();

        ItemJoinMechanic mechanic = itemJoinMechanicFactory.getMechanic(itemId.get());
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
        return getProtectedMechanic(item).isPresent();
    }

    @EventHandler
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            for (Map.Entry<String, ItemJoinMechanic> entry : itemJoinMechanicFactory.getAllMechanics()) {
                ItemJoinMechanic mechanic = entry.getValue();
                if (mechanic.shouldGrantOnFirstJoin()) {
                    this.itemManager.giveItem(player, entry.getKey());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (isProtectedItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.CRAFTING) {
            return;
        }

        Optional<ItemJoinMechanic> mechanic = getProtectedMechanic(event.getCurrentItem());
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
            Optional<ItemJoinMechanic> mechanic = getProtectedMechanic(draggedItem);
            if (mechanic.isPresent() && mechanic.get().getFixedSlot().isPresent() && event.getRawSlots().contains(mechanic.get().getFixedSlot().getAsInt())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        if (isProtectedItem(event.getOffHandItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame frame)) return;

        ItemStack frameItem = frame.getItem();
        ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

        if (frameItem.getType() == Material.AIR && itemInHand.getType() != Material.AIR) {
            if (isProtectedItem(itemInHand)) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onItemGive(Player player, ItemStack item, String itemId) {
        ItemJoinMechanic mechanic = itemJoinMechanicFactory.getMechanic(itemId);
        if (mechanic != null && mechanic.preventsInventoryChanges() && mechanic.getFixedSlot().isPresent()) {
            int slot = mechanic.getFixedSlot().getAsInt();
            player.getInventory().setItem(slot, item);
            return true;
        }
        return false;
    }
}
