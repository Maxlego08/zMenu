package fr.maxlego08.menu.api.itemstack.components.variants.sheep;

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

public final class SheepColorComponent extends ItemComponent {
    private final ResolvableEnum<DyeColor> color;

    public SheepColorComponent(ResolvableEnum<DyeColor> color) {
        this.color = color;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.color, resolvedColor -> itemStack.setData(DataComponentTypes.SHEEP_COLOR, resolvedColor));
    }
}
