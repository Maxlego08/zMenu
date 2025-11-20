package fr.maxlego08.menu.button.buttons;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.ItemDragButton;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZItemDragButton extends ItemDragButton {

    private final DupeManager dupeManager;
    //Check item section
    private boolean enableCheckItem = false;
    private MenuItemStack checkItems;
    private ItemStackSimilar itemStackSimilar;

    //Error item section
    private boolean enableErrorItem = false;
    private MenuItemStack errorItems;
    private final Map<UUID, Map<Integer, Boolean>> activeTasks = new HashMap<>();
    private int ticks;
    private PlatformScheduler scheduler;

    public ZItemDragButton(DupeManager dupeManager) {
        super();
        this.dupeManager = dupeManager;
    }

    public void setCheckItem(MenuItemStack menuItemStack, ItemStackSimilar itemStackSimilar) {
        this.enableCheckItem = true;
        this.checkItems = menuItemStack;
        this.itemStackSimilar = itemStackSimilar;
    }

    public void setErrorItem(MenuItemStack menuItemStack, PlatformScheduler scheduler, int ticks) {
        this.enableErrorItem = true;
        this.errorItems = menuItemStack;
        this.scheduler = scheduler;
        this.ticks = ticks;
    }

    @Override
    public boolean isRefreshOnDrag(){
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public boolean hasCustomRender() {
        return true;
    }

    @Override
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        if (inventoryEngine.getPage() == this.getPage() || this.isPermanent()) {

            int inventorySize = this.isPlayerInventory() ? 36 : inventoryEngine.getInventory().getSize();

            int[] slots = this.getSlots().stream()
                    .map(slot -> this.isPermanent() ? slot : slot - ((this.getPage() - 1) * inventorySize))
                    .filter(slot -> {
                        ItemStack item = inventoryEngine.getInventory().getItem(slot);
                        return item == null || item.getType() == Material.AIR || this.dupeManager.isDupeItem(item);
                    })
                    .mapToInt(Integer::intValue)
                    .toArray();
            inventoryEngine.displayFinalButton(this, slots);
        }
    }

    @Override
    public void onInventoryClose(Player player, InventoryEngine inventory) {
        this.slots.forEach(slot -> {
            ItemStack itemStack = inventory.getInventory().getItem(slot);

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }

            if (this.dupeManager.isDupeItem(itemStack)) {
                return;
            }

            Map<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
            if (!leftovers.isEmpty()) {
                leftovers.values().forEach(leftover -> {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                });
            }
        });
    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventoryEngine, int slot, Placeholders placeholders) {
        if (event == null) return;

        ItemStack currentItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (!this.dupeManager.isDupeItem(currentItem)){
            event.setCancelled(false);

            if (isAirItem(cursorItem)) {
                refreshInventory(inventoryEngine, player);
                return;
            }
            return;
        }

        if (isAirItem(cursorItem)) {
            return;
        }

        if (!this.enableCheckItem){
            return;
        }

        ItemStack itemStackCheck = this.checkItems.build(player);
        boolean isSuccess = this.itemStackSimilar.isSimilar(itemStackCheck, event.getCursor());

        if (isSuccess){
            if (this.dupeManager.isDupeItem(event.getCurrentItem())){
                event.setCurrentItem(new ItemStack(Material.AIR));
            }
            event.setCancelled(false);
            refreshInventory(inventoryEngine, player);
        }

        /*Credits Maxlego08*/
        if (!isSuccess & this.enableErrorItem) {
            errorItems(inventoryEngine, player, slot);
        }
    }

    protected void refreshInventory(InventoryEngine inventoryEngine, Player player){
        this.scheduler.runAtLocationLater(player.getLocation(), () -> {
            for (Button button : inventoryEngine.getButtons()) {
                if (button.isRefreshOnDrag()) inventoryEngine.buildButton(button);
            }
        }, 2);
    }

    /***
     * Credits Maxlego08
     * */
    protected void errorItems(InventoryEngine inventoryEngine, Player player, int slot){
        UUID playerUUID = player.getUniqueId();
        this.activeTasks.putIfAbsent(playerUUID, new HashMap<>()); // Initialiser la map pour ce joueur
        Map<Integer, Boolean> playerTasks = this.activeTasks.get(playerUUID);

        // Vérifier si une tâche est déjà active pour ce joueur et ce slot
        if (playerTasks.getOrDefault(slot, false)) {
            return; // Une tâche est déjà en cours pour ce slot
        }

        // Marquer la tâche comme active
        playerTasks.put(slot, true);

        Inventory inventory = inventoryEngine.getSpigotInventory();
        ItemStack itemStack = inventory.getItem(slot);
        inventory.setItem(slot, this.errorItems.build(player));

        this.scheduler.runAtLocationLater(player.getLocation(), () -> {

            try {
                Inventory topInventory = player.getOpenInventory().getTopInventory();
                if (topInventory.getHolder() instanceof InventoryEngine && topInventory.getHolder().equals(inventoryEngine)) {
                    inventory.setItem(slot, itemStack);
                }
            } finally {
                // Libérer le slot après l'exécution
                playerTasks.put(slot, false);

                // Nettoyer la map du joueur si elle est vide
                if (playerTasks.values().stream().noneMatch(Boolean::booleanValue)) {
                    activeTasks.remove(playerUUID);
                }
            }
        }, this.ticks);
    }

    protected boolean isAirItem(ItemStack itemStack){
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
