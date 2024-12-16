package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.builder.ItemBuilder;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class VInventory extends ZUtils implements Cloneable, InventoryHolder {

    protected int id;
    protected MenuPlugin plugin;
    protected Map<Integer, ItemButton> items = new HashMap<>();
    protected Player player;
    protected int page;
    protected Object[] args;
    protected Inventory inventory;
    protected String guiName;
    protected boolean disableClick = true;
    protected boolean disablePlayerInventoryClick = true;
    protected boolean openAsync = false;

    private boolean isClose = false;

    public boolean isClose() {
        return isClose;
    }

    public int getId() {
        return id;
    }

    public VInventory setId(int id) {
        this.id = id;
        return this;
    }

    protected void createInventory(String name) {
        createInventory(name, 54);
    }

    protected void createInventory(String name, int size) {
        this.guiName = name;
        this.inventory = Bukkit.createInventory(this, size, name);
    }
    protected void createInventory(String name, InventoryType inventoryType) {
        this.guiName = name;
        this.inventory = Bukkit.createInventory(this, inventoryType, name);
    }
    protected void createMetaInventory(String name, int size) {
        this.guiName = name;
        this.inventory = Meta.meta.createInventory(name, size, this);
    }
    protected void createMetaInventory(String name, InventoryType inventoryType) {
        this.guiName = name;
        this.inventory = Meta.meta.createInventory(name, inventoryType, this);
    }

    private void createDefaultInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "§cDefault Inventory");
        }
    }

    public ItemButton addItem(int slot, Material material, String name) {
        return addItem(slot, new ItemBuilder(material, name).build());
    }

    public ItemButton addItem(int slot, ItemBuilder item) {
        return addItem(slot, item.build());
    }

    public ItemButton addItem(int slot, ItemStack itemStack) {
        return addItem(slot, itemStack, true);
    }

    public ItemButton addItem(int slot, ItemStack itemStack, Boolean enableAntiDupe) {

        createDefaultInventory();

        if (Config.enableAntiDupe && enableAntiDupe) {
            itemStack = this.plugin.getDupeManager().protectItem(itemStack);
        }

        ItemButton button = new ItemButton(itemStack, slot);
        this.items.put(slot, button);

        if (this.openAsync) {
            ItemStack finalItem = itemStack;
            runAsync(this.plugin, () -> this.inventory.setItem(slot, finalItem));
        } else {
            this.inventory.setItem(slot, itemStack);
        }
        return button;
    }

    public void removeItem(int slot) {
        this.items.remove(slot);
    }

    public void clearItem() {
        this.items.clear();
    }

    public Map<Integer, ItemButton> getItems() {
        return items;
    }

    public boolean isDisableClick() {
        return disableClick;
    }

    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPage() {
        return page;
    }

    public Object[] getArgs() {
        return args;
    }

    public Inventory getSpigotInventory() {
        return inventory;
    }

    public String getGuiName() {
        return guiName;
    }

    protected InventoryResult preOpenInventory(MenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.page = page;
        this.args = args;
        this.player = player;
        this.plugin = main;

        return openInventory(main, player, page, args);
    }

    public abstract InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException;

    protected void onPreClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
        this.isClose = true;
        this.onClose(event, plugin, player);
    }


    protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
    }

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

    public boolean isDisablePlayerInventoryClick() {
        return this.disablePlayerInventoryClick;
    }

    public void setDisablePlayerInventoryClick(boolean disablePlayerInventoryClick) {
        this.disablePlayerInventoryClick = disablePlayerInventoryClick;
    }

    public void onInventoryClick(InventoryClickEvent event, MenuPlugin plugin, Player player) {

    }

    public void setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
    }
}
