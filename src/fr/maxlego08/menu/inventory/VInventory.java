package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.builder.ItemBuilder;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class VInventory extends ZUtils implements Cloneable, InventoryHolder {

    protected int id;
    protected MenuPlugin plugin;
    protected Map<Integer, ItemButton> items = new HashMap<Integer, ItemButton>();
    protected Player player;
    protected int page;
    protected Object[] args;
    protected Inventory inventory;
    protected String guiName;
    protected boolean disableClick = true;
    protected boolean openAsync = false;

    private boolean isClose = false;

    public boolean isClose() {
        return isClose;
    }

    public int getId() {
        return id;
    }

    /**
     * Inventory Id
     *
     * @param id
     * @return
     */
    public VInventory setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Allows you to create the spigot inventory object
     *
     * @param name
     * @return this
     */
    protected void createInventory(String name) {
        createInventory(name, 54);
    }

    /**
     * Allows you to create the spigot inventory object
     *
     * @param name - Inventory name
     * @param size - Inventory Size
     * @return this
     */
    protected void createInventory(String name, int size) {
        this.guiName = name;
        this.inventory = Bukkit.createInventory(this, size, name);
    }

    /**
     * Create default inventory with default size and name
     */
    private void createDefaultInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "§cDefault Inventory");
        }
    }

    /**
     * Adding an item to the inventory
     *
     * @param slot     - Inventory slot
     * @param material - ItemStack material
     * @param name     - ItemStack name
     * @return ItemButton
     */
    public ItemButton addItem(int slot, Material material, String name) {
        return addItem(slot, new ItemBuilder(material, name).build());
    }

    /**
     * Adding an item to the inventory
     *
     * @param slot - Inventory slot
     * @param item - ItemBuild
     * @return ItemButton
     */
    public ItemButton addItem(int slot, ItemBuilder item) {
        return addItem(slot, item.build());
    }

    /**
     * Adding an item to the inventory
     * Creates the default inventory if it does not exist
     *
     * @param slot - Inventory slot
     * @param item - ItemStack
     * @return ItemButton
     */
    public ItemButton addItem(int slot, ItemStack item) {

        createDefaultInventory();

        ItemButton button = new ItemButton(item, slot);
        this.items.put(slot, button);

        if (this.openAsync) {
            runAsync(this.plugin, () -> this.inventory.setItem(slot, item));
        } else {
            this.inventory.setItem(slot, item);
        }
        return button;
    }

    /**
     * Allows you to remove an item from the list of items
     *
     * @param slot
     */
    public void removeItem(int slot) {
        this.items.remove(slot);
    }

    /**
     * Allows you to delete all items
     */
    public void clearItem() {
        this.items.clear();
    }

    /**
     * Allows you to retrieve all items
     *
     * @return
     */
    public Map<Integer, ItemButton> getItems() {
        return items;
    }

    /**
     * If the click in the inventory is disabled (which is the default)
     * then it will return true
     *
     * @return vrai ou faux
     */
    public boolean isDisableClick() {
        return disableClick;
    }

    /**
     * Change the ability to click in the inventory
     *
     * @param disableClick
     */
    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    /**
     * Allows to recover the player
     *
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Allows you to retrieve the page
     *
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @return the inventory
     */
    public Inventory getSpigotInventory() {
        return inventory;
    }

    /**
     * @return the guiName
     */
    public String getGuiName() {
        return guiName;
    }

    protected InventoryResult preOpenInventory(MenuPlugin main, Player player, int page, Object... args)
            throws InventoryOpenException {

        this.page = page;
        this.args = args;
        this.player = player;
        this.plugin = main;

        return openInventory(main, player, page, args);
    }

    public abstract InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args)
            throws InventoryOpenException;

    protected void onPreClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
        this.isClose = true;
        this.onClose(event, plugin, player);
    }

    /**
     * @param event
     * @param plugin
     * @param player
     */
    protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
    }

    /**
     * @param event
     * @param plugin
     * @param player
     */
    protected void onDrag(InventoryDragEvent event, MenuPlugin plugin, Player player) {
    }

    public MenuPlugin getPlugin() {
        return plugin;
    }

    @Override
    protected VInventory clone() {
        try {
            return getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void postOpen(MenuPlugin plugin, Player player, int page, Object[] objects) {
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
