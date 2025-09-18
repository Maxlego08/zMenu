package fr.maxlego08.menu.api.engine;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ItemButton {

    private final int slot;
    private final ItemStack displayItem;
    private Map<ClickType, Consumer<InventoryClickEvent>> onClickType = new HashMap<>();
    private Consumer<InventoryClickEvent> onClick;
    
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

    public ItemButton setClick(ClickType clickType ,Consumer<InventoryClickEvent> onClick) {
        this.onClickType.put(clickType, onClick);
        return this;
    }

    public ItemButton setMiddleClick(Consumer<InventoryClickEvent> onMiddleClick) {
        this.onClickType.put(ClickType.MIDDLE, onMiddleClick);
        this.onClickType.put(ClickType.DROP, onMiddleClick);
        return this;
    }

    public ItemButton setLeftClick(Consumer<InventoryClickEvent> onLeftClick) {
        this.onClickType.put(ClickType.LEFT, onLeftClick);
        return this;
    }

    public ItemButton setShiftLeftClick(Consumer<InventoryClickEvent> onLeftClick) {
        this.onClickType.put(ClickType.SHIFT_LEFT, onLeftClick);
        return this;
    }

    public ItemButton setRightClick(Consumer<InventoryClickEvent> onRightClick) {
        this.onClickType.put(ClickType.RIGHT, onRightClick);
        return this;
    }

    public ItemButton setShiftRightClick(Consumer<InventoryClickEvent> onRightClick) {
        this.onClickType.put(ClickType.SHIFT_RIGHT, onRightClick);
        return this;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void onClick(InventoryClickEvent event) {
        if (this.onClick != null) {
            this.onClick.accept(event);
        }

        Consumer<InventoryClickEvent> consumer = onClickType.get(event.getClick());
        if (consumer != null) {
            consumer.accept(event);
        }
    }
}