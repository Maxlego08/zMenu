package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
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

        if (this.menuItemStack == null) return;

        ItemStack targetItem = this.menuItemStack.build(player, this.useCache, placeholders);

        if (targetItem == null) {
            debugLog("Build failed — target item is null | player=%s", player.getName());
            return;
        }

        debugLog("Built target item | player=%s item=%s", player.getName(), targetItem);

        int remaining = this.amount;
        PlayerInventory inventory = player.getInventory();

        for (int slot = 0; slot < 36 && remaining > 0; slot++) {
            ItemStack current = inventory.getItem(slot);
            if (current == null) continue;

            boolean matches = matches(current, targetItem);
            debugLog("Slot %02d | item=%-30s match=%s", slot, current, matches);
            if (!matches) continue;

            int toRemove = Math.min(remaining, current.getAmount());
            current.setAmount(current.getAmount() - toRemove);
            remaining -= toRemove;

            if (current.getAmount() <= 0) {
                debugLog("Slot %02d cleared | player=%s", slot, player.getName());
                inventory.setItem(slot, null);
            }
        }

        logResult(player, remaining);
    }

    private void logResult(@NotNull Player player, int remaining) {
        if (!this.debug) return;

        if (remaining > 0) {
            Logger.info(String.format(
                    "[TakeItem] Partial removal | player=%s  taken=%d  missing=%d",
                    player.getName(), this.amount - remaining, remaining
            ));
        } else {
            Logger.info(String.format(
                    "[TakeItem] Success         | player=%s  taken=%d",
                    player.getName(), this.amount
            ));
        }
    }

    private void debugLog(String format, Object... args) {
        if (this.debug) Logger.info("[TakeItem] " + String.format(format, args));
    }

    private boolean matches(@NotNull ItemStack item, @NotNull ItemStack target) {
        return switch (this.itemVerification) {

            case SIMILAR -> item.isSimilar(target);

            case MODELID -> {
                if (!item.hasItemMeta()) yield false;
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.hasCustomModelData()) yield false;
                yield String.valueOf(meta.getCustomModelData()).equals(this.menuItemStack.getModelID());
            }
        };
    }
}