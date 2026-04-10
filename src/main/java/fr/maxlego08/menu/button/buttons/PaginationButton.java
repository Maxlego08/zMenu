package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.GenericPaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PaginationButton extends Button {

    protected final MenuPlugin plugin;
    protected final PaginationManager manager;
    protected final String contextId;
    protected final boolean onlyRefreshButton;

    public PaginationButton(@NotNull MenuPlugin plugin, @NotNull String contextId, boolean onlyRefreshButton) {
        this.plugin = plugin;
        this.manager = plugin.getInventoryManager().getPaginationManager();
        this.contextId = contextId;
        this.onlyRefreshButton = onlyRefreshButton;
    }

    @Nullable
    protected GenericPaginateButton findPaginateButton(@NotNull InventoryEngine inventory, @NotNull Player player) {
        for (Button button : inventory.getButtons()) {
            if (button instanceof GenericPaginateButton paginate && paginate.getContextId(player).equals(this.contextId)) {
                return paginate;
            }
        }
        return null;
    }

    protected void onPageChange(@NotNull Player player, @NotNull InventoryEngine inventory, GenericPaginateButton paginateButton) {
        if (this.onlyRefreshButton) {
            org.bukkit.inventory.ItemStack air = new org.bukkit.inventory.ItemStack(org.bukkit.Material.AIR);
            for (int i : paginateButton.getSlots()) {
                inventory.addItem(i, air, paginateButton.isPlayerInventory());
            }
            paginateButton.onRender(player, inventory);
        } else {
            refreshInventory(player);
        }
    }

    protected void refreshInventory(@NotNull Player player) {
        this.plugin.getInventoryManager().updateInventory(player);
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}