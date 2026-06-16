package fr.maxlego08.menu.api.inventory;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.animation.TitleAnimation;
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
        return cleanInventory();
    }


    @Nullable
    MenuItemStack getFillItemStack();

    boolean shouldCancelItemPickup();

    InventoryType getType();

    boolean isClickLimiterEnabled();

    ClearInvType getClearInvType();
}
