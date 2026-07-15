package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;

import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MaxStackSizeComponent extends ItemComponent {
    private final ResolvableInt maxStackSize;

    public MaxStackSizeComponent(ResolvableInt maxStackSize) {
        this.maxStackSize = maxStackSize;
    }

    public ResolvableInt getMaxStackSize() {
        return this.maxStackSize;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.maxStackSize, resolved -> itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, resolved));
    }
}
