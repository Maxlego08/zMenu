package fr.maxlego08.menu.api.engine;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ItemButton {

    private final int slot;
    private final ItemStack displayItem;
    private final Map<ClickType, Consumer<InventoryClickEvent>> onClickType = new HashMap<>();
    private final boolean inPlayerInventory;
    private final BaseInventory baseInventory;
    private Consumer<InventoryClickEvent> onClick;

    public ItemButton(@NotNull ItemStack displayItem, int slot, boolean inPlayerInventory, @NotNull BaseInventory baseInventory) {
        this.displayItem = displayItem;
        this.slot = slot;
        this.inPlayerInventory = inPlayerInventory;
        this.baseInventory = baseInventory;
    }

    @Contract(pure = true)
    public int getSlot() {
        return slot;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setClick(@NotNull Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }

    @Contract("_,_-> this")
    @NotNull
    public ItemButton setClick(@NotNull ClickType clickType,@NotNull Consumer<InventoryClickEvent> onClick) {
        this.onClickType.put(clickType, onClick);
        return this;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setMiddleClick(@NotNull Consumer<InventoryClickEvent> onMiddleClick) {
        this.onClickType.put(ClickType.MIDDLE, onMiddleClick);
        this.onClickType.put(ClickType.DROP, onMiddleClick);
        return this;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setLeftClick(@NotNull Consumer<InventoryClickEvent> onLeftClick) {
        this.onClickType.put(ClickType.LEFT, onLeftClick);
        return this;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setShiftLeftClick(@NotNull Consumer<InventoryClickEvent> onLeftClick) {
        this.onClickType.put(ClickType.SHIFT_LEFT, onLeftClick);
        return this;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setRightClick(@NotNull Consumer<InventoryClickEvent> onRightClick) {
        this.onClickType.put(ClickType.RIGHT, onRightClick);
        return this;
    }

    @Contract("_-> this")
    @NotNull
    public ItemButton setShiftRightClick(@NotNull Consumer<InventoryClickEvent> onRightClick) {
        this.onClickType.put(ClickType.SHIFT_RIGHT, onRightClick);
        return this;
    }

    @Contract(pure = true)
    @NotNull
    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    @Contract(pure = true)
    public boolean isInPlayerInventory() {
        return this.inPlayerInventory;
    }

    @Contract(pure = true)
    @NotNull
    public BaseInventory getBaseInventory() {
        return this.baseInventory;
    }

    public void onClick(@NotNull InventoryClickEvent event) {
        if (this.onClick != null) {
            this.onClick.accept(event);
        }

        Consumer<InventoryClickEvent> consumer = onClickType.get(event.getClick());
        if (consumer != null) {
            consumer.accept(event);
        }
    }
}