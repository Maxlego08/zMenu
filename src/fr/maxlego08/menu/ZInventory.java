package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.OpenWithItem;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZInventory extends ZUtils implements Inventory {

    private final Plugin plugin;
    private final String name;
    private final String fileName;
    private final int size;
    private final List<Button> buttons;
    private List<Pattern> patterns;
    private MenuItemStack fillItemStack;
    private int updateInterval;
    private File file;
    private boolean clearInventory;
    private Requirement openRequirement;
    private OpenWithItem openWithItem;

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
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public Collection<Button> getButtons() {
        return Collections.unmodifiableCollection(this.buttons);
    }

    @Override
    public <T extends Button> List<T> getButtons(Class<T> type) {
        return this.getButtons().stream().filter(e -> type.isAssignableFrom(e.getClass())).map(type::cast)
                .collect(Collectors.toList());
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
        List<Button> buttons = new ArrayList<>();
        buttons.addAll(this.buttons);
        buttons.addAll(patterns.stream().flatMap(pattern -> pattern.getButtons().stream()).collect(Collectors.toList()));

        Optional<Integer> optional = buttons.stream().map(Button::getSlot).max(Integer::compare);
        if (optional.isPresent()) {
            int maxSlot = optional.get();
            return (maxSlot / this.size) + 1;
        }
        return 1;
    }

    @Override
    public List<Button> sortButtons(int page, Object... objects) {
        return this.buttons.stream().filter(button -> {
            int slot = button.getRealSlot(this.size, page);
            return slot >= 0 && slot < this.size;
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

        if (openRequirement != null && !openRequirement.execute(player, null, inventoryDefault)) {
            return InventoryResult.PERMISSION;
        }

        return InventoryResult.SUCCESS;
    }

    @Override
    public void postOpenInventory(Player player, InventoryDefault inventoryDefault) {
        InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
        if (holder != null) {
            if (holder instanceof InventoryDefault) {
                InventoryDefault inventoryHolder = (InventoryDefault) holder;
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
    }

    @Override
    public void closeInventory(Player player, InventoryDefault inventoryDefault) {
        if (this.clearInventory) {
            MenuPlugin.getInstance().getScheduler().runTaskLater(player.getLocation(), 1, () -> {
                InventoryHolder newHolder = player.getOpenInventory().getTopInventory().getHolder();
                if (newHolder != null && !(newHolder instanceof InventoryDefault)) {
                    InventoriesPlayer inventoriesPlayer = inventoryDefault.getPlugin().getInventoriesPlayer();
                    inventoriesPlayer.giveInventory(player);
                }
            });
        }
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
