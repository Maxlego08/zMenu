package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class ZInventory extends ZUtils implements Inventory {

    private final Plugin plugin;
    private final String name;
    private final String fileName;
    private final int size;
    private final List<Button> buttons;
    private final List<ConditionalName> conditionalNames = new ArrayList<>();
    private Map<String, String> translatedNames = new HashMap<>();
    private List<Pattern> patterns = new ArrayList<>();
    private MenuItemStack fillItemStack;
    private int updateInterval;
    private File file;
    private boolean clearInventory;
    private boolean ItemPickupDisabled;
    private Requirement openRequirement;
    private OpenWithItem openWithItem;
    private InventoryType type = InventoryType.CHEST;
    private String targetPlayerNamePlaceholder;

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
    public String getName(Player player, InventoryEngine inventoryDefault, Placeholders placeholders) {

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

        String locale = findPlayerLocale(player);
        return locale == null ? this.name : this.translatedNames.getOrDefault(locale, this.name);
    }

    @Override
    public InventoryType getType() {
        return type;
    }

    public void setType(InventoryType type) {
        this.type = type;
    }

    @Override
    public boolean shouldCancelItemPickup() {
        return ItemPickupDisabled;
    }

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
            clearPlayerInventoryButtons(player, inventoryHolder);

            if (inventoryHolder.getMenuInventory().cleanInventory() && !this.clearInventory) {
                inventoriesPlayer.giveInventory(player);
            } else if (this.clearInventory) {
                inventoriesPlayer.storeInventory(player);
            }
        } else if (this.clearInventory) {
            inventoriesPlayer.storeInventory(player);
        }

        return InventoryResult.SUCCESS;
    }

    private void clearPlayerInventoryButtons(Player player, InventoryEngine inventoryDefault) {
        for (Button button : inventoryDefault.getButtons()) {
            if (button.isPlayerInventory()) {
                for (int slot : button.getSlots()) {
                    if (slot >= 0 && slot <= 36) {
                        player.getInventory().setItem(slot, new ItemStack(Material.AIR));
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

        ZMenuPlugin.getInstance().getScheduler().runAtEntityLater(player, task -> {
            InventoryHolder newHolder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (newHolder != null && !(newHolder instanceof InventoryDefault)) {

                clearPlayerInventoryButtons(player, inventoryDefault);

                if (this.clearInventory) {
                    InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
                    inventoriesPlayer.giveInventory(player);
                }
            }
        }, 1);
    }

    @Override
    public MenuItemStack getFillItemStack() {
        return this.fillItemStack;
    }

    public void setFillItemStack(MenuItemStack fillItemStack) {
        this.fillItemStack = fillItemStack;
    }

    @Override
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean cleanInventory() {
        return clearInventory;
    }

    @Override
    public Requirement getOpenRequirement() {
        return this.openRequirement;
    }

    public void setOpenRequirement(Requirement openRequirement) {
        this.openRequirement = openRequirement;
    }

    @Override
    public OpenWithItem getOpenWithItem() {
        return this.openWithItem;
    }

    public void setOpenWithItem(OpenWithItem openWithItem) {
        this.openWithItem = openWithItem;
    }

    @Override
    public Map<String, String> getTranslatedNames() {
        return translatedNames;
    }

    public void setTranslatedNames(Map<String, String> translatedNames) {
        this.translatedNames = translatedNames;
    }

    @Override
    public List<ConditionalName> getConditionalNames() {
        return this.conditionalNames;
    }

    public void setClearInventory(boolean clearInventory) {
        this.clearInventory = clearInventory;
    }

    @Override
    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public String getTargetPlayerNamePlaceholder() {
        return this.targetPlayerNamePlaceholder;
    }

    public void setTargetPlayerNamePlaceholder(String targetPlaceholder) {
        this.targetPlayerNamePlaceholder = targetPlaceholder;
    }
}
