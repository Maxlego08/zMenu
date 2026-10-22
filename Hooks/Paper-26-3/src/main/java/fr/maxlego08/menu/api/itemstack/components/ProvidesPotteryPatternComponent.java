package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.block.pot.PotPatternType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ProvidesPotteryPatternComponent extends ItemComponent {
    private final ResolvableRegistryEntry<PotPatternType> pattern;

    public ProvidesPotteryPatternComponent(ResolvableRegistryEntry<PotPatternType> pattern) {
        this.pattern = pattern;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.pattern, resolvedPattern -> itemStack.setData(DataComponentTypes.PROVIDES_POTTERY_PATTERN, resolvedPattern));
    }

    @Override
    public @Nullable Object serialize() {
        return this.pattern.serialize();
    }
}
