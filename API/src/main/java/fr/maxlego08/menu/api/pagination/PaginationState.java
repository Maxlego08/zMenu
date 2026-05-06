package fr.maxlego08.menu.api.pagination;

public class PaginationState {
    private int currentPage;
    private int maxPage = 0;

    public PaginationState() {
        this(0);
    }

    public PaginationState(int page) {
        this.currentPage = Math.max(0, page);
    }

    /**
     * @return the current page (0-based index)
     */
    public int getCurrentPage() {
        return this.currentPage;
    }

    /**
     * @return the current page (1-based index for UI purposes)
     */
    public int getCurrentPageOneIndexed() {
        return this.currentPage + 1;
    }

    public void setCurrentPage(int page) {
        this.currentPage = Math.max(0, page);
    }

    public void nextPage() {
        this.currentPage++;
    }

    public void previousPage() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }
    }

    /**
     * Gets the maximum page number (0-based index).
     *
     * @return the maximum page
     */
    public int getMaxPage() {
        return this.maxPage;
    }

    /**
     * Sets the maximum page number (0-based index).
     *
     * @param maxPage the maximum page to set
     */
    public void setMaxPage(int maxPage) {
        this.maxPage = Math.max(0, maxPage);
    }

    /**
     * Gets the maximum page number (1-based index for UI purposes).
     *
     * @return the maximum page (1-based)
     */
    public int getMaxPageOneIndexed() {
        return this.maxPage + 1;
    }

    @Override
    public String toString() {
        return String.format("PaginationState{currentPage=%d, maxPage=%d}", this.currentPage, this.maxPage);
    }
}