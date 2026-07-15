package fr.maxlego08.menu.api.itemstack.components.variants.fox;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FoxVariantComponent extends ItemComponent {
    private final ResolvableEnum<Fox.Type> variant;

    public FoxVariantComponent(ResolvableEnum<Fox.Type> variant) {
        this.variant = variant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.variant, resolvedVariant -> itemStack.setData(DataComponentTypes.FOX_VARIANT, resolvedVariant));
    }
}
