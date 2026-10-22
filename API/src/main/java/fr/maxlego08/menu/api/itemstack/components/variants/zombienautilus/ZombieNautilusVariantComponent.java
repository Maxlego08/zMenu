package fr.maxlego08.menu.api.itemstack.components.variants.zombienautilus;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.entity.ZombieNautilus;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ZombieNautilusVariantComponent extends ItemComponent {
    private final ResolvableRegistryEntry<ZombieNautilus.Variant> variant;

    public ZombieNautilusVariantComponent(ResolvableRegistryEntry<ZombieNautilus.Variant> variant) {
        this.variant = variant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.variant, resolvedVariant -> itemStack.setData(DataComponentTypes.ZOMBIE_NAUTILUS_VARIANT, resolvedVariant));
    }

    @Override
    public @Nullable Object serialize() {
        return this.variant.serialize();
    }
}
