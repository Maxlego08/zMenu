package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class GenericPaginationButton <T> extends GenericPaginateButton {

    /**
     * Gets the list of elements to paginate.
     *
     * @param player the player
     * @return the list of elements
     */
    @NotNull
    protected abstract List<T> getElements(@NotNull Player player);

    /**
     * Renders a single element at the given slot.
     *
     * @param player the player
     * @param inventory the inventory engine
     * @param slot the inventory slot
     * @param element the element to render
     * @param placeholders the placeholders
     */
    protected abstract void renderElement(
            @NotNull Player player,
            @NotNull InventoryEngine inventory,
            int slot,
            @NotNull T element,
            @NotNull Placeholders placeholders);

    @Override
    public final void onRender(@NotNull Player player, @NotNull InventoryEngine inventory) {
        List<T> elements = getElements(player);
        int pageSize = getSlots().size();
        int currentPage = getCurrentPageOneIndexed(player);

        Pagination<T> pagination = new Pagination<>();
        List<T> paginatedElements = pagination.paginate(elements, pageSize, currentPage);

        int slotIndex = 0;
        for (Integer slot : getSlots()) {
            if (slotIndex >= paginatedElements.size()) break;

            T element = paginatedElements.get(slotIndex);
            Placeholders placeholders = new Placeholders();
            placeholders.register("page", String.valueOf(getCurrentPageOneIndexed(player)));
            placeholders.register("max_page", String.valueOf(getMaxPage(player) + 1));

            renderElement(player, inventory, slot, element, placeholders);
            slotIndex++;
        }
    }
}


