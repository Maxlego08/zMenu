package fr.maxlego08.menu.inventory.inventories;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.exceptions.InventoryOpenException;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Maxlego08
 */
public class InventoryDefault extends VInventory implements InventoryEngine {

    private final Map<Integer, TimerTask> timers = new HashMap<>();
    private Inventory inventory;
    private List<Inventory> oldInventories = new ArrayList<>();
    private List<Button> buttons = new ArrayList<>();
    private int maxPage = 1;
    private boolean isAsync = false;

    private List<Button> updatedButtons = new ArrayList<>();

    @Override
    public InventoryResult openInventory(ZMenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.inventory = (Inventory) args[0];

        InventoryResult result = this.inventory.openInventory(player, this);
        if (result != InventoryResult.SUCCESS) {
            return result;
        }

        this.oldInventories = (List<Inventory>) args[1];

        Collection<Pattern> patterns = this.inventory.getPatterns();

        this.maxPage = Math.max(1, this.inventory.getMaxPage(patterns, player, args));

        this.buttons = new ArrayList<>();
        this.buttons.addAll(patterns.stream().flatMap(pattern -> this.inventory.sortPatterns(pattern, page, args).stream()).toList());
        this.buttons.addAll(this.inventory.sortButtons(page, args));

        this.updatedButtons = this.buttons.stream().filter(Button::updateOnClick).collect(Collectors.toList());
        isAsync = this.buttons.stream().anyMatch(Button::isOpenAsync);
        InventoryManager manager = this.plugin.getInventoryManager();
        manager.setPlayerPage(player, page, maxPage);

        var scheduler = this.plugin.getScheduler();
        Consumer<WrappedTask> runnable = w -> {
            Placeholders placeholders = new Placeholders();
            for (Button button : this.buttons) {
                button.onInventoryOpen(player, this, placeholders);
            }

            String inventoryName = this.getMessage(this.inventory.getName(player, this, placeholders), "%page%", page, "%maxPage%", this.maxPage, "%max-page%", this.maxPage);
            Player targetPlayer = getTargetPlayer();

            if (this.inventory.getType() == InventoryType.CHEST) {
                super.createMetaInventory(super.papi(placeholders.parse(inventoryName), targetPlayer, false), this.inventory.size());
            } else {
                super.createMetaInventory(super.papi(placeholders.parse(inventoryName), targetPlayer, false), this.inventory.getType());
            }
            // Display fill items
            if (this.inventory.getFillItemStack() != null) {
                for (int a = 0; a != super.getSpigotInventory().getContents().length; a++) {
                    this.addItem(a, this.inventory.getFillItemStack().build(player));
                }
            }

            // Display buttons
            for (Button button : this.buttons) {
                buildButton(button);
            }

            if (isAsync) {
                scheduler.runAtEntity(player, w2 -> player.openInventory(this.getSpigotInventory()));
            }
        };

        if (isAsync) {

            scheduler.runAsync(runnable);
            return InventoryResult.SUCCESS_ASYNC;
        } else {

            runnable.accept(null);
            return InventoryResult.SUCCESS;
        }
    }

    @Override
    public void postOpen(ZMenuPlugin plugin, Player player, int page, Object[] objects) {
        this.inventory.postOpenInventory(player, this);
    }

    @Override
    protected void onClose(InventoryCloseEvent event, ZMenuPlugin plugin, Player player) {

        this.inventory.closeInventory(player, this);
        this.buttons.forEach(button -> button.onInventoryClose(player, this));
    }

    @Override
    protected void onDrag(InventoryDragEvent event, ZMenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onDrag(event, player, this));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, ZMenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onInventoryClick(event, player, this));
    }

    /**
     * Allows displaying a button
     *
     * @param button The button
     */
    @Override
    public void buildButton(Button button) {
        final Player targetPlayer = getTargetPlayer();
        if (button.hasCustomRender()) {
            button.onRender(targetPlayer, this);
            return;
        }

        button = button.getDisplayButton(this, this.player);

        // If the button has a permission or a placeholder to check
        if (button.hasPermission()) {

            // We will check if the player has the permission to display the
            // button
            if (!button.checkPermission(targetPlayer, this, new Placeholders())) {

                // If there is an ElseButton we will display it
                if (button.hasElseButton()) {

                    Button elseButton = button.getElseButton();
                    this.buildButton(elseButton);
                }
            } else {

                // If the player has the permission, the button
                this.displayButton(button);
            }

        } else {

            // If there is no permission, then the button
            this.displayButton(button);
        }
    }

    /**
     * Allows displaying the button in the inventory
     */
    @Override
    public void displayButton(Button button) {
        final Player targetPlayer = getTargetPlayer();
        if (button.hasSpecialRender()) {

            Consumer<WrappedTask> runnable = w -> button.onRender(targetPlayer, this);
            if (isAsync) plugin.getScheduler().runAtEntity(player, runnable);
            else runnable.accept(null);

        } else {

            Consumer<WrappedTask> runnable = w -> {
                int slot = button.getRealSlot(button.isPlayerInventory() ? 36 : this.inventory.size(), this.page);
                this.displayFinalButton(button, slot);
            };

            if (isAsync) plugin.getScheduler().runAtEntity(player, runnable);
            else runnable.accept(null);
        }
    }

    /**
     * Allows displaying the button and putting the actions on the clicks
     */
    @Override
    public void displayFinalButton(Button button, int... slots) {
        final Player targetPlayer = getTargetPlayer();
        ItemStack itemStack = button.getCustomItemStack(targetPlayer);
        for (int slot : slots) {

            if (slot < 0) {
                Logger.info("slot is negative ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            int maxSlotSize = button.isPlayerInventory() ? 36 : this.inventory.size();
            if (slot >= maxSlotSize) {
                Logger.info("slot is out of range ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            ItemButton itemButton = this.addItem(button.isPlayerInventory(), slot, itemStack);
            if (button.isClickable()) {
                itemButton.setClick(event -> {

                    if (event.getClick() == ClickType.DOUBLE_CLICK) return;

                    event.setCancelled(true);
                    button.onClick(this.player, event, this, slot, new Placeholders());

                    if (button.isRefreshOnClick()) {
                        this.cancel(slot);
                        this.buildButton(button.getMasterParentButton());
                    }

                    // Update buttons who need to be updated
                    this.updatedButtons.forEach(this::buildButton);
                });
                itemButton.setLeftClick(event -> button.onLeftClick(this.player, event, this, slot));
                itemButton.setRightClick(event -> button.onRightClick(this.player, event, this, slot));
                itemButton.setMiddleClick(event -> button.onMiddleClick(this.player, event, this, slot));
            }

            if (button.hasRefreshRequirement() || button.isUpdated()) {

                RefreshRequirement refreshRequirement = button.hasRefreshRequirement() ? button.getRefreshRequirement() : null;
                boolean needRefresh = refreshRequirement != null && refreshRequirement.needRefresh(player, button, this, new Placeholders());

                if (needRefresh || button.isUpdated()) {
                    long interval = refreshRequirement != null ? refreshRequirement.getUpdateInterval() : this.inventory.getUpdateInterval();
                    TimerTask timerTask = this.scheduleFix(this.plugin, interval, (task, canRun) -> {
                        if (!canRun) {
                            return;
                        }

                        if (this.isClose()) {
                            task.cancel();
                            return;
                        }

                        TimerTask tTask = this.timers.get(slot);
                        if (!task.equals(tTask)) {
                            task.cancel();
                            return;
                        }

                        if (refreshRequirement != null && refreshRequirement.canRefresh(player, button, this, new Placeholders())) {
                            this.cancel(slot);
                            updateItemMeta(itemStack, button, refreshRequirement, slot);
                        } else if (button.isUpdated()) {
                            handleUpdatedButton(button, itemStack, slot);
                        }
                    });

                    this.timers.put(slot, timerTask);
                }
            }
        }
    }

    @Override
    public void cancel(int slot) {
        TimerTask task = this.timers.remove(slot);
        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public Inventory getMenuInventory() {
        return inventory;
    }

    @Override
    public List<Inventory> getOldInventories() {
        return this.oldInventories == null ? new ArrayList<>() : this.oldInventories;
    }

    @Override
    public int getMaxPage() {
        return maxPage;
    }

    @Override
    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    @Override
    public List<Button> getButtons() {
        return buttons;
    }

    private void updateItemMeta(ItemStack itemStack, Button button, RefreshRequirement refreshRequirement, int slot) {
        final Player targetPlayer = getTargetPlayer();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = button.buildLore(targetPlayer);
        String displayName = button.buildDisplayName(targetPlayer);

        if (!lore.isEmpty() && refreshRequirement.isRefreshLore()) {
            this.plugin.getMetaUpdater().updateLore(itemMeta, papi(lore, targetPlayer, false), button.getItemStack().getLoreType());
        }

        if (displayName != null && refreshRequirement.isRefreshName()) {
            this.plugin.getMetaUpdater().updateDisplayName(itemMeta, papi(displayName, targetPlayer, false), targetPlayer);
        }

        itemStack.setItemMeta(itemMeta);
        this.getSpigotInventory().setItem(slot, itemStack);

        if (refreshRequirement.isRefreshButton()) {
            this.buildButton(button.getMasterParentButton());
        }
    }

    private void handleUpdatedButton(Button button, ItemStack itemStack, int slot) {
        final Player targetPlayer = getTargetPlayer();
        Button masterButton = button.getMasterParentButton();

        if (button.isUpdatedMasterButton()) {
            this.cancel(slot);
            this.buildButton(masterButton);
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = button.buildLore(targetPlayer);
        String displayName = button.buildDisplayName(targetPlayer);

        if (!lore.isEmpty()) {
            this.plugin.getMetaUpdater().updateLore(itemMeta, papi(lore, targetPlayer, false), button.getItemStack().getLoreType());
        }
        if (displayName != null) {
            this.plugin.getMetaUpdater().updateDisplayName(itemMeta, papi(displayName, targetPlayer, false), targetPlayer);
        }

        itemStack.setItemMeta(itemMeta);
        this.getSpigotInventory().setItem(slot, itemStack);
    }

    private Player getTargetPlayer(){
        if (inventory == null || player == null) {
            return this.player;
        }
        String targetName = papi(inventory.getTargetPlayerNamePlaceholder(), player, false);
        Player targetPlayer = Bukkit.getPlayer(targetName);
        return targetPlayer != null ? targetPlayer : this.player;
    }
}
