package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class SetItemAction extends ActionHelper {
    private final List<Integer> slots;
    private final boolean inPlayerInventory;

    private final MenuItemStack menuItemStack;

    private final boolean dupeProtection;

    public SetItemAction(@NotNull List<Integer> slots, boolean inPlayerInventory, MenuItemStack menuItemStack, boolean dupeProtection) {
        this.slots = slots;
        this.inPlayerInventory = inPlayerInventory;
        this.menuItemStack = menuItemStack;
        this.dupeProtection = dupeProtection;
    }


    @Override
    protected void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        ItemStack itemStack = this.menuItemStack.build(player);


        if (dupeProtection) {
            itemStack = inventoryEngine.getPlugin().getDupeManager().protectItem(itemStack);
        }

        for (int slot : slots) {
            if (inPlayerInventory) {
                Map<Integer, ItemButton> playerInventoryItems = inventoryEngine.getPlayerInventoryItems();
                if (playerInventoryItems.containsKey(slot)) {
                    playerInventoryItems.get(slot).updateDisplayItem(itemStack);
                } else {
                    inventoryEngine.addItem(true, slot, itemStack, dupeProtection);
                }
            } else {
                Map<Integer, ItemButton> items = inventoryEngine.getItems();
                if (items.containsKey(slot)) {
                    items.get(slot).updateDisplayItem(itemStack);
                } else {
                    inventoryEngine.addItem(false, slot, itemStack, dupeProtection);
                }
            }
        }
    }
}
