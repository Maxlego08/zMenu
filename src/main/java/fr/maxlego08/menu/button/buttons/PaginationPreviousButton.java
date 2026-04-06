package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class PaginationPreviousButton extends PaginationButton {

    public PaginationPreviousButton(@NotNull MenuPlugin plugin, @NotNull String contextId) {
        super(plugin, contextId);
    }

    protected void onPreviousPage(@NotNull Player player, @NotNull InventoryEngine inventory) {
        refreshInventory(player);
    }

    protected void onCannotPreviousPage(@NotNull Player player, @NotNull InventoryEngine inventory) {
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot, @NotNull Placeholders placeholders) {
        int currentPage = this.manager.getPage(player.getUniqueId(), this.contextId);
        if (currentPage > 0) {
            this.manager.previousPage(player.getUniqueId(), this.contextId);
            onPreviousPage(player, inventory);
        } else {
            onCannotPreviousPage(player, inventory);
        }
    }
}