package fr.maxlego08.menu.inventory.inventories;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.InventoryResult;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.exceptions.InventoryOpenException;
import fr.maxlego08.menu.api.inventory.ChestInventory;
import fr.maxlego08.menu.api.inventory.ContainerInventory;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.api.requirement.RefreshRequirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.PerformanceDebug;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Maxlego08
 */
public class InventoryDefault extends VInventory implements InventoryEngine {

    private final Map<Integer, TimerTask> timers = new ConcurrentHashMap<>();
    private PerformanceDebug perfDebug;
    private Inventory inventory;
    private List<Inventory> oldInventories = new ArrayList<>();
    private List<Button> buttons = Collections.emptyList();
    private int maxPage = 1;
    private boolean isAsync = false;

    private List<Button> updatedButtons = Collections.emptyList();

    @Override
    public InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.inventory = (Inventory) args[0];
        this.perfDebug = PerformanceDebug.create("inventory:" + this.inventory.getFileName());

        this.perfDebug.start("openInventory.permissionCheck");
        InventoryResult result = this.inventory.openInventory(player, this);
        this.perfDebug.end();
        if (result != InventoryResult.SUCCESS) {
            return result;
        }

        if (this.inventory instanceof ContainerInventory containerInventory) {
            super.setClearInvType(containerInventory.getClearInvType());
            super.setClickLimiterEnabled(containerInventory.isClickLimiterEnabled());
        }

        this.oldInventories = this.extractOldInventories(args);

        Collection<Pattern> patterns = this.inventory.getPatterns();

        this.perfDebug.start("openInventory.getMaxPage");
        this.maxPage = Math.max(1, this.inventory.getMaxPage(patterns, player, args));
        this.perfDebug.end();

        List<Button> computedButtons = new ArrayList<>();
        this.perfDebug.start("openInventory.sortPatterns");
        for (Pattern pattern : patterns) {
            computedButtons.addAll(this.inventory.sortPatterns(pattern, page, args));
        }
        this.perfDebug.end();

        this.perfDebug.start("openInventory.sortButtons");
        computedButtons.addAll(this.inventory.sortButtons(page, args));
        this.perfDebug.end();
        this.buttons = computedButtons;

        List<Button> clickableUpdates = new ArrayList<>();
        boolean async = false;
        for (Button button : computedButtons) {
            if (button.updateOnClick()) {
                clickableUpdates.add(button);
            }
            if (!async && button.isOpenAsync()) {
                async = true;
            }
        }
        this.updatedButtons = clickableUpdates;
        this.isAsync = async;
        InventoryManager manager = this.plugin.getInventoryManager();
        manager.setPlayerPage(player, page, this.maxPage);

        var scheduler = this.plugin.getScheduler();
        Consumer<WrappedTask> runnable = w -> {
            Placeholders placeholders = new Placeholders();
            this.perfDebug.start("openInventory.onInventoryOpen");
            for (Button button : this.buttons) {
                button.onInventoryOpen(player, this, placeholders);
            }
            this.perfDebug.end();

            this.perfDebug.start("openInventory.resolveInventoryName");
            String inventoryName = this.plugin.getFontImage().replace(this.getMessage(this.inventory.getName(player, this, placeholders), "%page%", page, "%maxPage%", this.maxPage, "%max-page%", this.maxPage));
            Player targetPlayer = this.getTargetPlayer();
            this.perfDebug.end();

            this.perfDebug.start("openInventory.papiInventoryName");
            String parsedName = super.papi(placeholders.parse(inventoryName), targetPlayer, false);
            this.perfDebug.end();

            if (this.inventory instanceof ContainerInventory containerInventory) {
                this.perfDebug.start("openInventory.createMetaInventory");
                if (containerInventory.getType() == InventoryType.CHEST && containerInventory instanceof ChestInventory chestInventory) {
                    super.createMetaInventory(parsedName, chestInventory.size());
                } else {
                    super.createMetaInventory(parsedName, containerInventory.getType());
                }
                this.perfDebug.end();


                super.setTitleAnimation(containerInventory.getTitleAnimation());

                // Display fill items
                this.perfDebug.start("openInventory.fillItems");
                MenuItemStack fillItemStack = containerInventory.getFillItemStack();
                if (fillItemStack != null) {
                    ItemStack builtItem = fillItemStack.build(player);
                    if (builtItem != null) {
                        ItemStack[] contents = super.getSpigotInventory().getContents();
                        for (int slot = 0; slot < contents.length; slot++) {
                            this.addItem(slot, builtItem.clone());
                        }
                    }
                }
                this.perfDebug.end();
            }


            // Display buttons
            for (Button button : this.buttons) {
                this.perfDebug.start("buildButton." + button.getName());
                this.buildButton(button, placeholders);
                this.perfDebug.end();
            }

            this.perfDebug.printSummary();

            if (this.isAsync) {
                scheduler.runAtEntity(player, w2 -> player.openInventory(this.getSpigotInventory()));
            }
        };

        if (this.isAsync) {

            scheduler.runAsync(runnable);
            return InventoryResult.SUCCESS_ASYNC;
        } else {

            runnable.accept(null);
            return InventoryResult.SUCCESS;
        }
    }

    @Override
    public void postOpen(MenuPlugin plugin, Player player, int page, Object[] objects) {
        this.inventory.postOpenInventory(player, this);
    }

    @Override
    protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {

        this.inventory.closeInventory(player, this);
        this.buttons.forEach(button -> button.onInventoryClose(player, this));
    }

    @Override
    protected void onDrag(InventoryDragEvent event, MenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onDrag(event, player, this));
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, MenuPlugin plugin, Player player) {
        this.buttons.forEach(button -> button.onInventoryClick(event, player, this));
    }

    /**
     * Allows displaying a button
     *
     * @param button The button
     */
    @Override
    public void buildButton(Button button) {
        this.buildButton(button, new Placeholders());
    }

    @Override
    public void buildButton(Button button, @NonNull Placeholders placeholders) {
        if (button == null) {
            return;
        }
        final Player targetPlayer = this.getTargetPlayer();

        this.perfDebug.start("getDisplayButton." + button.getName());
        button = button.getDisplayButton(this, targetPlayer);
        this.perfDebug.end();
        if (button == null) {
            return;
        }

        // If the button has a permission or a placeholder to check
        if (button.hasPermission()) {

            this.perfDebug.start("checkPermission." + button.getName());
            boolean hasPermission = button.checkPermission(targetPlayer, this, placeholders);
            this.perfDebug.end();

            // We will check if the player has the permission to display the
            // button
            if (!hasPermission) {

                // If there is an ElseButton we will display it
                if (button.hasElseButton()) {

                    Button elseButton = button.getElseButton();
                    this.buildButton(elseButton, placeholders);
                }
            } else {

                // If the player has the permission, the button
                if (button.hasCustomRender()) {
                    this.perfDebug.start("onRender." + button.getName());
                    button.onRender(targetPlayer, this);
                    this.perfDebug.end();
                } else {
                    this.displayButton(button, placeholders);
                }
            }

        } else {

            // If there is no permission, then the button
            if (button.hasCustomRender()) {
                this.perfDebug.start("onRender." + button.getName());
                button.onRender(targetPlayer, this);
                this.perfDebug.end();
            } else {
                this.displayButton(button, placeholders);
            }
        }
    }

    /**
     * Allows displaying the button in the inventory
     */
    @Override
    public void displayButton(@NonNull Button button) {
        this.displayButton(button, new Placeholders());
    }

    @Override
    public void displayButton(@NotNull Button button, @NotNull Placeholders placeholders) {
        final Player targetPlayer = this.getTargetPlayer();
        var scheduler = this.plugin.getScheduler();
        Consumer<WrappedTask> runnable;
        if (button.hasSpecialRender()) {

            runnable = w -> {
                this.perfDebug.start("specialRender." + button.getName());
                button.onRender(targetPlayer, this);
                this.perfDebug.end();
            };

        } else {

            runnable = w -> {
                this.perfDebug.start("getRealSlot." + button.getName());
                int slot;
                if (button.isPlayerInventory()) {
                    slot = button.getRealSlot(36, this.page);
                } else {
                    if (this.inventory instanceof ChestInventory chestInventory) {
                        slot = button.getRealSlot(chestInventory.size(), this.page);
                    } else {
                        //noinspection deprecation
                        slot = button.getRealSlot(this.inventory.size(), this.page);
                    }
                }

                this.perfDebug.end();
                this.displayFinalButton(button, placeholders, slot);
            };

        }
        if (this.isAsync) scheduler.runAtEntity(this.player, runnable);
        else runnable.accept(null);
    }

    /**
     * Allows displaying the button and putting the actions on the clicks
     */
    @Override
    public void displayFinalButton(@NonNull Button button, int... slots) {
        this.displayFinalButton(button, new Placeholders(), slots);
    }

    @Override
    public void displayFinalButton(@NotNull Button button, @NotNull Placeholders placeholders, int... slots) {
        final Player targetPlayer = this.getTargetPlayer();

        this.perfDebug.start("getCustomItemStack." + button.getName());
        ItemStack itemStack = button.getCustomItemStack(targetPlayer, button.isUseCache(), placeholders);
        this.perfDebug.end();

        int maxSlotSize = button.isPlayerInventory() ? 36 : this.inventory.size();

        if (button.getPlayerHead() != null && itemStack.getItemMeta() instanceof SkullMeta) {
            this.plugin.getScheduler().runAsync(w -> {
                var updatedItemStack = this.plugin.getInventoryManager().postProcessSkullItemStack(itemStack, button, this.player, placeholders);
                for (int slot : slots) {
                    if (slot < 0 || slot >= maxSlotSize) continue;

                    this.getSpigotInventory().setItem(slot, updatedItemStack);
                }
            });
        }

        for (int slot : slots) {

            if (slot < 0) {
                Logger.info("slot is negative ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            if (slot >= maxSlotSize) {
                Logger.info("slot is out of range ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            this.perfDebug.start("addItem." + button.getName() + "[" + slot + "]");
            ItemButton itemButton = this.addItem(button.isPlayerInventory(), slot, itemStack);
            this.perfDebug.end();

            if (button.isClickable()) {
                itemButton.setClick(event -> {

                    if (event.getClick() == ClickType.DOUBLE_CLICK) return;

                    event.setCancelled(true);
                    button.onClick(this.player, event, this, slot, new Placeholders());

                    if (button.isRefreshOnClick()) {
                        this.cancel(slot);
                        this.buildButton(button.getMasterParentButton(), placeholders);
                    }

                    // Update buttons that need to be updated
                    this.updatedButtons.forEach(updatedButton -> this.buildButton(updatedButton, placeholders));
                });
                itemButton.setLeftClick(event -> button.onLeftClick(this.player, event, this, slot));
                itemButton.setRightClick(event -> button.onRightClick(this.player, event, this, slot));
                itemButton.setMiddleClick(event -> button.onMiddleClick(this.player, event, this, slot));
            }

            if (button.isDraggable()) {
                //If one button is draggable enable click
                super.setDisablePlayerInventoryClick(false);
            }

            if (button.hasRefreshRequirement() || button.isUpdated()) {

                RefreshRequirement refreshRequirement = button.hasRefreshRequirement() ? button.getRefreshRequirement() : null;
                this.perfDebug.start("refreshCheck." + button.getName() + "[" + slot + "]");
                boolean needRefresh = refreshRequirement != null && refreshRequirement.needRefresh(this.player, button, this, new Placeholders());
                this.perfDebug.end();

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

                        if (refreshRequirement != null && refreshRequirement.canRefresh(this.player, button, this, new Placeholders())) {
                            this.cancel(slot);
                            this.updateItemMeta(itemStack, button, refreshRequirement, slot, placeholders);
                        } else if (button.isUpdated()) {
                            this.handleUpdatedButton(button, itemStack, slot, placeholders);
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
    public @NotNull PerformanceDebug getPerformanceDebug() {
        return this.perfDebug != null ? this.perfDebug : PerformanceDebug.disabled();
    }

    @Override
    public Inventory getMenuInventory() {
        return this.inventory;
    }

    @Override
    public @NonNull List<Inventory> getOldInventories() {
        return this.oldInventories == null ? new ArrayList<>() : this.oldInventories;
    }

    @Override
    public int getMaxPage() {
        return this.maxPage;
    }

    @Override
    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    @Override
    public @NonNull List<Button> getButtons() {
        return this.buttons;
    }

    private void updateItemMeta(ItemStack itemStack, Button button, RefreshRequirement refreshRequirement, int slot, @NotNull Placeholders placeholders) {
        boolean updated = this.applyMetaUpdates(button, itemStack, refreshRequirement.isRefreshLore(), refreshRequirement.isRefreshName());
        if (!updated) {
            return;
        }

        this.getSpigotInventory().setItem(slot, itemStack);

        if (refreshRequirement.isRefreshButton()) {
            this.buildButton(button.getMasterParentButton(), placeholders);
        }
    }

    private void handleUpdatedButton(Button button, ItemStack itemStack, int slot, @NotNull Placeholders placeholders) {
        Button masterButton = button.getMasterParentButton();

        if (button.isUpdatedMasterButton()) {
            this.cancel(slot);
            this.buildButton(masterButton, placeholders);
            return;
        }

        if (!this.applyMetaUpdates(button, itemStack, true, true)) {
            return;
        }

        this.getSpigotInventory().setItem(slot, itemStack);
    }

    private boolean applyMetaUpdates(Button button, ItemStack itemStack, boolean refreshLore, boolean refreshName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }

        final Player targetPlayer = this.getTargetPlayer();
        if (targetPlayer == null) {
            return false;
        }

        if (refreshLore) {
            List<String> lore = button.buildLore(targetPlayer);
            if (!lore.isEmpty()) {
                this.plugin.getMetaUpdater().updateLore(itemMeta, this.papi(lore, targetPlayer, false), button.getItemStack().getLoreType());
            }
        }

        if (refreshName) {
            String displayName = button.buildDisplayName(targetPlayer);
            if (displayName != null) {
                this.plugin.getMetaUpdater().updateDisplayName(itemMeta, this.papi(displayName, targetPlayer, false), targetPlayer);
            }
        }

        itemStack.setItemMeta(itemMeta);
        return true;
    }

    private List<Inventory> extractOldInventories(Object[] args) {
        if (args.length < 2 || !(args[1] instanceof List<?> inventories)) {
            return new ArrayList<>();
        }
        @SuppressWarnings("unchecked")
        List<Inventory> castedInventories = (List<Inventory>) inventories;
        return new ArrayList<>(castedInventories);
    }

    private Player getTargetPlayer() {
        if (this.inventory == null || this.player == null) {
            return this.player;
        }
        String targetPlaceholder = this.inventory.getTargetPlayerNamePlaceholder();
        if (targetPlaceholder == null || targetPlaceholder.isEmpty()) {
            return this.player;
        }
        String targetName = this.papi(targetPlaceholder, this.player, false);
        if (targetName == null || targetName.isEmpty()) {
            return this.player;
        }
        Player targetPlayer = Bukkit.getPlayer(targetName);
        return targetPlayer != null ? targetPlayer : this.player;
    }
}
