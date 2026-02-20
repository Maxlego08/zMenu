package fr.maxlego08.menu.inventory;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.exceptions.InventoryOpenException;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

public abstract class VInventory extends ZUtils implements Cloneable, BaseInventory {

    protected int id;
    protected ZMenuPlugin plugin;
    protected final Map<Integer, ItemButton> items = new HashMap<>();
    protected final Map<Integer, ItemButton> playerInventoryItems = new HashMap<>();
    protected Player player;
    protected int page;
    protected Object[] args;
    protected Inventory inventory;
    protected String guiName;
    protected boolean disableClick = true;
    protected boolean disablePlayerInventoryClick = true;
    protected boolean isClickLimiterEnabled = true;
    private TitleAnimation titleAnimation;
    private PlayerTitleAnimation playerTitleAnimation;
    private ClearInvType clearInvType = ClearInvType.DEFAULT;

    private boolean isClose = false;

    @Override
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
        this.inventory = this.plugin.getMetaUpdater().createInventory(name, size, this);
    }

    protected void createMetaInventory(String name, InventoryType inventoryType) {
        this.guiName = name;
        this.inventory = this.plugin.getMetaUpdater().createInventory(name, inventoryType, this);
    }

    private void createDefaultInventory() {
        if (this.inventory == null) {
            this.inventory = Bukkit.createInventory(this, 54, "§cDefault Inventory");
        }
    }

    @Override
    public ItemButton addItem(int slot, ItemStack itemStack) {
        return addItem(false, slot, itemStack, true);
    }

    @Override
    public ItemButton addItem(boolean inPlayerInventory, int slot, ItemStack itemStack) {
        return addItem(inPlayerInventory, slot, itemStack, true);
    }

    @Override
    public ItemButton addItem(int slot, ItemStack itemStack, boolean enableAntiDupe) {
        return addItem(false, slot, itemStack, enableAntiDupe);
    }

    @Override
    public ItemButton addItem(boolean inPlayerInventory, int slot, ItemStack itemStack, boolean enableAntiDupe) {

        createDefaultInventory();

        if (itemStack == null) {
            this.plugin.getLogger().severe("Attention, a null ItemStack was found in slot " + slot + " ! > " + this);
            return null;
        }

        if (Configuration.enableAntiDupe && enableAntiDupe) {
            itemStack = this.plugin.getDupeManager().protectItem(itemStack);
        }

        ItemButton button = new ItemButton(itemStack, slot, inPlayerInventory, this);

        boolean needCancel = false;
        for (InventoryListener inventoryListener : this.plugin.getInventoryManager().getInventoryListeners()) {
            if (inventoryListener.addItem(this, inPlayerInventory, button, enableAntiDupe)) {
                needCancel = true;
            }
        }

        if (inPlayerInventory) {

            this.playerInventoryItems.put(slot, button);
            if (!needCancel) this.player.getInventory().setItem(slot, itemStack);
        } else {

            this.items.put(slot, button);
            if (!needCancel) this.inventory.setItem(slot, itemStack);
        }
        return button;
    }

    @Override
    public void removeItem(int slot) {
        this.items.remove(slot);
    }

    @Override
    public void removePlayerItem(int slot) {
        this.playerInventoryItems.remove(slot);
    }

    @Override
    public void clearItem() {
        this.items.clear();
        this.playerInventoryItems.clear();
    }

    @Override
    public @NonNull Map<Integer, ItemButton> getItems() {
        return items;
    }

    @Override
    public @NonNull Map<Integer, ItemButton> getPlayerInventoryItems() {
        return playerInventoryItems;
    }

    @Override
    public boolean isDisableClick() {
        return disableClick;
    }

    @Override
    public void setDisableClick(boolean disableClick) {
        this.disableClick = disableClick;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public @NonNull Inventory getSpigotInventory() {
        return inventory;
    }

    public String getGuiName() {
        return guiName;
    }

    protected InventoryResult preOpenInventory(@NotNull ZMenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.page = page;
        this.args = args;
        this.player = player;
        this.plugin = main;

        return openInventory(main, player, page, args);
    }

    public abstract InventoryResult openInventory(ZMenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException;

    protected void onPreClose(InventoryCloseEvent event, ZMenuPlugin plugin, Player player) {
        this.isClose = true;
        if (this.playerTitleAnimation != null){
            this.playerTitleAnimation.stop();
        }
        this.onClose(event, plugin, player);
    }

    protected void onClose(InventoryCloseEvent event, ZMenuPlugin plugin, Player player) {
    }

    protected void onDrag(InventoryDragEvent event, ZMenuPlugin plugin, Player player) {
    }

    public @NonNull ZMenuPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(ZMenuPlugin plugin) {
        this.plugin = plugin;
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

    public void postOpen(ZMenuPlugin plugin, Player player, int page, Object[] objects) {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    public boolean isDisablePlayerInventoryClick() {
        return this.disablePlayerInventoryClick;
    }

    public void setDisablePlayerInventoryClick(boolean disablePlayerInventoryClick) {
        this.disablePlayerInventoryClick = disablePlayerInventoryClick;
    }

    @Override
    public void setPlayerTitleAnimation(PlayerTitleAnimation playerTitleAnimation){
        this.playerTitleAnimation = playerTitleAnimation;
    }

    @Override
    public PlayerTitleAnimation getPlayerTitleAnimation(){
        return this.playerTitleAnimation;
    }

    @Override
    public void setTitleAnimation(TitleAnimation animation){
        this.titleAnimation = animation;
    }

    @Override
    public TitleAnimation getTitleAnimation(){
        return this.titleAnimation;
    }

    @Override
    public void setClearInvType(ClearInvType clearInvType) {
        if (clearInvType == null) return;
        this.clearInvType = clearInvType;
    }

    @Override
    public ClearInvType getClearInvType() {
        return this.clearInvType;
    }

    @Override
    public void setClickLimiterEnabled(boolean enabled) {
        this.isClickLimiterEnabled = enabled;
    }

    @Override
    public boolean isClickLimiterEnabled() {
        return this.isClickLimiterEnabled;
    }

    public void onInventoryClick(InventoryClickEvent event, ZMenuPlugin plugin, Player player) {

    }
}
