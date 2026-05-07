package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TakeItemAction extends Action {
    private final ItemVerification itemVerification;
    private final MenuItemStack menuItemStack;
    private final boolean useCache;
    private final int amount;

    public TakeItemAction(MenuItemStack menuItemStack, boolean useCache, int amount, ItemVerification itemVerification) {
        this.menuItemStack = menuItemStack;
        this.useCache = useCache;
        this.amount = amount;
        this.itemVerification = itemVerification;
    }

    @Override
    protected void execute(@NotNull Player player,
                           @Nullable Button button,
                           @NotNull InventoryEngine inventoryEngine,
                           @NotNull Placeholders placeholders) {

        if (this.menuItemStack == null) {
            return;
        }

        ItemStack targetItem = this.menuItemStack.build(player, this.useCache, placeholders);
        if (targetItem == null) {
            return;
        }
        int remaining = this.amount;

        PlayerInventory inventory = player.getInventory();

        for (int slot = 0; slot < 36 && remaining > 0; slot++) {

            ItemStack current = inventory.getItem(slot);
            if (current == null) continue;

            if (!matches(current, targetItem)) continue;

            int removed = Math.min(remaining, current.getAmount());
            current.setAmount(current.getAmount() - removed);
            remaining -= removed;

            if (current.getAmount() <= 0) {
                inventory.setItem(slot, null);
            }
        }
    }

    private boolean matches(@NotNull ItemStack item, @NotNull ItemStack target) {
        return switch (this.itemVerification) {

            case SIMILAR -> item.isSimilar(target);

            case MODELID -> {
                if (!item.hasItemMeta()) yield false;

                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasCustomModelData()) yield false;

                String modelID = String.valueOf(meta.getCustomModelData());
                yield modelID.equals(this.menuItemStack.getModelID());
            }
        };
    }
}