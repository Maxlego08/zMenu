package fr.maxlego08.menu.hooks.packetevents.session;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Packet state of one online player, shared by every packet feature.
 * <p>
 * Fields read by the network thread and written by the server thread are volatile or concurrent.
 */
public class PacketSession {

    private volatile WrapperPlayServerOpenWindow lastOpenWindow;
    private volatile WrapperPlayServerWindowItems lastWindowItems;

    private volatile int animationContainerId;
    private volatile int animationWindowId;

    private boolean holdArmed;
    private int heldContainerId = -1;
    private WrapperPlayServerWindowItems heldWindowItems;

    private final Map<Integer, ItemStack> hiddenSlots = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> topSizes = new ConcurrentHashMap<>();
    private volatile Map<Integer, WrapperPlayServerSetPlayerInventory> pendingSlots;

    private volatile boolean clickLimited;
    private volatile long lastClickTime;

    public WrapperPlayServerOpenWindow getLastOpenWindow() {
        return this.lastOpenWindow;
    }

    public void setLastOpenWindow(WrapperPlayServerOpenWindow lastOpenWindow) {
        this.lastOpenWindow = lastOpenWindow;
    }

    public WrapperPlayServerWindowItems getLastWindowItems() {
        return this.lastWindowItems;
    }

    public void setLastWindowItems(WrapperPlayServerWindowItems lastWindowItems) {
        this.lastWindowItems = lastWindowItems;
    }

    public void clearWindow() {
        this.lastOpenWindow = null;
        this.lastWindowItems = null;
    }

    public int getAnimationContainerId() {
        return this.animationContainerId;
    }

    public void setAnimationContainerId(int animationContainerId) {
        this.animationContainerId = animationContainerId;
    }

    public int getAnimationWindowId() {
        return this.animationWindowId;
    }

    public void setAnimationWindowId(int animationWindowId) {
        this.animationWindowId = animationWindowId;
    }

    public void clearAnimation() {
        this.animationContainerId = 0;
        this.animationWindowId = 0;
    }

    /**
     * Sets whether the content of the next window opened must be held back until its title animation ends.
     * Called before the menu opens, because the window packets are sent before any later check could run.
     */
    public synchronized void armWindowHold(boolean armed) {
        this.holdArmed = armed;
    }

    public synchronized void onWindowOpened(int containerId) {
        if (!this.holdArmed && containerId == this.heldContainerId) return;
        this.heldContainerId = this.holdArmed ? containerId : -1;
        this.heldWindowItems = null;
        this.holdArmed = false;
    }

    /**
     * Keeps the first content packet of the held window.
     *
     * @return true if the packet was kept and must not be sent now
     */
    public synchronized boolean holdWindowItems(int windowId, WrapperPlayServerWindowItems wrapper) {
        if (this.heldContainerId == -1 || this.heldContainerId != windowId || this.heldWindowItems != null) return false;
        this.heldWindowItems = wrapper;
        return true;
    }

    /**
     * Stops holding the content of the window, and returns the packet kept so far, if any.
     */
    public synchronized WrapperPlayServerWindowItems releaseWindow(int containerId) {
        if (this.heldContainerId != containerId) return null;
        WrapperPlayServerWindowItems wrapper = this.heldWindowItems;
        this.heldContainerId = -1;
        this.heldWindowItems = null;
        return wrapper;
    }

    /**
     * Player inventory slots shown with a fake item, by player slot (0-8 hotbar, 9-35 storage, 40 offhand).
     */
    public Map<Integer, ItemStack> getHiddenSlots() {
        return this.hiddenSlots;
    }

    /**
     * Size of the top inventory of each window, read from its last {@code WINDOW_ITEMS} packet.
     */
    public Map<Integer, Integer> getTopSizes() {
        return this.topSizes;
    }

    /**
     * Slot packets waiting for the flush that follows the menu opening, or null when nothing is waiting.
     */
    public Map<Integer, WrapperPlayServerSetPlayerInventory> getPendingSlots() {
        return this.pendingSlots;
    }

    public Map<Integer, WrapperPlayServerSetPlayerInventory> getOrCreatePendingSlots() {
        Map<Integer, WrapperPlayServerSetPlayerInventory> pending = this.pendingSlots;
        if (pending == null) {
            pending = new ConcurrentHashMap<>();
            this.pendingSlots = pending;
        }
        return pending;
    }

    public void clearPendingSlots() {
        this.pendingSlots = null;
    }

    public void clearHiddenInventory() {
        this.hiddenSlots.clear();
        this.topSizes.clear();
        this.pendingSlots = null;
    }

    public boolean isClickLimited() {
        return this.clickLimited;
    }

    public void setClickLimited(boolean clickLimited) {
        this.clickLimited = clickLimited;
    }

    public long getLastClickTime() {
        return this.lastClickTime;
    }

    public void setLastClickTime(long lastClickTime) {
        this.lastClickTime = lastClickTime;
    }

    public void clearClickLimiter() {
        this.clickLimited = false;
        this.lastClickTime = 0L;
    }
}
