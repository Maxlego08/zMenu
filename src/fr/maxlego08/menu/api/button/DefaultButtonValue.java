package fr.maxlego08.menu.api.button;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the default values of the button. Allows changes before the button is loaded.
 */
public class DefaultButtonValue {

    // Default slot value
    private int slot = 0;

    // Default page value
    private int page = 1;

    // Default list of slots
    private List<Integer> slots = new ArrayList<>();

    // Default isPermanent value
    private boolean isPermanent = false;

    // Default updateOnClick value
    private boolean updateOnClick = false;

    // Default closeInventory value
    private boolean closeInventory = false;

    // Default update value
    private boolean update = false;

    // Default refreshOnClick value
    private boolean refreshOnClick = false;

    // Default useCache value
    private boolean useCache = true;

    // Default playerHead value
    private String playerHead = null;

    /**
     * Gets the default slot value.
     *
     * @return The default slot value.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Sets the default slot value.
     *
     * @param slot The default slot value to set.
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Gets the default page value.
     *
     * @return The default page value.
     */
    public int getPage() {
        return page;
    }

    /**
     * Sets the default page value.
     *
     * @param page The default page value to set.
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * Gets the default list of slots.
     *
     * @return The default list of slots.
     */
    public List<Integer> getSlots() {
        return slots;
    }

    /**
     * Sets the default list of slots.
     *
     * @param slots The default list of slots to set.
     */
    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    /**
     * Gets the default isPermanent value.
     *
     * @return The default isPermanent value.
     */
    public boolean isPermanent() {
        return isPermanent;
    }

    /**
     * Sets the default isPermanent value.
     *
     * @param permanent The default isPermanent value to set.
     */
    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    /**
     * Gets the default updateOnClick value.
     *
     * @return The default updateOnClick value.
     */
    public boolean isUpdateOnClick() {
        return updateOnClick;
    }

    /**
     * Sets the default updateOnClick value.
     *
     * @param updateOnClick The default updateOnClick value to set.
     */
    public void setUpdateOnClick(boolean updateOnClick) {
        this.updateOnClick = updateOnClick;
    }

    /**
     * Gets the default closeInventory value.
     *
     * @return The default closeInventory value.
     */
    public boolean isCloseInventory() {
        return closeInventory;
    }

    /**
     * Sets the default closeInventory value.
     *
     * @param closeInventory The default closeInventory value to set.
     */
    public void setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
    }

    /**
     * Gets the default update value.
     *
     * @return The default update value.
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * Sets the default update value.
     *
     * @param update The default update value to set.
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * Gets the default refreshOnClick value.
     *
     * @return The default refreshOnClick value.
     */
    public boolean isRefreshOnClick() {
        return refreshOnClick;
    }

    /**
     * Sets the default refreshOnClick value.
     *
     * @param refreshOnClick The default refreshOnClick value to set.
     */
    public void setRefreshOnClick(boolean refreshOnClick) {
        this.refreshOnClick = refreshOnClick;
    }

    /**
     * Gets the default playerHead value.
     *
     * @return The default playerHead value.
     */
    public String getPlayerHead() {
        return playerHead;
    }

    /**
     * Sets the default playerHead value.
     *
     * @param playerHead The default playerHead value to set.
     */
    public void setPlayerHead(String playerHead) {
        this.playerHead = playerHead;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isUseCache() {
        return useCache;
    }
}
