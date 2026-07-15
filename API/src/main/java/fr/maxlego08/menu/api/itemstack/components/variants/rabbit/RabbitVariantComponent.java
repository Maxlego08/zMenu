package fr.maxlego08.menu.api.itemstack.components.variants.rabbit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RabbitVariantComponent extends ItemComponent {
    private final ResolvableEnum<Rabbit.Type> variant;

    public RabbitVariantComponent(ResolvableEnum<Rabbit.Type> variant) {
        this.variant = variant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.variant, resolvedVariant -> itemStack.setData(io.papermc.paper.datacomponent.DataComponentTypes.RABBIT_VARIANT, resolvedVariant));
    }
}
