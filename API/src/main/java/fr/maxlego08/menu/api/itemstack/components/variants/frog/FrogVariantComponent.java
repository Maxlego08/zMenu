package fr.maxlego08.menu.api.itemstack.components.variants.frog;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FrogVariantComponent extends ItemComponent {
    private final ResolvableRegistryEntry<Frog.Variant> variant;

    public FrogVariantComponent(ResolvableRegistryEntry<Frog.Variant> variant) {
        this.variant = variant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.variant, resolvedVariant -> itemStack.setData(DataComponentTypes.FROG_VARIANT, resolvedVariant));
    }
}
