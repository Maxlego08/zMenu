package fr.maxlego08.menu.api.button;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains the default values of the button. Allows changes before the button is loaded.
 */
public class DefaultButtonValue {

    private final int inventorySize;
    private final Map<Character, List<Integer>> matrix;
    private final File file;
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
    private boolean updateMasterButton = false;
    // Default refreshOnClick value
    private boolean refreshOnClick = false;
    // Default refreshOnDrag value
    private boolean refreshOnDrag = false;
    // Default useCache value
    private boolean useCache = true;
    // Default playerHead value
    private String playerHead = null;

    public DefaultButtonValue(int inventorySize, Map<Character, List<Integer>> matrix, File file) {
        this.inventorySize = inventorySize;
        this.matrix = matrix;
        this.file = file;
    }

    /**
     * Gets the default slot value.
     *
     * @return The default slot value.
     */
    @Contract(pure = true)
    public int getSlot() {
        return this.slot;
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
    @Contract(pure = true)
    public int getPage() {
        return this.page;
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
    @Contract(pure = true)
    public List<Integer> getSlots() {
        return this.slots;
    }

    /**
     * Sets the default list of slots.
     *
     * @param slots The default list of slots to set.
     */
    public void setSlots(@NotNull List<Integer> slots) {
        this.slots = slots;
    }

    /**
     * Gets the default isPermanent value.
     *
     * @return The default isPermanent value.
     */
    @Contract(pure = true)
    public boolean isPermanent() {
        return this.isPermanent;
    }

    /**
     * Sets the default isPermanent value.
     *
     * @param permanent The default isPermanent value to set.
     */
    public void setPermanent(boolean permanent) {
        this.isPermanent = permanent;
    }

    /**
     * Gets the default updateOnClick value.
     *
     * @return The default updateOnClick value.
     */
    @Contract(pure = true)
    public boolean isUpdateOnClick() {
        return this.updateOnClick;
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
    @Contract(pure = true)
    public boolean isCloseInventory() {
        return this.closeInventory;
    }

    /**
     * Sets the default closeInventory value.
     *
     * @param closeInventory The default closeInventory value to set.
     */
    @Contract(pure = true)
    public void setCloseInventory(boolean closeInventory) {
        this.closeInventory = closeInventory;
    }

    /**
     * Gets the default update value.
     *
     * @return The default update value.
     */
    @Contract(pure = true)
    public boolean isUpdate() {
        return this.update;
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
    @Contract(pure = true)
    public boolean isRefreshOnClick() {
        return this.refreshOnClick;
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
     * Gets the default refreshOnDrag value.
     *
     * @return The default refreshOnDrag value.
     */
    public boolean isRefreshOnDrag() {
        return refreshOnDrag;
    }

    /**
     * Sets the default refreshOnDrag value.
     *
     * @param refreshOnDrag The default refreshOnDrag value to set.
     */
    public void setRefreshOnDrag(boolean refreshOnDrag) {
        this.refreshOnDrag = refreshOnDrag;
    }

    /**
     * Gets the default playerHead value.
     *
     * @return The default playerHead value.
     */
    @Contract(pure = true)
    @Nullable
    public String getPlayerHead() {
        return this.playerHead;
    }

    /**
     * Sets the default playerHead value.
     *
     * @param playerHead The default playerHead value to set.
     */
    public void setPlayerHead(@Nullable String playerHead) {
        this.playerHead = playerHead;
    }

    @Contract(pure = true)
    public boolean isUseCache() {
        return this.useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    @Contract(pure = true)
    public boolean isUpdateMasterButton() {
        return this.updateMasterButton;
    }

    public void setUpdateMasterButton(boolean updateMasterButton) {
        this.updateMasterButton = updateMasterButton;
    }

    @Contract(pure = true)
    public int getInventorySize() {
        return this.inventorySize;
    }

    @Contract(pure = true)
    public Map<Character, List<Integer>> getMatrix() {
        return this.matrix;
    }

    @Contract(pure = true)
    @Nullable
    public File getFile() {
        return this.file;
    }
}
