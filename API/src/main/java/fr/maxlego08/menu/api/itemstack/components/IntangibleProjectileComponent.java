package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntangibleProjectileComponent extends ItemComponent {
    private final boolean intangible;

    public IntangibleProjectileComponent(boolean intangible) {
        this.intangible = intangible;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        if (this.intangible)
            itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }

    @Override
    public @Nullable Object serialize() {
        return this.intangible;
    }
}
