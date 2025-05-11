package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public abstract class Button extends PlaceholderButton {

    private MenuPlugin plugin;
    private String buttonName;
    private MenuItemStack itemStack;
    private boolean isPermanent = false;
    private boolean closeInventory = false;
    private boolean useCache = true;
    private List<String> messages = new ArrayList<>();
    private SoundOption soundOption;
    private String playerHead;
    private OpenLink openLink;
    private boolean isUpdated = false;
    private boolean isMasterButtonUpdated = false;
    private boolean refreshOnClick = false;
    private List<ActionPlayerData> datas = new ArrayList<>();
    private boolean updateOnClick = false;
    private boolean isOpenAsync = false;
    private List<Requirement> clickRequirements = new ArrayList<>();
    private Requirement viewRequirement;
    private List<Action> actions = new ArrayList<>();
    private List<ButtonOption> options = new ArrayList<>();
    private RefreshRequirement refreshRequirement;
    private int priority; // only use for convert DeluxeMenus config to zmenu object
    private boolean isInPlayerInventory;

    public String getName() {
        return this.buttonName;
    }

    public MenuItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * @param itemStack the itemStack to set
     */
    public Button setItemStack(MenuItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public ItemStack getCustomItemStack(Player player) {
        if (this.itemStack == null) return null;

        ItemStack itemStack = this.itemStack.build(player, this.useCache);

        if (this.playerHead != null && itemStack.getItemMeta() instanceof SkullMeta) {
            return this.plugin.getInventoryManager().postProcessSkullItemStack(itemStack, this, player);
        }

        return itemStack;
    }

    public int getSlot() {
        return this.slots.get(0);
    }

    /**
     * @param slot the slot to set
     */
    public Button setSlot(int slot) {
        this.slots = new ArrayList<>();
        this.slots.add(slot);
        return this;
    }

    public boolean isClickable() {
        return true;
    }

    public boolean isPermanent() {
        return this.isPermanent;
    }

    /**
     * @param isPermanent the isPermanent to set
     */
    public Button setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
        return this;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public Button setMessages(List<String> messages) {
        this.messages = messages;
        return this;
    }

    public int getRealSlot(int inventorySize, int page) {
        int slot = getSlot();
        return this.isPermanent() ? slot : slot - ((page - 1) * inventorySize);
    }

    public SoundOption getSound() {
        return this.soundOption;
    }

    public boolean hasSpecialRender() {
        return this.getSlots().size() > 1;
    }

    public String getPlayerHead() {
        return this.playerHead;
    }

    public Button setPlayerHead(String playerHead) {
        this.playerHead = playerHead;
        return this;
    }

    public void onRender(Player player, InventoryEngine inventoryEngine) {
        if (inventoryEngine.getPage() == this.getPage() || this.isPermanent()) {

            int inventorySize = this.isPlayerInventory() ? 36 : inventoryEngine.getInventory().getSize();

            int[] slots = this.getSlots().stream().map(slot -> {
                if (!this.isPermanent) {
                    return slot - ((this.getPage() - 1) * inventorySize);
                }
                return slot;
            }).mapToInt(Integer::intValue).toArray();
            inventoryEngine.displayFinalButton(this, slots);
        }
    }

    public void onLeftClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot) {
    }

    public void onRightClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot) {
    }

    public void onMiddleClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot) {
    }

    public void onInventoryClose(Player player, InventoryEngine inventory) {

    }

    public void onClick(Player player, InventoryClickEvent event, InventoryEngine inventory, int slot, Placeholders placeholders) {

        if (this.closeInventory()) {
            player.closeInventory();
        }

        if (!this.datas.isEmpty()) {

            DataManager dataManager = this.plugin.getDataManager();
            for (ActionPlayerData actionPlayerData : this.datas) {
                actionPlayerData.execute(player, dataManager);
            }
        }

        if (!this.messages.isEmpty()) {
            if (this.openLink != null) {
                this.openLink.send(player, this.messages);
            } else {
                this.messages.forEach(message -> plugin.getMetaUpdater().sendMessage(player, this.plugin.parse(player, placeholders.parse(message.replace("%player%", player.getName())))));
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

        this.execute(this.plugin, event.getClick(), placeholders, player);
    }

    public void onInventoryOpen(Player player, InventoryEngine inventory, Placeholders placeholders) {

    }

    public boolean closeInventory() {
        return this.closeInventory;
    }

    public Button setButtonName(String buttonName) {
        this.buttonName = buttonName;
        return this;
    }

    public Button setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
        return this;
    }

    public Button setSoundOption(SoundOption soundOption) {
        this.soundOption = soundOption;
        return this;
    }


    public OpenLink getOpenLink() {
        return this.openLink;
    }

    public void setOpenLink(OpenLink openLink) {
        this.openLink = openLink;
    }


    public boolean isUpdated() {
        return this.isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }


    public boolean isRefreshOnClick() {
        return this.refreshOnClick;
    }

    public void setRefreshOnClick(boolean refreshOnClick) {
        this.refreshOnClick = refreshOnClick;
    }


    public List<ActionPlayerData> getData() {
        return this.datas;
    }

    public void setDatas(List<ActionPlayerData> datas) {
        this.datas = datas;
    }

    public void setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
    }


    public boolean updateOnClick() {
        return this.updateOnClick;
    }

    public void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }


    public List<String> buildLore(Player player) {
        return this.itemStack.getLore();
    }


    public String buildDisplayName(Player player) {
        return this.itemStack.getDisplayName();
    }

    public void onBackClick(Player player, InventoryClickEvent event, InventoryEngine inventory, List<Inventory> oldInventories, Inventory toInventory, int slot) {
    }

    public List<Requirement> getClickRequirements() {
        return this.clickRequirements;
    }

    public void setClickRequirements(List<Requirement> clickRequirements) {
        this.clickRequirements = clickRequirements;
    }

    public Requirement getViewRequirement() {
        return this.viewRequirement;
    }

    public void setViewRequirement(Requirement viewRequirement) {
        this.viewRequirement = viewRequirement;
    }

    public boolean hasPermission() {
        return this.viewRequirement != null || super.hasPermission();
    }

    public boolean checkPermission(Player player, InventoryEngine inventory, Placeholders placeholders) {
        return super.checkPermission(player, inventory, placeholders) && (this.viewRequirement == null || this.viewRequirement.execute(player, this, inventory, placeholders));
    }

    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }


    public void onDrag(InventoryDragEvent event, Player player, InventoryEngine inventoryDefault) {

    }

    public void onInventoryClick(InventoryClickEvent event, Player player, InventoryEngine inventoryDefault) {

    }

    public boolean isUseCache() {
        return this.useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public List<ButtonOption> getOptions() {
        return this.options;
    }

    public void setOptions(List<ButtonOption> options) {
        this.options = options;
    }

    public boolean hasCustomRender() {
        return false;
    }

    public boolean isUpdatedMasterButton() {
        return this.isMasterButtonUpdated;
    }

    public void setMasterButtonUpdated(boolean masterButtonUpdated) {
        isMasterButtonUpdated = masterButtonUpdated;
    }

    public boolean isOpenAsync() {
        return isOpenAsync;
    }

    public void setOpenAsync(boolean openAsync) {
        isOpenAsync = openAsync;
    }


    public boolean hasRefreshRequirement() {
        return this.refreshRequirement != null;
    }

    public RefreshRequirement getRefreshRequirement() {
        return refreshRequirement;
    }

    public void setRefreshRequirement(RefreshRequirement refreshRequirement) {
        this.refreshRequirement = refreshRequirement;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Paginate a given list of elements and set them as elements of this button
     * according to the page of the inventory.
     *
     * @param elements  the elements to paginate
     * @param inventory the inventory to get the page from
     * @param consumer  a consumer that will be used to set the elements, it will
     *                  be called with the slot of the element and the element
     *                  itself
     * @param <T>       the type of the elements
     */
    protected <T> void paginate(List<T> elements, InventoryEngine inventory, BiConsumer<Integer, T> consumer) {
        Pagination<T> pagination = new Pagination<>();
        elements = pagination.paginate(elements, this.slots.size(), inventory.getPage());

        for (int i = 0; i != Math.min(elements.size(), this.slots.size()); i++) {
            int slot = slots.get(i);
            T element = elements.get(i);
            consumer.accept(slot, element);
        }
    }


    public boolean isPlayerInventory() {
        return this.isInPlayerInventory;
    }

    public void setPlayerInventory(boolean inPlayerInventory) {
        isInPlayerInventory = inPlayerInventory;
    }
}
