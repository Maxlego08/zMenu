package fr.maxlego08.menu.api.itemstack.components.variants;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TropicalFishBaseColorComponent extends ItemComponent {
    private final ResolvableEnum<DyeColor> baseColor;

    public TropicalFishBaseColorComponent(ResolvableEnum<DyeColor> baseColor) {
        this.baseColor = baseColor;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.baseColor, resolvedBaseColor -> itemStack.setData(DataComponentTypes.TROPICAL_FISH_BASE_COLOR, resolvedBaseColor));
    }
}
