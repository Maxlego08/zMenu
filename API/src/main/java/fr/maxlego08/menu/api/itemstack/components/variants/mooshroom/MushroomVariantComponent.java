package fr.maxlego08.menu.api.itemstack.components.variants.mooshroom;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MushroomVariantComponent extends ItemComponent {
    private final ResolvableEnum<MushroomCow.Variant> variant;

    public MushroomVariantComponent(ResolvableEnum<MushroomCow.Variant> variant) {
        this.variant = variant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.variant, resolvedVariant -> itemStack.setData(DataComponentTypes.MOOSHROOM_VARIANT, resolvedVariant));
    }
}
