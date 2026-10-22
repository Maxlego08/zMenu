package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WaxedComponent extends ItemComponent {
    private final ResolvableBoolean waxed;

    public WaxedComponent(ResolvableBoolean waxed) {
        this.waxed = waxed;
    }

    public ResolvableBoolean isWaxed() {
        return this.waxed;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.waxed, resolvedWaxed -> {
            if (resolvedWaxed) {
                itemStack.setData(DataComponentTypes.WAXED);
            } else {
                itemStack.unsetData(DataComponentTypes.WAXED);
            }
        });
    }

    @Override
    public @Nullable Object serialize() {
        return this.waxed.serialize();
    }
}
