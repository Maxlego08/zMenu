package fr.maxlego08.menu.api.button;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class SlotButton {

    protected List<Integer> slots;
    protected int page;

    /**
     * Returns the list of slots used by this button.
     *
     * @return The list of slots used by this button.
     */
    @Contract(pure = true)
    @NotNull
    public Collection<Integer> getSlots() {
        return this.slots;
    }

    /**
     * Sets the list of slots used by this button.
     *
     * @param slots The list of slots to set.
     */
    public void setSlots(@NotNull List<Integer> slots) {
        this.slots = slots;
    }

    @Contract(pure = true)
    public int getPage() {
        return this.page;
    }

    /**
     * Sets the page number for this button.
     * This page number is used to determine which page of an inventory this button should be displayed on.
     *
     * @param page The page number to set.
     */
    public void setPage(int page) {
        this.page = page;
    }
}
