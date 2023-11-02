package fr.maxlego08.menu.api.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the default values of the button. Allows to be changed before the button is loaded.
 */
public class DefaultButtonValue {

    private int slot = 0;
    private int page = 1;
    private List<Integer> slots = new ArrayList<>();
    private boolean isPermanent = false;
    private boolean updateOnClick = false;
    private boolean closeInventory = false;
    private boolean update = false;
    private boolean refreshOnClick = false;
    private String playerHead = null;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    public boolean isUpdateOnClick() {
        return updateOnClick;
    }

    public void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    public boolean isCloseInventory() {
        return closeInventory;
    }

    public void setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isRefreshOnClick() {
        return refreshOnClick;
    }

    public void setRefreshOnClick(boolean refreshOnClick) {
        this.refreshOnClick = refreshOnClick;
    }

    public String getPlayerHead() {
        return playerHead;
    }

    public void setPlayerHead(String playerHead) {
        this.playerHead = playerHead;
    }
}
