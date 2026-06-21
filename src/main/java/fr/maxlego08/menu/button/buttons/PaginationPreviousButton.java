package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.GenericPaginateButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class PaginationPreviousButton extends PaginationButton {

    public PaginationPreviousButton(@NotNull MenuPlugin plugin, @NotNull String contextId, boolean onlyRefreshButton) {
        super(plugin, contextId, onlyRefreshButton);
    }

    @Override
    public void onClick(@NotNull Player player, @NotNull InventoryClickEvent event, @NotNull InventoryEngine inventory, int slot, @NotNull Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        GenericPaginateButton paginateButton = this.findPaginateButton(inventory, player);
        if (paginateButton != null && paginateButton.previousPage(player)) {
            this.onPageChange(player, inventory, paginateButton);
        }
    }
}
