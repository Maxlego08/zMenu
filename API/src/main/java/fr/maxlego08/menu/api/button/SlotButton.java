package fr.maxlego08.menu.api.button;

import java.util.Collection;
import java.util.List;

public abstract class SlotButton {

    protected List<Integer> slots;
    protected int page;

    public Collection<Integer> getSlots() {
        return this.slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }
    
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
