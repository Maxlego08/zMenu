package fr.maxlego08.menu.inventory.inventories;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.pattern.Pattern;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * @author Maxlego08
 */
public class InventoryDefault extends VInventory {

    private final Map<Integer, TimerTask> timers = new HashMap<>();
    private Inventory inventory;
    private List<Inventory> oldInventories;
    private List<Button> buttons;
    private int maxPage = 1;

    private List<Button> updatedButtons = new ArrayList<>();

    @Override
    public InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args) throws InventoryOpenException {

        this.inventory = (Inventory) args[0];

        InventoryResult result = this.inventory.openInventory(player, this);
        if (result != InventoryResult.SUCCESS) {
            return result;
        }

        this.oldInventories = (List<Inventory>) args[1];

        this.maxPage = this.inventory.getMaxPage(player, args);

        Collection<Pattern> patterns = this.inventory.getPatterns();

        this.buttons = this.inventory.sortButtons(page, args);
        this.buttons.addAll(patterns.stream().flatMap(e -> e.getButtons().stream()).collect(Collectors.toList()));
        this.buttons.forEach(button -> button.onInventoryOpen(player, this));

        this.updatedButtons = this.buttons.stream().filter(Button::updateOnClick).collect(Collectors.toList());

        // Create inventory
        String inventoryName = this.getMessage(this.inventory.getName(), "%page%", page, "%maxPage%", this.maxPage);
        super.createInventory(super.papi(super.color(inventoryName), player), this.inventory.size());

        // Display fill items
        if (this.inventory.getFillItemStack() != null) {
            for (int a = 0; a != super.getSpigotInventory().getContents().length; a++) {
                this.addItem(a, this.inventory.getFillItemStack().build(player));
            }
        }

        // Display buttons
        this.buttons.forEach(this::buildButton);

        return InventoryResult.SUCCESS;
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

    /**
     * Allows to display a button
     *
     * @param button The button
     */
    public void buildButton(Button button) {

        // If the button has a permission or a placeholder to check
        if (button.hasPermission()) {

            // We will check if the player has the permission to display the
            // button
            if (!button.checkPermission(this.player, this)) {

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
     * Allows to display the button in the inventory
     *
     * @param button
     */
    private void displayButton(Button button) {

        if (button.hasSpecialRender()) {

            button.onRender(player, this);

        } else {

            this.displayFinalButton(button, button.getRealSlot(this.inventory.size(), this.page));
        }
    }

    /**
     * Allows to display the button and to put the actions on the clicks
     *
     * @param button
     * @param slots
     */
    public void displayFinalButton(Button button, int... slots) {

        ItemStack itemStack = button.getCustomItemStack(this.player);
        for (int slot : slots) {

            if (slot < 0) {
                Logger.info("slot is negative ! (" + slot + ") Button: " + button.getName() + " in inventory " + this.inventory.getFileName(), Logger.LogType.ERROR);
                continue;
            }

            ItemButton itemButton = this.addItem(slot, itemStack);
            if (button.isClickable()) {
                itemButton.setClick(event -> {

                    if (event.getClick() == ClickType.DOUBLE_CLICK) return;

                    event.setCancelled(true);
                    button.onClick(this.player, event, this, slot);
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

            if (button.isUpdated()) {

                TimerTask timerTask = this.scheduleFix(this.plugin, this.inventory.getUpdateInterval() * 1000L, (task, canRun) -> {

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

                    Button masterButton = button.getMasterParentButton();
                    if (!masterButton.checkPermission(this.player, this)) {
                        this.cancel(slot);
                        this.buildButton(masterButton);
                        return;
                    }

                    ItemMeta itemMeta = itemStack.getItemMeta();

                    List<String> lore = button.buildLore(this.player);
                    String displayName = button.buildDisplayName(this.player);

                    if (!lore.isEmpty()) Meta.meta.updateLore(itemMeta, lore, this.player);
                    if (displayName != null) Meta.meta.updateDisplayName(itemMeta, displayName, this.player);

                    itemStack.setItemMeta(itemMeta);
                    this.getSpigotInventory().setItem(slot, itemStack);
                });

                this.timers.put(slot, timerTask);
            }
        }

    }

    public void cancel(int slot) {
        TimerTask task = this.timers.get(slot);
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * @return the inventory
     */
    public @NotNull Inventory getMenuInventory() {
        return inventory;
    }

    /**
     * @return the oldInventories
     */
    public List<Inventory> getOldInventories() {
        return oldInventories;
    }

    /**
     * @return the maxPage
     */
    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
