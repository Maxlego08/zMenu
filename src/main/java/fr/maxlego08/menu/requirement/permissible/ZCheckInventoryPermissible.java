package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class ZCheckInventoryPermissible extends Permissible {
    private final int slot;
    private final MenuItemStack menuItemStack;
    private final boolean requirePlayerItem;
    private final ItemStackSimilar itemStackSimilar;
    private final boolean isInPlayerInventory;
    private final boolean inSpigotInventory;

    public ZCheckInventoryPermissible(int slot, @Nullable MenuItemStack menuItemStack, boolean requirePlayerItem, boolean isInPlayerInventory, List<Action> denyActions, List<Action> successActions, ItemStackSimilar itemStackSimilar, boolean inSpigotInventory) {
        super(denyActions, successActions);
        this.slot = slot;
        this.menuItemStack = menuItemStack;
        this.requirePlayerItem = requirePlayerItem;
        this.isInPlayerInventory = isInPlayerInventory;
        this.itemStackSimilar = itemStackSimilar;
        this.inSpigotInventory = inSpigotInventory;
    }

    @Override
    public boolean hasPermission(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        ItemStack itemStackA;
        if (this.inSpigotInventory) {
            if (this.isInPlayerInventory) {
                itemStackA = player.getInventory().getItem(this.slot);
            } else {
                Inventory spigotInventory = inventoryEngine.getSpigotInventory();
                itemStackA = spigotInventory.getItem(this.slot);
            }
        } else {
            Map<Integer, ItemButton> items;
            if (this.isInPlayerInventory) {
                items = inventoryEngine.getPlayerInventoryItems();
            } else {
                items = inventoryEngine.getItems();
            }

            ItemButton itemButton = items.get(this.slot);
            if (itemButton == null) {
                return false;
            }

            itemStackA = itemButton.getDisplayItem();
        }

        if (itemStackA == null) {
            return false;
        }

        if (requirePlayerItem && inventoryEngine.getPlugin().getDupeManager().isDupeItem(itemStackA)) {
            return false;
        }

        if (this.menuItemStack == null) return false;
        ItemStack itemStackB = this.menuItemStack.build(player, false, placeholders);
        return this.itemStackSimilar.isSimilar(itemStackA, itemStackB);
    }

    @Override
    public boolean isValid() {
        return this.slot >= 0 && this.menuItemStack != null;
    }
}
