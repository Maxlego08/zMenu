package fr.maxlego08.menu.api.itemstack.components.variants.cat;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CatCollarComponent extends ItemComponent {
    private final ResolvableDyeColor collarColor;

    public CatCollarComponent(ResolvableDyeColor collarColor) {
        this.collarColor = collarColor;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.collarColor, resolvedCollarColor -> itemStack.setData(DataComponentTypes.CAT_COLLAR, resolvedCollarColor));
    }
}
