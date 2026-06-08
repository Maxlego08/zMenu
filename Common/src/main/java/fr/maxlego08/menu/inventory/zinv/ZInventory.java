package fr.maxlego08.menu.inventory.zinv;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.*;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.common.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class ZInventory extends ZUtils implements ContainerInventorySetter {

    private final Plugin plugin;
    private final String name;
    private final String fileName;
    private final int size;
    private final List<Button> buttons;
    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private InventoryReplacement inventoryReplacement;
    private Map<String, String> translatedNames = new HashMap<>();
    private List<Pattern> patterns = new ArrayList<>();
    private MenuItemStack fillItemStack;
    private int updateInterval;
    private File file;
    private boolean clearInventory;
    private ClearInvType clearInvType = ClearInvType.DEFAULT;
    private boolean ItemPickupDisabled;
    private boolean isClickLimiterEnabled = true;
    private Requirement openRequirement;
    private OpenWithItem openWithItem;
    private InventoryType type = InventoryType.CHEST;
    private String targetPlayerNamePlaceholder = null;
    private TitleAnimation titleAnimation;
    private List<Action> openActions = new ArrayList<>();
    private List<Action> closeActions = new ArrayList<>();

    /**
     * @param plugin   The plugin where the inventory comes from
     * @param name     Inventory name
     * @param fileName Inventory file name
     * @param size     Inventory size
     * @param buttons  List of {@link Button}
     */
    public ZInventory(Plugin plugin, String name, String fileName, int size, List<Button> buttons) {
        super();
        this.plugin = plugin;
        this.name = name;
        this.fileName = fileName;
        this.size = size;
        this.buttons = buttons;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getName(@NotNull Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {

        if (!this.conditionalNames.isEmpty()) {
            ConditionalName selected = null;
            int highestPriority = Integer.MIN_VALUE;
            for (ConditionalName conditionalName : this.conditionalNames) {
                if (conditionalName.hasPermission(player, null, inventoryDefault, placeholders)) {
                    if (selected == null || conditionalName.priority() > highestPriority) {
                        selected = conditionalName;
                        highestPriority = conditionalName.priority();
                    }
                }
            }

            if (selected != null) {
                return selected.name();
            }
        }

        String locale = this.findPlayerLocale(player);
        return locale == null ? this.name : this.translatedNames.getOrDefault(locale, this.name);
    }

    @Override
    public InventoryType getType() {
        return this.type;
    }

    @Override
    public void setType(@NotNull InventoryType type) {
        this.type = type;
    }

    @Override
    public boolean shouldCancelItemPickup() {
        return this.ItemPickupDisabled;
    }

    @Override
    public void setCancelItemPickup(boolean ItemPickupDisabled) {
        this.ItemPickupDisabled = ItemPickupDisabled;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public Collection<Button> getButtons() {
        return Collections.unmodifiableCollection(this.buttons);
    }

    @Override
    public <T extends Button> List<T> getButtons(Class<T> type) {
        List<T> filtered = new ArrayList<>();
        for (Button button : this.getButtons()) {
            if (type.isAssignableFrom(button.getClass())) {
                filtered.add(type.cast(button));
            }
        }
        return filtered;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects) {
        List<Button> buttons = new ArrayList<>(this.buttons);
        patterns.forEach(pattern -> buttons.addAll(pattern.buttons()));

        int maxSlotInventory = -1;
        for (Button button : buttons) {
            if (!button.isPlayerInventory()) {
                maxSlotInventory = Math.max(maxSlotInventory, button.getSlot());
            }
        }
        int maxPageInventory = (maxSlotInventory >= 0) ? (maxSlotInventory / this.size) + 1 : 1;

        int maxSlotPlayerInventory = -1;
        for (Button button : buttons) {
            if (button.isPlayerInventory()) {
                maxSlotPlayerInventory = Math.max(maxSlotPlayerInventory, button.getSlot());
            }
        }
        int maxPagePlayerInventory = (maxSlotPlayerInventory >= 0) ? (maxSlotPlayerInventory / 36) + 1 : 1;

        final int maxPageFinal = Math.max(maxPageInventory, maxPagePlayerInventory);

        PaginateButton selected = null;
        int maxPaginationSize = Integer.MIN_VALUE;
        for (Button button : buttons) {
            if (button instanceof PaginateButton paginateButton) {
                int paginationSize = paginateButton.getPaginationSize(player);
                if (selected == null || paginationSize > maxPaginationSize) {
                    selected = paginateButton;
                    maxPaginationSize = paginationSize;
                }
            }
        }

        if (selected != null) {
            int slotsSize = selected.getSlots().size();
            if (slotsSize > 0) {
                int calculated = (int) Math.ceil((double) maxPaginationSize / slotsSize);
                return Math.max(maxPageFinal, calculated);
            }
        }
        return maxPageFinal;
    }

    @Override
    public List<Button> sortButtons(int page, Object... objects) {
        List<Button> sorted = new ArrayList<>();
        for (Button button : this.buttons) {
            int size = button.isPlayerInventory() ? 36 : this.size;
            int slot = button.getRealSlot(size, page);
            if (slot >= 0 && slot < size) {
                sorted.add(button);
            }
        }
        return sorted;
    }

    @Override
    public List<Button> sortPatterns(Pattern pattern, int page, Object... objects) {
        if (!pattern.enableMultiPage()) return new ArrayList<>(pattern.buttons());
        List<Button> sorted = new ArrayList<>();
        for (Button button : pattern.buttons()) {
            int slot = button.getRealSlot(this.size, page);
            if (slot >= 0 && slot < this.size) {
                sorted.add(button);
            }
        }
        return sorted;
    }

    @Override
    public InventoryResult openInventory(Player player, InventoryEngine inventoryDefault) {
        if (this.openRequirement != null && !this.openRequirement.execute(player, null, inventoryDefault, new Placeholders())) {
            return InventoryResult.PERMISSION;
        }

        InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
        InventoryHolder holder = CompatibilityUtil.getTopInventory(player).getHolder();

        if (holder instanceof InventoryDefault inventoryHolder) {
            this.clearPlayerInventoryButtons(player, inventoryHolder);

            if (inventoryHolder.getMenuInventory() instanceof ContainerInventory containerInventory && containerInventory.clearInventory() && !this.clearInventory) {
                inventoriesPlayer.giveInventory(player);
            } else if (this.clearInventory) {
                if (this.clearInvType == ClearInvType.DEFAULT){
                    inventoriesPlayer.storeInventory(player);
                } else {
                    inventoriesPlayer.storeInventoryTemporaryOrClear(player);
                }
            }
        } else if (this.clearInventory) {
            if (this.clearInvType == ClearInvType.DEFAULT) {
                inventoriesPlayer.storeInventory(player);
            } else {
                inventoriesPlayer.storeInventoryTemporary(player);
            }
        }

        var placeholders = new Placeholders();
        this.openActions.forEach(action -> action.preExecute(player, null, inventoryDefault, placeholders));

        return InventoryResult.SUCCESS;
    }

    private void clearPlayerInventoryButtons(Player player, InventoryEngine inventoryDefault) {
        for (Button button : inventoryDefault.getButtons()) {
            if (button.isPlayerInventory()) {
                for (int slot : button.getSlots()) {
                    if (slot >= 0 && slot <= 36) {
                        this.clearInvType.getOnButtonClear().accept(player, slot);
                    }
                }
            }
        }
    }

    @Override
    public void postOpenInventory(Player player, InventoryEngine inventoryDefault) {

    }

    @Override
    public void closeInventory(Player player, InventoryEngine inventoryDefault) {

        MenuPlugin menuPlugin = inventoryDefault.getPlugin();
        menuPlugin.getScheduler().runAtEntityLater(player, task -> {
            InventoryHolder newHolder = CompatibilityUtil.getTopInventory(player).getHolder();
            boolean isInNewzMenuInventory = newHolder instanceof InventoryDefault;
            if (newHolder != null && !(newHolder instanceof InventoryDefault)) {
                this.clearPlayerInventoryButtons(player, inventoryDefault);

                if (this.clearInventory) {
                    InventoriesPlayer inventoriesPlayer = menuPlugin.getInventoriesPlayer();
                    this.clearInvType.getOnInventoryClose().accept(inventoriesPlayer, player);
                }
            }
            var placeholders = new Placeholders();
            if (isInNewzMenuInventory) {
                for (Action action : this.closeActions) {
                    if (!Configuration.skipCloseActionsOnInventorySwitch.contains(action.getType())) {
                        action.preExecute(player, null, inventoryDefault, placeholders);
                    }
                }
            } else {
                this.closeActions.forEach(action -> action.preExecute(player, null, inventoryDefault, placeholders));
            }
        }, 1);

    }

    @Override
    public MenuItemStack getFillItemStack() {
        return this.fillItemStack;
    }

    @Override
    public void setFillItemStack(MenuItemStack fillItemStack) {
        this.fillItemStack = fillItemStack;
    }

    @Override
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    @Override
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return this.size;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean cleanInventory() {
        return this.clearInventory;
    }

    @Override
    public Requirement getOpenRequirement() {
        return this.openRequirement;
    }

    @Override
    public void setOpenRequirement(Requirement openRequirement) {
        this.openRequirement = openRequirement;
    }

    @Override
    public OpenWithItem getOpenWithItem() {
        return this.openWithItem;
    }

    @Override
    public void setOpenWithItem(OpenWithItem openWithItem) {
        this.openWithItem = openWithItem;
    }

    @Override
    public Map<String, String> getTranslatedNames() {
        return this.translatedNames;
    }

    @Override
    public void setTranslatedNames(Map<String, String> translatedNames) {
        this.translatedNames = translatedNames;
    }

    @Override
    public List<ConditionalName> getConditionalNames() {
        return this.conditionalNames;
    }

    @Override
    public void setClearInventory(boolean clearInventory) {
        this.clearInventory = clearInventory;
    }

    @Override
    public List<Pattern> getPatterns() {
        return this.patterns;
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String getTargetPlayerNamePlaceholder() {
        return this.targetPlayerNamePlaceholder;
    }

    @Override
    public void setTitleAnimation(TitleAnimation load) {
        this.titleAnimation = load;
    }

    @Override
    public TitleAnimation getTitleAnimation() {
        return this.titleAnimation;
    }

    @Override
    public void setTargetPlayerNamePlaceholder(String targetPlaceholder) {
        this.targetPlayerNamePlaceholder = targetPlaceholder;
    }

    @Override
    public List<Action> getOpenActions() {
        return this.openActions;
    }

    @Override
    public void setOpenActions(List<Action> openActions) {
        this.openActions = openActions;
    }

    @Override
    public List<Action> getCloseActions() {
        return this.closeActions;
    }

    @Override
    public void setCloseActions(List<Action> closeActions) {
        this.closeActions = closeActions;
    }

    @Override
    public ClearInvType getClearInvType() {
        return this.clearInvType;
    }

    @Override
    public boolean isClickLimiterEnabled() {
        return this.isClickLimiterEnabled;
    }

    @Override
    public @Nullable InventoryReplacement getInventoryReplacement() {
        return this.inventoryReplacement;
    }

    @Override
    public void setInventoryReplacement(InventoryReplacement inventoryReplacement) {
        this.inventoryReplacement = inventoryReplacement;
    }

    @Override
    public void setClickLimiterEnabled(boolean enabled) {
        this.isClickLimiterEnabled = enabled;
    }

    @Override
    public void setClearInvType(ClearInvType clearInvType) {
        this.clearInvType = clearInvType;
    }
}
