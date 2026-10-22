package fr.maxlego08.menu.api.inventory;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.animation.slot.settings.SlotAnimationSettings;
import fr.maxlego08.menu.api.utils.ClearInvType;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.Nullable;

public interface ContainerInventory extends Inventory {

    // --- Title Animation ---

    void setTitleAnimation(TitleAnimation animation);

    @Nullable
    TitleAnimation getTitleAnimation();

    // -- Clean Inventory ---

    boolean cleanInventory();

    default boolean clearInventory() {
        return this.cleanInventory();
    }


    @Nullable
    MenuItemStack getFillItemStack();

    /**
     * Whether the fill item may be served from its build cache, the {@code use-cache} option of
     * the fill item, which works like the one of a button.
     *
     * @return true when the cache may be used, which is the default
     */
    default boolean isFillItemUseCache() {
        return true;
    }

    boolean shouldCancelItemPickup();

    InventoryType getType();

    boolean isClickLimiterEnabled();

    ClearInvType getClearInvType();

    /**
     * Gets the animation played when this inventory is opened.
     *
     * @return the animation settings, or null if the inventory is displayed at once
     */
    @Nullable
    default SlotAnimationSettings getSlotAnimation() {
        return null;
    }

    /**
     * Gets the animation played when this inventory closes itself.
     *
     * <p>Only plays for a close the menu decides on, such as a {@code close} action or a switch to
     * another menu. A player pressing escape closes the window at once and nothing can hold it
     * open long enough to animate.</p>
     *
     * @return the animation settings, or null if the inventory just goes away
     */
    @Nullable
    default SlotAnimationSettings getCloseAnimation() {
        return null;
    }
}
