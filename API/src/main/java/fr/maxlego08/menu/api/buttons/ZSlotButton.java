package fr.maxlego08.menu.api.buttons;

import fr.maxlego08.menu.api.button.SlotButton;

import java.util.Collection;
import java.util.List;

public abstract class ZSlotButton implements SlotButton {

    protected List<Integer> slots;
    protected int page;

    @Override
    public Collection<Integer> getSlots() {
        return this.slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    @Override
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
