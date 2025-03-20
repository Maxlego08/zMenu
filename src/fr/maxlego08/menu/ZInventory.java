package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.PaginateButton;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.requirement.ConditionalName;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZInventory extends ZUtils implements Inventory {

    private final Plugin plugin;
    private final String name;
    private final String fileName;
    private final int size;
    private final List<Button> buttons;
    private Map<String, String> translatedNames = new HashMap<>();
    private List<Pattern> patterns = new ArrayList<>();
    private MenuItemStack fillItemStack;
    private int updateInterval;
    private File file;
    private boolean clearInventory;
    private Requirement openRequirement;
    private OpenWithItem openWithItem;
    private InventoryType type = InventoryType.CHEST;
    private List<ConditionalName> conditionalNames = new ArrayList<>();

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
    public String getName(Player player, InventoryDefault inventoryDefault, Placeholders placeholders) {

        if (!this.conditionalNames.isEmpty()) {
            Optional<ConditionalName> optional = this.conditionalNames.stream().filter(conditionalName -> conditionalName.hasPermission(player, null, inventoryDefault, placeholders)).max(Comparator.comparingInt(ConditionalName::getPriority));

            if (optional.isPresent()) {
                ConditionalName conditionalName = optional.get();
                return conditionalName.getName();
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
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public Collection<Button> getButtons() {
        return Collections.unmodifiableCollection(this.buttons);
    }

    @Override
    public <T extends Button> List<T> getButtons(Class<T> type) {
        return this.getButtons().stream().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast).collect(Collectors.toList());
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    @Deprecated
    public int getMaxPage(Player player, Object... objects) {
        Optional<Integer> optional = this.buttons.stream().map(Button::getSlot).max(Integer::compare);
        if (optional.isPresent()) {
            int maxSlot = optional.get();
            return (maxSlot / this.size) + 1;
        }
        return 1;
    }

    @Override
    public int getMaxPage(Collection<Pattern> patterns, Player player, Object... objects) {

        List<Button> buttons = new ArrayList<>(this.buttons);
        patterns.forEach(pattern -> buttons.addAll(pattern.getButtons()));

        int maxSlotInventory = buttons.stream().filter(button -> !button.isPlayerInventory()).mapToInt(Button::getSlot).max().orElse(-1);
        int maxPageInventory = (maxSlotInventory >= 0) ? (maxSlotInventory / this.size) + 1 : 1;

        int maxSlotPlayerInventory = buttons.stream().filter(Button::isPlayerInventory).mapToInt(Button::getSlot).max().orElse(-1);
        int maxPagePlayerInventory = (maxSlotPlayerInventory >= 0) ? (maxSlotPlayerInventory / 36) + 1 : 1;

        final int maxPageFinal = Math.max(maxPageInventory, maxPagePlayerInventory);

        return this.buttons.stream().filter(PaginateButton.class::isInstance).map(PaginateButton.class::cast).max(Comparator.comparingInt(button -> button.getPaginationSize(player))).map(paginateButton -> Math.max(maxPageFinal, (int) Math.ceil((double) paginateButton.getPaginationSize(player) / paginateButton.getSlots().size()))).orElse(maxPageFinal);
    }

    @Override
    public List<Button> sortButtons(int page, Object... objects) {
        return this.buttons.stream().filter(button -> {
            int size = button.isPlayerInventory() ? 36 : this.size;
            int slot = button.getRealSlot(size, page);
            return slot >= 0 && slot < size;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Button> sortPatterns(Pattern pattern, int page, Object... objects) {
        if (!pattern.enableMultiPage()) return new ArrayList<>(pattern.getButtons());
        return pattern.getButtons().stream().filter(button -> {
            int slot = button.getRealSlot(this.size, page);
            return slot >= 0 && slot < this.size;
        }).collect(Collectors.toList());
    }

    @Override
    public InventoryResult openInventory(Player player, InventoryDefault inventoryDefault) {

        if (this.openRequirement != null && !this.openRequirement.execute(player, null, inventoryDefault, new Placeholders())) {
            return InventoryResult.PERMISSION;
        }

        org.bukkit.inventory.Inventory topInventory = CompatibilityUtil.getTopInventory(player);
        InventoryHolder holder = topInventory.getHolder();

        if (holder != null) {
            if (holder instanceof InventoryDefault) {
                InventoryDefault inventoryHolder = (InventoryDefault) holder;

                clearPlayerInventoryButtons(player, inventoryHolder);

                if (inventoryHolder.getMenuInventory().cleanInventory()) {
                    InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
                    inventoriesPlayer.storeInventory(player);
                }
            } else {
                if (this.clearInventory) {
                    InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
                    inventoriesPlayer.storeInventory(player);
                }
            }
        }

        return InventoryResult.SUCCESS;
    }

    private void clearPlayerInventoryButtons(Player player, InventoryDefault inventoryDefault) {
        for (Button button : getButtons()) {
            if (button.isPlayerInventory()) {
                int slot = button.getRealSlot(36, inventoryDefault.getPage());
                if (slot >= 0 && slot <= 36) {
                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                }
            }
        }
    }

    @Override
    public void postOpenInventory(Player player, InventoryDefault inventoryDefault) {

    }

    @Override
    public void closeInventory(Player player, InventoryDefault inventoryDefault) {

        MenuPlugin.getInstance().getScheduler().runTaskLater(player.getLocation(), 1, () -> {
            InventoryHolder newHolder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (newHolder != null && !(newHolder instanceof InventoryDefault)) {

                clearPlayerInventoryButtons(player, inventoryDefault);

                if (this.clearInventory) {
                    InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
                    inventoriesPlayer.giveInventory(player);
                }
            }
        });
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

    public void setConditionalNames(List<ConditionalName> conditionalNames) {
        this.conditionalNames = conditionalNames;
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
}
