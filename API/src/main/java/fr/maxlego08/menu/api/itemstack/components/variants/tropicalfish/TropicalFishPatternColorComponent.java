package fr.maxlego08.menu.api.itemstack.components.variants.tropicalfish;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TropicalFishPatternColorComponent extends ItemComponent {
    private final ResolvableDyeColor patternColor;

    public TropicalFishPatternColorComponent(ResolvableDyeColor patternColor) {
        this.patternColor = patternColor;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.patternColor, resolvedPatternColor -> itemStack.setData(DataComponentTypes.TROPICAL_FISH_PATTERN_COLOR, resolvedPatternColor));
    }
}
