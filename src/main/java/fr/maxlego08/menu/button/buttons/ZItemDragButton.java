package fr.maxlego08.menu.button.buttons;

import com.tcoded.folialib.impl.PlatformScheduler;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.ItemDragButton;
import fr.maxlego08.menu.api.dupe.DupeManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.rules.ZRuleContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ZItemDragButton extends ItemDragButton {

    private final DupeManager dupeManager;
    private final PlatformScheduler scheduler;
    //Check item section
    private boolean enableCheckItem = false;

    private MenuItemStack checkItems;
    private ItemStackSimilar itemStackSimilar;

    private Rule rule;

    //Error item section
    private boolean enableErrorItem = false;
    private MenuItemStack errorItems;
    private boolean useErrorItemCache = true;
    private final Map<UUID, Map<Integer, Boolean>> activeTasks = new HashMap<>();
    private int ticks;

    public ZItemDragButton(@NotNull MenuPlugin plugin) {
        super();
        this.dupeManager = plugin.getDupeManager();
        this.scheduler = plugin.getInventoryManager().getScheduler();
    }

    public void setCheckItem(MenuItemStack menuItemStack, ItemStackSimilar itemStackSimilar) {
        this.enableCheckItem = true;
        this.checkItems = menuItemStack;
        this.itemStackSimilar = itemStackSimilar;
    }

    public void setErrorItem(MenuItemStack menuItemStack, int ticks, boolean useCache) {
        this.enableErrorItem = true;
        this.errorItems = menuItemStack;
        this.ticks = ticks;
        this.useErrorItemCache = useCache;
    }

    public void setRule(Rule rule) {
        this.enableCheckItem = true;
        this.rule = rule;
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
            if (this.getItemStack() != null)
                inventoryEngine.displayFinalButton(this, new Placeholders(), slots);
        }
    }

    @Override
    public void onInventoryClose(@NonNull Player player, @NonNull InventoryEngine inventory) {
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
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventoryEngine, int slot, @NonNull Placeholders placeholders) {

        ItemStack clickedItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        if (!this.dupeManager.isDupeItem(clickedItem)){

            if (this.isAirItem(cursorItem)) {
                event.setCancelled(false);
                this.refreshInventory(inventoryEngine, player, placeholders);
                return;
            } else {
                if (!this.enableCheckItem) {
                    event.setCancelled(false);
                    return;
                }
                boolean isSuccess = this.isSuccess(player, cursorItem);
                if (isSuccess) {
                    event.setCancelled(false);
                }
            }
            return;
        }

        if (this.isAirItem(cursorItem)) {
            return;
        }

        if (!this.enableCheckItem){
            return;
        }

        boolean isSuccess = this.isSuccess(player, cursorItem);

        if (isSuccess){
            if (this.dupeManager.isDupeItem(clickedItem)){
                event.setCurrentItem(new ItemStack(Material.AIR));
            }
            event.setCancelled(false);
            this.refreshInventory(inventoryEngine, player, placeholders);
        }

        /*
        Credits Maxlego08
        */
        if (!isSuccess & this.enableErrorItem) {
            this.errorItems(inventoryEngine, player, slot, placeholders);
        }
    }

    private boolean isSuccess(@NotNull Player player, @NotNull ItemStack itemStack){
        return (rule == null || rule.matches(new ZRuleContext(itemStack))) && (this.itemStackSimilar == null || this.itemStackSimilar.isSimilar(itemStack, this.checkItems.build(player)));
    }

    protected void refreshInventory(@NotNull InventoryEngine inventoryEngine,@NotNull Player player,@NotNull Placeholders placeholders){
        this.scheduler.runAtLocationLater(player.getLocation(), () -> {
            for (Button button : inventoryEngine.getButtons()) {
                if (button.isRefreshOnDrag())
                    inventoryEngine.buildButton(button, placeholders);
            }
        }, 2);
    }

    /***
     * Credits Maxlego08
     * */
    protected void errorItems(@NotNull InventoryEngine inventoryEngine,@NotNull Player player, int slot, @NonNull Placeholders placeholders){
        UUID playerUUID = player.getUniqueId();
        this.activeTasks.putIfAbsent(playerUUID, new HashMap<>()); // Initialiser la map pour ce joueur
        Map<Integer, Boolean> playerTasks = this.activeTasks.get(playerUUID);

        // Vérifier si une tâche est déjà active pour ce joueur et ce slot
        if (playerTasks.getOrDefault(slot, false)) {
            return; // Une tâche est déjà en cours pour ce slot
        }

        // Marquer la tâche comme active
        playerTasks.put(slot, true);

        inventoryEngine.addItem(slot, this.errorItems.build(player, this.useErrorItemCache, placeholders));

        this.scheduler.runAtEntityLater(player, w -> {

            try {
                Inventory topInventory = player.getOpenInventory().getTopInventory();
                if (topInventory.getHolder() instanceof InventoryEngine && topInventory.getHolder().equals(inventoryEngine)) {
                    inventoryEngine.displayFinalButton(this, placeholders, slot);
                }
            } finally {
                // Libérer le slot après l'exécution
                playerTasks.put(slot, false);

                // Nettoyer la map du joueur si elle est vide
                if (playerTasks.values().stream().noneMatch(Boolean::booleanValue)) {
                    this.activeTasks.remove(playerUUID);
                }
            }
        }, this.ticks);
    }

    @Contract("null -> true")
    protected boolean isAirItem(ItemStack itemStack){
        return itemStack == null || itemStack.getType() == Material.AIR;
    }
}
