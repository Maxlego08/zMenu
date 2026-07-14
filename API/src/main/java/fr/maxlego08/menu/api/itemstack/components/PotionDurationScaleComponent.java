package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PotionDurationScaleComponent extends ItemComponent {
    private final @NotNull ResolvableFloat durationScale;

    public PotionDurationScaleComponent(@NotNull ResolvableFloat durationScale) {
        this.durationScale = durationScale;
    }

    public @NotNull ResolvableFloat getDurationScale() {
        return this.durationScale;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Float resolvedDurationScale = Resolvable.resolve(context, this.durationScale);
        if (resolvedDurationScale != null) {
            itemStack.setData(DataComponentTypes.POTION_DURATION_SCALE, resolvedDurationScale);
        }
    }
}
