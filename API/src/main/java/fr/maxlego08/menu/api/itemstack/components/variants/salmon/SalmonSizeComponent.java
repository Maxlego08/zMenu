package fr.maxlego08.menu.api.itemstack.components.variants.salmon;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.entity.Salmon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SalmonSizeComponent extends ItemComponent {
    private final ResolvableEnum<Salmon.Variant> size;

    public SalmonSizeComponent(ResolvableEnum<Salmon.Variant> size) {
        this.size = size;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.size, resolvedSize -> itemStack.setData(DataComponentTypes.SALMON_SIZE, resolvedSize));
    }
}
