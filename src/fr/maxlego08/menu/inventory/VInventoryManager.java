package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.exceptions.InventoryAlreadyExistException;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.listener.ListenerAdapter;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class VInventoryManager extends ListenerAdapter {

    private final Map<Integer, VInventory> inventories = new HashMap<>();
    //private final Map<UUID, VInventory> playerInventories = new HashMap<>();
    private final Map<Inventory, VInventory> playerInventories = new HashMap<>();
    private final MenuPlugin plugin;

    /**
     * @param plugin
     */
    public VInventoryManager(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    /**
     * Allows you to record an inventory If the inventory ID already exists then
     * an exception will be throw
     *
     * @param enumInventory
     * @param inventory
     */
    public void registerInventory(EnumInventory enumInventory, VInventory inventory) {
        if (!this.inventories.containsKey(enumInventory.getId())) {
            this.inventories.put(enumInventory.getId(), inventory);
        } else {
            throw new InventoryAlreadyExistException("Inventory with id " + inventory.getId() + " already exist !");
        }
    }

    /**
     * Allows you to open an inventory
     *
     * @param enumInventory - Inventory enum for get the ID
     * @param player        - Player that will open the inventory
     * @param page          - The inventory page
     * @param objects       - The arguments used to make the inventory work
     */
    public void createInventory(EnumInventory enumInventory, Player player, int page, Object... objects) {
        this.createInventory(enumInventory.getId(), player, page, objects);
    }

    /**
     * Allows you to open an inventory When opening the inventory will be cloned
     *
     * @param id      - Inventory ID
     * @param player  - Player that will open the inventory
     * @param page    - The inventory page
     * @param objects - The arguments used to make the inventory work
     */
    public void createInventory(int id, Player player, int page, Object... objects) {
        Optional<VInventory> optional = this.getInventory(id);

        if (!optional.isPresent()) {
            message(player, Message.VINVENTORY_CLONE_NULL, "%id%", id);
            return;
        }

        VInventory inventory = optional.get();

        // We need to clone the object to have one object per open inventory
        // An inventory will remain open for several seconds, during this time
        // the inventories of the inventory must be correctly saved according to
        // the player.
        VInventory clonedInventory = inventory.clone();

        if (clonedInventory == null) {
            message(player, Message.VINVENTORY_CLONE_NULL, "%id%", id);
            return;
        }

        clonedInventory.setId(id);
        try {
            InventoryResult result = clonedInventory.preOpenInventory(this.plugin, player, page, objects);
            if (result.equals(InventoryResult.SUCCESS)) {

                clonedInventory.postOpen(this.plugin, player, page, objects);

                Inventory spigotInventory = clonedInventory.getSpigotInventory();
                player.openInventory(spigotInventory);
                this.playerInventories.put(spigotInventory, clonedInventory);

            } else if (result.equals(InventoryResult.ERROR)) {
                message(player, Message.VINVENTORY_OPEN_ERROR, "%id%", id);
            }
        } catch (InventoryOpenException e) {
            message(player, Message.VINVENTORY_OPEN_ERROR, "%id%", id);
            e.printStackTrace();
        }
    }

    public void createInventory(VInventory parent, Player player) {
        createInventory(parent.getId(), player, parent.getPage(), parent.getArgs());
    }

    @Override
    protected void onInventoryClick(InventoryClickEvent event, Player player) {

        if (event.getClickedInventory() == null) {
            return;
        }

        if (event.getWhoClicked() instanceof Player) {

            VInventory gui = this.playerInventories.get(event.getInventory());

            if (gui.getGuiName() == null || gui.getGuiName().length() == 0) {
                Logger.info("An error has occurred with the menu ! " + gui.getClass().getName());
                return;
            }
            if (gui.getPlayer().equals(player) && event.getView().getTitle().equals(gui.getGuiName())) {

                event.setCancelled(gui.isDisableClick());

                if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                    return;
                }

                ItemButton button = gui.getItems().getOrDefault(event.getSlot(), null);
                if (button != null)
                    button.onClick(event);
            }
        }
    }

    @Override
    protected void onInventoryClose(InventoryCloseEvent event, Player player) {
        VInventory inventory = this.playerInventories.get(event.getInventory());
        if (inventory != null) {
            System.out.println("CLOSE");
            System.out.println("Avant EVENT " + this.playerInventories.size() + " -> " + this.playerInventories);
            this.playerInventories.remove(event.getInventory());
            inventory.onPreClose(event, this.plugin, player);
        }
    }

    @Override
    protected void onInventoryDrag(InventoryDragEvent event, Player player) {
        VInventory inventory = this.playerInventories.get(event.getInventory());
        if (inventory != null) {
            inventory.onDrag(event, this.plugin, player);
        }
    }

    /**
     * @param id - Inventory I'd
     * @return Optional - Allows to return the inventory in an optional
     */
    private Optional<VInventory> getInventory(int id) {
        return Optional.ofNullable(this.inventories.getOrDefault(id, null));
    }

    public void close() {
        this.close(v -> !v.isClose());
    }

    public void close(Predicate<VInventory> predicate) {
        new ArrayList<>(this.playerInventories.values()).stream().filter(predicate).forEach(vInventory -> {
            Player player = vInventory.getPlayer();
            if (player.isOnline()) {
                player.closeInventory();
            }
        });
    }

    public Map<Inventory, VInventory> getPlayerInventories() {
        return playerInventories;
    }

    public boolean hasOpenInventory(Player player) {
        return this.playerInventories.values().stream().anyMatch(e -> e.getPlayer() == player);
    }
}
