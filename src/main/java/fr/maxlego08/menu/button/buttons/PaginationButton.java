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

    public PaginationButton(@NotNull MenuPlugin plugin, @NotNull String contextId) {
        this.plugin = plugin;
        this.manager = plugin.getInventoryManager().getPaginationManager();
        this.contextId = contextId;
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

    protected void refreshInventory(@NotNull Player player) {
        this.plugin.getInventoryManager().updateInventory(player);
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}