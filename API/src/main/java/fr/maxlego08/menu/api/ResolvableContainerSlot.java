package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public final class ResolvableContainerSlot {

    private final MenuItemStack itemStack;
    private final ResolvableInt slot;

    public ResolvableContainerSlot(@NotNull MenuItemStack itemStack, @NotNull ResolvableInt slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public void applyTo(@NotNull Inventory inventory, @NotNull BuildContext context) {
        Integer resolvedSlot = this.slot.resolve(context);
        if (resolvedSlot != null) {
            inventory.setItem(resolvedSlot, this.itemStack.build(context));
        }
    }
}
