package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.ZBuildContext;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.ItemVerification;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
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
    private final ResolvableInt amount;

    public TakeItemAction(MenuItemStack menuItemStack, boolean useCache, int amount, ItemVerification itemVerification) {
        this(menuItemStack, useCache, ResolvableInt.of(amount), itemVerification);
    }

    public TakeItemAction(MenuItemStack menuItemStack, boolean useCache, ResolvableInt amount, ItemVerification itemVerification) {
        this.menuItemStack = menuItemStack;
        this.useCache = useCache;
        this.amount = amount;
        this.itemVerification = itemVerification;
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders
    ) {
        if (this.menuItemStack == null) {
            this.debugLog("Menu item stack is null | player=%s", player.getName());
            return;
        }

        ItemStack targetItem = this.menuItemStack.build(
                player,
                this.useCache,
                placeholders
        );

        if (targetItem == null) {
            this.debugLog("Build failed - target item is null | player=%s", player.getName());
            return;
        }

        ZBuildContext context = new ZBuildContext.Builder()
                .player(player)
                .placeholders(placeholders)
                .build();

        int requestedAmount = Resolvable.resolveOrDefault(
                context,
                this.amount,
                1
        );

        if (requestedAmount <= 0) {
            this.debugLog(
                    "Invalid amount=%d | player=%s",
                    requestedAmount,
                    player.getName()
            );
            return;
        }

        this.debugLog(
                "Built target item | player=%s item=%s amount=%d",
                player.getName(),
                targetItem,
                requestedAmount
        );

        PlayerInventory inventory = player.getInventory();

        int remaining = requestedAmount;

        for (int slot = 0; slot < 36 && remaining > 0; slot++) {
            ItemStack current = inventory.getItem(slot);

            if (current == null || current.getAmount() <= 0) {
                continue;
            }

            boolean matches = this.matches(current, targetItem);

            this.debugLog(
                    "Slot %02d | item=%s match=%s",
                    slot,
                    current,
                    matches
            );

            if (!matches) {
                continue;
            }

            int toRemove = Math.min(remaining, current.getAmount());

            current.setAmount(current.getAmount() - toRemove);
            remaining -= toRemove;

            if (current.getAmount() <= 0) {
                inventory.setItem(slot, null);

                this.debugLog(
                        "Slot %02d cleared | player=%s",
                        slot,
                        player.getName()
                );
            }
        }

        this.logResult(player, requestedAmount, remaining);
    }

    private void logResult(
            @NotNull Player player,
            int requestedAmount,
            int remaining
    ) {
        if (!this.debug) {
            return;
        }

        int taken = requestedAmount - remaining;

        if (remaining > 0) {
            Logger.info(String.format(
                    "[TakeItem] Partial removal | player=%s taken=%d missing=%d",
                    player.getName(),
                    taken,
                    remaining
            ));
        } else {
            Logger.info(String.format(
                    "[TakeItem] Success | player=%s taken=%d",
                    player.getName(),
                    taken
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