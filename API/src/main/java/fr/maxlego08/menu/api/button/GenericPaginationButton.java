package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.Pagination;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
        Collection<Integer> slots = getSlots();
        int pageSize = slots.size();
        int currentPage = getCurrentPageOneIndexed(player);
        
        int maxPage = getMaxPage(player, pageSize) + 1;

        Pagination<T> pagination = new Pagination<>();
        List<T> paginatedElements = pagination.paginate(elements, pageSize, currentPage - 1);

        int slotIndex = 0;
        for (Integer slot : slots) {
            if (slotIndex >= paginatedElements.size()) break;

            T element = paginatedElements.get(slotIndex);
            Placeholders placeholders = new Placeholders();
            placeholders.register("page", String.valueOf(currentPage));
            placeholders.register("max_page", String.valueOf(maxPage));

            renderElement(player, inventory, slot, element, placeholders);
            slotIndex++;
        }
    }
}


