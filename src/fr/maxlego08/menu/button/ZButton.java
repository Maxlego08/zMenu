package fr.maxlego08.menu.button;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.ButtonOption;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.PlayerSkin;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ZButton extends ZPlaceholderButton implements Button {

    private MenuPlugin plugin;
    private String buttonName;
    private MenuItemStack itemStack;
    private int slot = 0;
    private boolean isPermanent = false;
    private boolean closeInventory = false;
    private boolean useCache = true;
    private List<String> messages = new ArrayList<>();
    private SoundOption soundOption;
    private String playerHead;
    private OpenLink openLink = new ZOpenLink();
    private boolean isUpdated = false;
    private boolean refreshOnClick = false;
    private List<ActionPlayerData> datas = new ArrayList<>();
    private boolean updateOnClick = false;
    private List<Requirement> clickRequirements = new ArrayList<>();
    private Requirement viewRequirement;
    private List<Action> actions = new ArrayList<>();
    private List<ButtonOption> options = new ArrayList<>();

    @Override
    public String getName() {
        return this.buttonName;
    }

    @Override
    public MenuItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * @param itemStack the itemStack to set
     */
    public ZButton setItemStack(MenuItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCustomItemStack(Player player) {
        if (this.itemStack == null) return null;

        ItemStack itemStack = this.itemStack.build(player, this.useCache);

        if (this.playerHead != null && itemStack.getItemMeta() instanceof SkullMeta) {

            String name = papi(this.playerHead.replace("%player%", player.getName()), player);

            if (!isMinecraftName(name)) {
                return itemStack;
            }

            if (NMSUtils.isNewHeadApi()) {

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                if (offlinePlayer != null) {
                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    skullMeta.setOwnerProfile(offlinePlayer.getPlayerProfile());
                    itemStack.setItemMeta(skullMeta);
                }
            } else {
                String texture = PlayerSkin.getTexture(name);
                if (texture == null) {

                    SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                    skullMeta.setOwner(name);
                    itemStack.setItemMeta(skullMeta);
                } else {
                    this.applyTexture(itemStack, texture);
                }
            }
        }

        return itemStack;
    }

    @Override
    public int getSlot() {
        return this.slot;
    }

    /**
     * @param slot the slot to set
     */
    public ZButton setSlot(int slot) {
        this.slot = slot;
        return this;
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return this.isPermanent;
    }

    /**
     * @param isPermanent the isPermanent to set
     */
    public ZButton setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
        return this;
    }

    @Override
    public List<String> getMessages() {
        return this.messages;
    }

    public ZButton setMessages(List<String> messages) {
        this.messages = messages;
        return this;
    }

    @Override
    public int getRealSlot(int inventorySize, int page) {
        return this.isPermanent() ? this.slot : this.slot - ((page - 1) * inventorySize);
    }

    @Override
    public SoundOption getSound() {
        return this.soundOption;
    }

    @Override
    public boolean hasSpecialRender() {
        return this.getSlots().size() > 0;
    }

    @Override
    public String getPlayerHead() {
        return this.playerHead;
    }

    public ZButton setPlayerHead(String playerHead) {
        this.playerHead = playerHead;
        return this;
    }

    @Override
    public void onRender(Player player, InventoryDefault inventory) {
        inventory.displayFinalButton(this, this.getSlots().stream().mapToInt(Integer::intValue).toArray());
    }

    @Override
    public void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
    }

    @Override
    public void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
    }

    @Override
    public void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
    }

    @Override
    public void onInventoryClose(Player player, InventoryDefault inventory) {

    }

    @Override
    public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot, Placeholders placeholders) {

        if (this.closeInventory()) {
            player.closeInventory();
        }

        if (!this.datas.isEmpty()) {

            DataManager dataManager = this.plugin.getDataManager();
            for (ActionPlayerData actionPlayerData : this.datas) {
                actionPlayerData.execute(player, dataManager);
            }

        }

        if (this.messages.size() > 0) {

            if (this.openLink != null) {

                this.openLink.send(player, this.messages);
            } else {

                this.messages.forEach(message -> Meta.meta.sendMessage(player, this.papi(message.replace("%player%", player.getName()), player)));
            }
        }

        if (this.soundOption != null) {
            this.soundOption.play(player);
        }

        AtomicBoolean isSuccess = new AtomicBoolean(true);

        this.clickRequirements.forEach(requirement -> {
            if (requirement.getClickTypes().contains(event.getClick())) {
                isSuccess.set(requirement.execute(player, this, inventory, placeholders));
            }
        });

        this.actions.forEach(action -> action.preExecute(player, this, inventory, placeholders));
        this.options.forEach(option -> option.onClick(this, player, event, inventory, slot, isSuccess.get()));

        this.execute(player, event.getClick(), inventory.getPlugin().getScheduler());
    }

    @Override
    public void onInventoryOpen(Player player, InventoryDefault inventory) {

    }

    @Override
    public boolean closeInventory() {
        return this.closeInventory;
    }

    /**
     * @param buttonName the buttonName to set
     */
    public ZButton setButtonName(String buttonName) {
        this.buttonName = buttonName;
        return this;
    }

    /**
     * @param closeInventory
     */
    public ZButton setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
        return this;
    }

    /**
     * @param soundOption
     */
    public ZButton setSoundOption(SoundOption soundOption) {
        this.soundOption = soundOption;
        return this;
    }

    @Override
    public OpenLink getOpenLink() {
        return this.openLink;
    }

    public void setOpenLink(OpenLink openLink) {
        this.openLink = openLink;
    }

    @Override
    public boolean isUpdated() {
        return this.isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    @Override
    public boolean isRefreshOnClick() {
        return this.refreshOnClick;
    }

    public void setRefreshOnClick(boolean refreshOnClick) {
        this.refreshOnClick = refreshOnClick;
    }

    @Override
    public List<ActionPlayerData> getData() {
        return this.datas;
    }

    public void setDatas(List<ActionPlayerData> datas) {
        this.datas = datas;
    }

    public void setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean updateOnClick() {
        return this.updateOnClick;
    }

    public void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    @Override
    public List<String> buildLore(Player player) {
        return this.itemStack.getLore();
    }

    @Override
    public String buildDisplayName(Player player) {
        return this.itemStack.getDisplayName();
    }

    @Override
    public void onBackClick(Player player, InventoryClickEvent event, InventoryDefault inventory, List<Inventory> oldInventories, Inventory toInventory, int slot) {
    }

    @Override
    public List<Requirement> getClickRequirements() {
        return this.clickRequirements;
    }

    public void setClickRequirements(List<Requirement> clickRequirements) {
        this.clickRequirements = clickRequirements;
    }

    @Override
    public Requirement getViewRequirement() {
        return this.viewRequirement;
    }

    public void setViewRequirement(Requirement viewRequirement) {
        this.viewRequirement = viewRequirement;
    }

    @Override
    public boolean hasPermission() {
        return this.viewRequirement != null || super.hasPermission();
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory, Placeholders placeholders) {
        return super.checkPermission(player, inventory, placeholders) && this.viewRequirement.execute(player, this, inventory, placeholders);
    }

    @Override
    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void onDrag(InventoryDragEvent event, Player player, InventoryDefault inventoryDefault) {

    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryDefault inventoryDefault) {

    }

    @Override
    public boolean isUseCache() {
        return this.useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Override
    public List<ButtonOption> getOptions() {
        return this.options;
    }

    public void setOptions(List<ButtonOption> options) {
        this.options = options;
    }

    @Override
    public boolean hasCustomRender() {
        return false;
    }
}
