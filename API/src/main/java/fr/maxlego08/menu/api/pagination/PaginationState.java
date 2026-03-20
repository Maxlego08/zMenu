package fr.maxlego08.menu.api.pagination;

public class PaginationState {
    private int currentPage;

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
        return currentPage;
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

    @Override
    public String toString() {
        return String.format("PaginationState{currentPage=%d}", currentPage);
    }
}