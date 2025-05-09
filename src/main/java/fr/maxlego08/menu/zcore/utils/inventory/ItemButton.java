package fr.maxlego08.menu.zcore.utils.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Class used to create an item button
 *
 * @author Maxlego08
 */
public class ItemButton {

    private final int slot;
    private final ItemStack displayItem;
    private Consumer<InventoryClickEvent> onClick;
    private Consumer<InventoryClickEvent> onMiddleClick;
    private Consumer<InventoryClickEvent> onLeftClick;
    private Consumer<InventoryClickEvent> onRightClick;

    /**
     * @param displayItem the item stack display
     * @param slot        the slot of the item
     */
    public ItemButton(ItemStack displayItem, int slot) {
        super();
        this.displayItem = displayItem;
        this.slot = slot;
    }

    /**
     * @return the slot of the item
     */
    public int getSlot() {
        return slot;
    }

    /**
     * @param onClick the consumer to execute when the user click the item
     * @return the item button
     */
    public ItemButton setClick(Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    /**
     * @param onMiddleClick the consumer to execute when the user click the item with the
     *                      mouse wheel
     * @return the item button
     */
    public ItemButton setMiddleClick(Consumer<InventoryClickEvent> onMiddleClick) {
        this.onMiddleClick = onMiddleClick;
        return this;
    }

    /**
     * @param onLeftClick the consumer to execute when the user click the item with the
     *                    left mouse button
     * @return the item button
     */
    public ItemButton setLeftClick(Consumer<InventoryClickEvent> onLeftClick) {
        this.onLeftClick = onLeftClick;
        return this;
    }

    /**
     * @param onRightClick the consumer to execute when the user click the item with the
     *                     right mouse button
     * @return the item button
     */
    public ItemButton setRightClick(Consumer<InventoryClickEvent> onRightClick) {
        this.onRightClick = onRightClick;
        return this;
    }

    /**
     * @return the item stack display
     */
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * Method called when the player click the item
     *
     * @param event the inventory click event
     */
    public void onClick(InventoryClickEvent event) {

        if (this.onClick != null) {
            this.onClick.accept(event);
        }

        if ((event.getClick().equals(ClickType.MIDDLE) || event.getClick().equals(ClickType.DROP)) && this.onMiddleClick != null) {
            this.onMiddleClick.accept(event);
        } else if (event.getClick().equals(ClickType.RIGHT) && this.onRightClick != null) {
            this.onRightClick.accept(event);
        } else if (event.getClick().equals(ClickType.LEFT) && this.onLeftClick != null) {
            this.onLeftClick.accept(event);
        }
    }
}