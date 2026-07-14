package fr.maxlego08.menu.api.itemstack.components.variants.tropicalfish;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TropicalFishPatternComponent extends ItemComponent {
    private final ResolvableEnum<TropicalFish.Pattern> pattern;

    public TropicalFishPatternComponent(ResolvableEnum<TropicalFish.Pattern> pattern) {
        this.pattern = pattern;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.pattern, resolvedPattern -> itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN, resolvedPattern));
    }
}
