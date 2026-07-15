package fr.maxlego08.menu.api.itemstack.components.variants.wolf;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WolfCollarComponent extends ItemComponent {
    private final ResolvableDyeColor color;

    public WolfCollarComponent(ResolvableDyeColor color) {
        this.color = color;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.color, resolvedColor -> itemStack.setData(DataComponentTypes.WOLF_COLLAR, resolvedColor));
    }
}
