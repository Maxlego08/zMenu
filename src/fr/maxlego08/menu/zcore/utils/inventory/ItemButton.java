package fr.maxlego08.menu.zcore.utils.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class ItemButton {

    private final int slot;
    private final ItemStack displayItem;
    private Consumer<InventoryClickEvent> onClick;
    private Consumer<InventoryClickEvent> onMiddleClick;
    private Consumer<InventoryClickEvent> onLeftClick;
    private Consumer<InventoryClickEvent> onRightClick;

    public ItemButton(ItemStack displayItem, int slot) {
        super();
        this.displayItem = displayItem;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemButton setClick(Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    public ItemButton setMiddleClick(Consumer<InventoryClickEvent> onMiddleClick) {
        this.onMiddleClick = onMiddleClick;
        return this;
    }

    /**
     * @param onLeftClick the onLeftClick to set
     */
    public ItemButton setLeftClick(Consumer<InventoryClickEvent> onLeftClick) {
        this.onLeftClick = onLeftClick;
        return this;
    }

    /**
     * @param onRightClick the onRightClick to set
     */
    public ItemButton setRightClick(Consumer<InventoryClickEvent> onRightClick) {
        this.onRightClick = onRightClick;
        return this;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    /**
     * Permet de gérer le click du joueur
     *
     * @param event
     */
    public void onClick(InventoryClickEvent event) {

        if (this.onClick != null) {
            this.onClick.accept(event);
        }

        if ((event.getClick().equals(ClickType.MIDDLE) || event.getClick().equals(ClickType.DROP))
                && this.onMiddleClick != null) {

            this.onMiddleClick.accept(event);

        } else if (event.getClick().equals(ClickType.RIGHT) && this.onRightClick != null) {

            this.onRightClick.accept(event);

        } else if (event.getClick().equals(ClickType.LEFT) && this.onLeftClick != null) {

            this.onLeftClick.accept(event);

        }
    }

}