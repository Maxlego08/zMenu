package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.pagination.PaginationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class GenericPaginateButton extends PaginateButton {

    @NotNull
    public abstract String getContextId(@NotNull Player player);


    @NotNull
    public abstract PaginationManager getPaginationManager();

    /**
     * Gets the current page for this button (0-based index).
     *
     * @param player the player
     * @return the current page
     */
    public final int getCurrentPage(@NotNull Player player) {
        return this.getPaginationManager().getPage(player.getUniqueId(), this.getContextId(player));
    }

    /**
     * Gets the current page (1-based index) for UI purposes.
     *
     * @param player the player
     * @return the current page (1-based)
     */
    public final int getCurrentPageOneIndexed(@NotNull Player player) {
        return this.getCurrentPage(player) + 1;
    }

    /**
     * Sets the current page for this button.
     *
     * @param player the player
     * @param page   the page to set (0-based index)
     */
    public final void setCurrentPage(@NotNull Player player, int page) {
        this.getPaginationManager().setPage(player.getUniqueId(), this.getContextId(player), page);
    }

    /**
     * Advances to the next page if available.
     *
     * @param player the player
     * @return true if advanced, false if already at the last page
     */
    public final boolean nextPage(@NotNull Player player) {
        int currentPage = this.getCurrentPage(player);
        int maxPage = this.getMaxPage(player);
        if (currentPage < maxPage) {
            this.getPaginationManager().nextPage(player.getUniqueId(), this.getContextId(player));
            return true;
        }
        return false;
    }

    /**
     * Goes to the previous page if available.
     *
     * @param player the player
     * @return true if went back, false if already at the first page
     */
    public final boolean previousPage(@NotNull Player player) {
        int currentPage = this.getCurrentPage(player);
        if (currentPage > 0) {
            this.getPaginationManager().previousPage(player.getUniqueId(), this.getContextId(player));
            return true;
        }
        return false;
    }

    /**
     * Resets pagination to the first page.
     *
     * @param player the player
     */
    public final void resetPagination(@NotNull Player player) {
        this.getPaginationManager().reset(player.getUniqueId(), this.getContextId(player));
    }

    /**
     * Calculates the maximum page number (0-based index).
     * This caches the page size to avoid multiple getSlots() calls.
     *
     * @param player the player
     * @return the maximum page
     */
    public final int getMaxPage(@NotNull Player player) {
        int totalSize = this.getPaginationSize(player);
        int pageSize = this.getSlots().size();
        if (pageSize <= 0) return 0;
        return Math.max(0, (totalSize - 1) / pageSize);
    }

    /**
     * Calculates the maximum page number with pre-calculated page size (0-based index).
     * Use this when page size is already available to avoid redundant getSlots() calls.
     *
     * @param player the player
     * @param pageSize the page size
     * @return the maximum page
     */
    public final int getMaxPage(@NotNull Player player, int pageSize) {
        if (pageSize <= 0) return 0;
        int totalSize = this.getPaginationSize(player);
        return Math.max(0, (totalSize - 1) / pageSize);
    }

    /**
     * Checks if there's a next page available.
     *
     * @param player the player
     * @return true if there's a next page
     */
    public final boolean hasNextPage(@NotNull Player player) {
        int currentPage = this.getCurrentPage(player);
        return currentPage < this.getMaxPage(player);
    }

    /**
     * Checks if there's a previous page available.
     *
     * @param player the player
     * @return true if there's a previous page
     */
    public final boolean hasPreviousPage(@NotNull Player player) {
        return this.getCurrentPage(player) > 0;
    }

}

