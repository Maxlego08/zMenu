package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.GenericPaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class PaginationNextButton extends PaginationButton {

    public PaginationNextButton(@NotNull MenuPlugin plugin, @NotNull String contextId) {
        super(plugin, contextId);
    }

    protected void onNextPage(@NotNull Player player, @NotNull InventoryEngine inventory) {
        this.refreshInventory(player);
    }

    protected void onCannotNextPage(@NotNull Player player, @NotNull InventoryEngine inventory) {
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot, @NotNull Placeholders placeholders) {
        GenericPaginateButton paginateButton = this.findPaginateButton(inventory, player);
        if (paginateButton == null) return;

        int currentPage = this.manager.getPage(player.getUniqueId(), this.contextId);
        if (currentPage < paginateButton.getMaxPage(player)) {
            this.manager.nextPage(player.getUniqueId(), this.contextId);
            this.onNextPage(player, inventory);
        } else {
            this.onCannotNextPage(player, inventory);
        }
    }
}