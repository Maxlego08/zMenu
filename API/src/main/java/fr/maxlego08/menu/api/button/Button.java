package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.AnvilInventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.requirement.*;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.permissible.PermissionPermissible;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.TextChange;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
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
    private boolean refreshOnDrag = false;
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

    @Contract(pure = true)
    @Nullable
    public String getName() {
        return this.buttonName;
    }

    @Contract(pure = true)
    @Nullable
    public MenuItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * @param itemStack the itemStack to set
     */
    @Contract("_ -> this")
    public Button setItemStack(@Nullable MenuItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public ItemStack getCustomItemStack(@NotNull Player player, boolean useCache, @NotNull Placeholders placeholders) {
        return this.itemStack == null ? new ItemStack(Material.STONE) : this.itemStack.build(player, useCache, placeholders);
    }

    public int getSlot() {
        return this.slots.getFirst();
    }

    /**
     * @param slot the slot to set
     */
    @Contract("_ -> this")
    public Button setSlot(int slot) {
        this.slots = new ArrayList<>();
        this.slots.add(slot);
        return this;
    }

    public boolean isClickable() {
        return true;
    }

    @Contract(pure = true)
    public boolean isPermanent() {
        return this.isPermanent;
    }

    /**
     * @param isPermanent the isPermanent to set
     */
    @Contract("_ -> this")
    @NotNull
    public Button setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public List<String> getMessages() {
        return this.messages;
    }

    @Contract("_ -> this")
    @NotNull
    public Button setMessages(@NotNull List<String> messages) {
        this.messages = messages;
        return this;
    }

    @Contract(pure = true)
    public int getRealSlot(int inventorySize, int page) {
        int slot = this.getSlot();
        return this.isPermanent() ? slot : slot - ((page - 1) * inventorySize);
    }

    @Contract(pure = true)
    @Nullable
    public SoundOption getSound() {
        return this.soundOption;
    }

    @Contract(pure = true)
    public boolean hasSpecialRender() {
        return this.getSlots().size() > 1;
    }

    @Contract(pure = true)
    @Nullable
    public String getPlayerHead() {
        return this.playerHead;
    }

    /**
     * @param playerHead the playerHead to set
     */
    @Contract("_ -> this")
    public Button setPlayerHead(@Nullable String playerHead) {
        this.playerHead = playerHead;
        return this;
    }

    @Contract(pure = true)
    public void onRender(Player player, InventoryEngine inventoryEngine) {
        if (inventoryEngine.getPage() == this.getPage() || this.isPermanent()) {

            PerformanceDebug perfDebug = inventoryEngine.getPerformanceDebug();

            perfDebug.start("onRender.slotCalc." + this.getName());
            int inventorySize = this.isPlayerInventory() ? 36 : inventoryEngine.getInventory().getSize();

            List<Integer> slotList = new ArrayList<>(this.getSlots());
            int[] slots = new int[slotList.size()];
            for (int i = 0; i < slotList.size(); i++) {
                int slot = slotList.get(i);
                if (!this.isPermanent) {
                    slot -= ((this.getPage() - 1) * inventorySize);
                }
                slots[i] = slot;
            }
            perfDebug.end();

            perfDebug.start("onRender.displayFinalButton." + this.getName());
            inventoryEngine.displayFinalButton(this, new Placeholders(), slots);
            perfDebug.end();
        }
    }

    /**
     * Called when the left mouse button is clicked
     *
     * @param player    the player
     * @param event     the inventory click event
     * @param inventory the inventory
     * @param slot      the slot
     */
    public void onLeftClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot) {
    }

    /**
     * Called when the right mouse button is clicked
     *
     * @param player    the player
     * @param event     the inventory click event
     * @param inventory the inventory
     * @param slot      the slot
     */
    public void onRightClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot) {
    }

    public void onAnvilTextChange(@NotNull Player player, AnvilInventoryEngine inventoryEngine, TextChange textChange, Placeholders placeholders) {}

    /**
     * Called when the middle mouse button is clicked
     *
     * @param player    the player
     * @param event     the inventory click event
     * @param inventory the inventory
     * @param slot      the slot
     */
    public void onMiddleClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot) {
    }

    /**
     * Called when the inventory is closed
     *
     * @param player    the player
     * @param inventory the inventory
     */
    public void onInventoryClose(@NotNull Player player, @NotNull InventoryEngine inventory) {
    }

    @Contract(pure = true)
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot, @NotNull Placeholders placeholders) {
        AtomicBoolean isSuccess = this.handleClickCommon(player, inventory, event.getClick(), placeholders, true);
        this.options.forEach(option -> option.onClick(this, player, event, inventory, slot, isSuccess.get()));

        if (isSuccess.get()) {
            this.execute(this.plugin, event.getClick(), placeholders, player);
        }
    }

    /**
     * Used for bedrock inventory click, because bedrock inventory click event doesn't have click type, so we need to use this method to handle the click.
     * @param player the player
     * @param inventory the inventory
     * @param slot the slot
     * @param placeholders the placeholders
     */
    public void onClick(@NotNull Player player, @NotNull InventoryEngine inventory, int slot, @NotNull Placeholders placeholders) {
        ClickType clickType = ClickType.LEFT; // Default to left click for this method
        AtomicBoolean isSuccess = this.handleClickCommon(player, inventory, clickType, placeholders, false);

        if (isSuccess.get()) {
            this.execute(this.plugin, clickType, placeholders, player);
        }
    }

    private AtomicBoolean handleClickCommon(@NotNull Player player, @NotNull InventoryEngine inventory, @NotNull ClickType clickType, @NotNull Placeholders placeholders, boolean clickRequirementsCheck) {
        if (this.closeInventory()) {
            player.closeInventory();
        }

        if (!this.datas.isEmpty()) {
            DataManager dataManager = this.plugin.getDataManager();
            for (ActionPlayerData actionPlayerData : this.datas) {
                actionPlayerData.execute(player, dataManager, placeholders);
            }
        }

        if (!this.messages.isEmpty()) {
            if (this.openLink != null) {
                this.openLink.send(player, this.messages);
            } else {
                this.messages.forEach(message -> this.plugin.getMetaUpdater().sendMessage(player, this.plugin.parse(player, placeholders.parse(message))));
            }
        }

        if (this.soundOption != null) {
            this.soundOption.play(player);
        }

        AtomicBoolean isSuccess = new AtomicBoolean(true);

        if (clickRequirementsCheck) {
            this.clickRequirements.forEach(requirement -> {
                if (requirement.getClickTypes().contains(clickType)) {
                    isSuccess.set(isSuccess.get() & requirement.execute(player, this, inventory, placeholders));
                }
            });
        } else {
            this.clickRequirements.forEach(requirement -> {
                isSuccess.set(isSuccess.get() & requirement.execute(player, this, inventory, placeholders));
            });
        }

        if (isSuccess.get()) {
            for (Action action : this.actions) {
                if (action.preExecuteChain(player, this, inventory, placeholders) == ActionResult.STOP) {
                    break;
                }
            }
        }

        return isSuccess;
    }

    /**
     * Called when the inventory is opened
     *
     * @param player       the player
     * @param inventory    the inventory
     * @param placeholders the placeholders
     */
    public void onInventoryOpen(@NotNull Player player, @NotNull InventoryEngine inventory, @NotNull Placeholders placeholders) {

    }

    @Contract(pure = true)
    public boolean closeInventory() {
        return this.closeInventory;
    }

    @Contract("_ -> this")
    @NotNull
    public Button setButtonName(String buttonName) {
        this.buttonName = buttonName;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public Button setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
        return this;
    }

    @Contract("_ -> this")
    @NotNull
    public Button setSoundOption(SoundOption soundOption) {
        this.soundOption = soundOption;
        return this;
    }

    @Contract(pure = true)
    public OpenLink getOpenLink() {
        return this.openLink;
    }

    public void setOpenLink(OpenLink openLink) {
        this.openLink = openLink;
    }

    @Contract(pure = true)
    public boolean isUpdated() {
        return this.isUpdated;
    }

    public void setUpdated(boolean isUpdated) {
        this.isUpdated = isUpdated;
    }

    @Contract(pure = true)
    public boolean isRefreshOnClick() {
        return this.refreshOnClick;
    }

    public void setRefreshOnClick(boolean refreshOnClick) {
        this.refreshOnClick = refreshOnClick;
    }

    @Contract(pure = true)
    @Nullable
    public boolean isRefreshOnDrag() {
        return this.refreshOnDrag;
    }

    public void setRefreshOnDrag(boolean refreshOnDrag) {
        this.refreshOnDrag = refreshOnDrag;
    }

    @Contract(pure = true)

    public List<ActionPlayerData> getData() {
        return this.datas;
    }

    public void setDatas(@Nullable List<ActionPlayerData> datas) {
        this.datas = datas;
    }

    public void setPlugin(@Nullable MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Contract(pure = true)
    public boolean updateOnClick() {
        return this.updateOnClick;
    }

    public void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    @Contract(pure = true)
    public List<String> buildLore(Player player) {
        return this.itemStack.getLore();
    }

    @Contract(pure = true)
    public String buildDisplayName(Player player) {
        return this.itemStack.getDisplayName();
    }

    /**
     * Called when the back button is clicked
     *
     * @param player         the player
     * @param event          the inventory click event
     * @param inventory      the inventory
     * @param oldInventories the old inventories
     * @param toInventory    the to inventory
     * @param slot           the slot
     */
    public void onBackClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, @NotNull List<Inventory> oldInventories, @NotNull Inventory toInventory, int slot) {
    }

    @Contract(pure = true)
    @Nullable
    public List<Requirement> getClickRequirements() {
        return this.clickRequirements;
    }

    public void setClickRequirements(@Nullable List<Requirement> clickRequirements) {
        this.clickRequirements = clickRequirements;
    }

    @Contract(pure = true)
    @Nullable
    public Requirement getViewRequirement() {
        return this.viewRequirement;
    }

    public void setViewRequirement(@Nullable Requirement viewRequirement) {
        this.viewRequirement = viewRequirement;
    }

    @Contract(pure = true)
    @Override
    public boolean hasPermission() {
        return this.viewRequirement != null || super.hasPermission();
    }

    public boolean checkPermission(@NotNull Player player, @NotNull InventoryEngine inventory, @NotNull Placeholders placeholders) {
        PerformanceDebug perfDebug = inventory.getPerformanceDebug();

        perfDebug.start("checkPermission.permissions." + this.getName());
        boolean permissionResult = super.checkPermission(player, inventory, placeholders);
        perfDebug.end();

        if (!permissionResult) return false;

        if (this.viewRequirement != null) {
            perfDebug.start("checkPermission.viewRequirement." + this.getName());
            boolean viewResult = this.viewRequirement.execute(player, this, inventory, placeholders);
            perfDebug.end();
            return viewResult;
        }

        return true;
    }

    @Contract(pure = true)
    @NotNull
    public List<Action> getActions() {
        return this.actions;
    }

    public void setActions(@NotNull List<Action> actions) {
        this.actions = actions;
    }

    /**
     * Called when the inventory is dragged
     *
     * @param event            the inventory drag event
     * @param player           the player
     * @param inventoryDefault the inventory engine
     */
    public void onDrag(@NotNull InventoryDragEvent event, @NotNull Player player, @NotNull InventoryEngine inventoryDefault) {
    }

    /**
     * Called when the inventory is clicked
     *
     * @param event            the inventory click event
     * @param player           the player
     * @param inventoryDefault the inventory engine
     */
    public void onInventoryClick(@NotNull InventoryClickEvent event, @NotNull Player player, @NotNull InventoryEngine inventoryDefault) {
    }

    @Contract(pure = true)
    public boolean isUseCache() {
        return this.useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Contract(pure = true)
    @NotNull
    public List<ButtonOption> getOptions() {
        return this.options;
    }

    public void setOptions(@Nullable List<ButtonOption> options) {
        if (options == null) {
            options = new ArrayList<>();
        }
        this.options = options;
    }

    @Contract(pure = true)
    public boolean hasCustomRender() {
        return false;
    }

    @Contract(pure = true)
    public boolean isUpdatedMasterButton() {
        return this.isMasterButtonUpdated;
    }

    public void setMasterButtonUpdated(boolean masterButtonUpdated) {
        this.isMasterButtonUpdated = masterButtonUpdated;
    }

    @Contract(pure = true)
    public boolean isOpenAsync() {
        return this.isOpenAsync;
    }

    public void setOpenAsync(boolean openAsync) {
        this.isOpenAsync = openAsync;
    }

    public boolean hasRefreshRequirement() {
        return this.refreshRequirement != null;
    }

    @Contract(pure = true)
    @Nullable
    public RefreshRequirement getRefreshRequirement() {
        return this.refreshRequirement;
    }

    public void setRefreshRequirement(@Nullable RefreshRequirement refreshRequirement) {
        this.refreshRequirement = refreshRequirement;
    }

    @Contract(pure = true)
    public int getPriority() {
        return this.priority;
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
    protected <T> void paginate(@NotNull List<T> elements, @NotNull InventoryEngine inventory, @NotNull BiConsumer<Integer, T> consumer) {
        Pagination<T> pagination = new Pagination<>();
        elements = pagination.paginate(elements, this.slots.size(), inventory.getPage());

        for (int i = 0; i != Math.min(elements.size(), this.slots.size()); i++) {
            int slot = this.slots.get(i);
            T element = elements.get(i);
            consumer.accept(slot, element);
        }
    }

    /**
     * Returns whether this button is displayed in the player's inventory.
     *
     * @return true if this button is displayed in the player's inventory, false otherwise
     */
    @Contract(pure = true)
    public boolean isPlayerInventory() {
        return this.isInPlayerInventory;
    }

    /**
     * Sets whether this button is displayed in the player's inventory.
     *
     * @param inPlayerInventory true if this button is displayed in the player's inventory, false otherwise
     */
    public void setPlayerInventory(boolean inPlayerInventory) {
        this.isInPlayerInventory = inPlayerInventory;
    }

    /**
     * Returns whether this button allow draggable in the button slot.
     *
     * @return true if this button can allow drag in this button, false otherwise
     */
    public boolean isDraggable() {
        return false;
    }

    /**
     * Returns the button to display in the inventory. This method is called every
     * time the inventory is updated.
     * The default implementation of this method returns the button itself.
     *
     * @param inventoryEngine the inventory engine
     * @param player          the player to display the button for
     * @return the button to display
     */
    public Button getDisplayButton(InventoryEngine inventoryEngine, Player player) {
        return this;
    }

    /**
     * Builds the item stack for the given player, with the owner of the skull
     * being the given offline player, and the given placeholders.
     *
     * @param player       the player to build the item stack for
     * @param owner        the owner of the skull
     * @param placeholders the placeholders to use
     * @return the built item stack
     */
    protected ItemStack buildAsOwner(@NotNull Player player, @NotNull OfflinePlayer owner, @NotNull Placeholders placeholders) {
        ItemStack itemStack = this.getItemStack().build(player, false, placeholders);
        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
        skullMeta.setOwningPlayer(owner);
        itemStack.setItemMeta(skullMeta);
        return itemStack;
    }

    /**
     * Writes this button in the format the button loader reads. The options shared by every button
     * are written here, then {@link #serializeProperties} writes the ones specific to its type.
     * <p>
     * An else button is written relative to its parent, since the loader gives it its parent's
     * values as defaults.
     *
     * @param section       The section to write into.
     * @param inventorySize The size of the inventory the button is in, to write slots relative to their page.
     * @throws UnsupportedOperationException If this button, or something it holds, cannot be serialized.
     */
    public void serialize(@NotNull ConfigurationSection section, int inventorySize) {
        this.serializeProperties(section);

        Button parent = this.getParentButton();
        int pageOffset = (this.page - 1) * inventorySize;
        List<Integer> buttonSlots = new ArrayList<>(this.getSlots());
        if (buttonSlots.size() == 1) {
            section.set("slot", buttonSlots.getFirst() - pageOffset);
        } else if (!buttonSlots.isEmpty()) {
            section.set("slots", compressSlots(buttonSlots, pageOffset));
        }
        if (parent != null || this.page != 1) section.set("page", this.page);

        setIfDifferent(section, "is-permanent", this.isPermanent, parent != null && parent.isPermanent());
        setIfDifferent(section, "update-on-click", this.updateOnClick, parent != null && parent.updateOnClick());
        setIfDifferent(section, "close-inventory", this.closeInventory, false);
        setIfDifferent(section, "update", this.isUpdated, parent != null && parent.isUpdated());
        setIfDifferent(section, "update-master-button", this.isMasterButtonUpdated, parent != null && parent.isUpdatedMasterButton());
        setIfDifferent(section, "refresh-on-click", this.refreshOnClick, false);
        setIfDifferent(section, "refresh-on-drag", this.refreshOnDrag, false);
        setIfDifferent(section, "use-cache", this.useCache, parent == null || parent.isUseCache());
        setIfDifferent(section, "open-async", this.isOpenAsync, false);

        if (this.itemStack != null) this.itemStack.serialize(section.createSection("item"));
        if (this.playerHead != null) section.set("player-head", this.playerHead);
        setIfNotEmpty(section, "messages", this.getMessages());
        if (this.openLink != null) this.openLink.serialize(section.createSection("open-link"));

        if (this.soundOption != null) {
            Map<String, Object> sound = new LinkedHashMap<>();
            this.soundOption.serialize(sound);
            sound.forEach(section::set);
        }

        List<ActionPlayerData> datas = this.getData();
        if (datas != null && !datas.isEmpty()) {
            ConfigurationSection datasSection = section.createSection("datas");
            for (int index = 0; index < datas.size(); index++) {
                datas.get(index).serialize(datasSection.createSection(String.valueOf(index)));
            }
        }

        setIfNotEmpty(section, "permissions", serializePermissions(this.getPermissions()));
        setIfNotEmpty(section, "or-permissions", serializePermissions(this.getOrPermission()));

        List<Map<String, Object>> placeholders = new ArrayList<>();
        for (PlaceholderPermissible placeholder : this.getPlaceholders()) {
            placeholders.add(placeholder.serialize());
        }
        setIfNotEmpty(section, "placeholders", placeholders);

        setIfNotEmpty(section, "commands", this.getCommands());
        setIfNotEmpty(section, "left-commands", this.getLeftCommands());
        setIfNotEmpty(section, "right-commands", this.getRightCommands());
        setIfNotEmpty(section, "console-commands", this.getConsoleCommands());
        setIfNotEmpty(section, "console-right-commands", this.getConsoleRightCommands());
        setIfNotEmpty(section, "console-left-commands", this.getConsoleLeftCommands());
        setIfNotEmpty(section, "console-permission-commands", this.getConsolePermissionCommands());
        if (this.getConsolePermission() != null) section.set("console-permission", this.getConsolePermission());

        if (this.viewRequirement != null) this.viewRequirement.serialize(section.createSection("view-requirement"));
        List<Requirement> clickRequirements = this.getClickRequirements();
        if (clickRequirements != null && !clickRequirements.isEmpty()) {
            ConfigurationSection clickSection = section.createSection("click-requirement");
            for (Requirement requirement : clickRequirements) {
                requirement.serialize(clickSection.createSection(requirementKey(requirement, clickSection)));
            }
        }

        if (this.refreshRequirement != null) this.refreshRequirement.serialize(section.createSection("refresh-requirements"));
        setIfNotEmpty(section, "actions", Permissible.serializeActions(this.getActions()));

        for (ButtonOption option : this.getOptions()) {
            option.serialize(this, section);
        }

        if (this.getElseButton() != null) {
            this.getElseButton().serialize(section.createSection("else"), inventorySize);
        }
    }

    /**
     * Writes the options specific to this type of button, starting with its {@code type} key when
     * it is not the default {@code NONE}.
     *
     * @param section The section of the button.
     * @throws UnsupportedOperationException If this type of button cannot be serialized.
     */
    protected void serializeProperties(@NotNull ConfigurationSection section) {
        throw new UnsupportedOperationException("The button " + this.buttonName + " of type " + this.getClass().getName() + " cannot be serialized yet");
    }

    private static List<Object> compressSlots(List<Integer> slots, int pageOffset) {
        List<Object> compressed = new ArrayList<>();
        int index = 0;
        while (index < slots.size()) {
            int start = slots.get(index) - pageOffset;
            int end = start;
            while (index + 1 < slots.size() && slots.get(index + 1) - pageOffset == end + 1) {
                end++;
                index++;
            }
            compressed.add(start == end ? start : start + "-" + end);
            index++;
        }
        return compressed;
    }

    private static List<String> serializePermissions(List<PermissionPermissible> permissions) {
        List<String> serialized = new ArrayList<>(permissions.size());
        for (PermissionPermissible permission : permissions) {
            serialized.add(permission.isReverse() ? "!" + permission.getPermission() : permission.getPermission());
        }
        return serialized;
    }

    private static String requirementKey(Requirement requirement, ConfigurationSection clickSection) {
        List<ClickType> clickTypes = requirement.getClickTypes();
        String base = new HashSet<>(clickTypes).equals(new HashSet<>(Configuration.allClicksType)) ? "all"
                : clickTypes.size() == 1 ? clickTypes.getFirst().name().toLowerCase(Locale.ROOT) : "requirement";
        String key = base;
        for (int suffix = 2; clickSection.contains(key); suffix++) {
            key = base + "-" + suffix;
        }
        return key;
    }

    private static void setIfDifferent(ConfigurationSection section, String key, boolean value, boolean defaultValue) {
        if (value != defaultValue) section.set(key, value);
    }

    private static void setIfNotEmpty(ConfigurationSection section, String key, List<?> values) {
        if (values != null && !values.isEmpty()) section.set(key, values);
    }
}
