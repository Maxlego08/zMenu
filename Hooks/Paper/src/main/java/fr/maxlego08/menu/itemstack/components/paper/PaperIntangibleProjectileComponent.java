package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperIntangibleProjectileComponent extends ItemComponent {
    private final boolean intangible;

    public PaperIntangibleProjectileComponent(boolean intangible) {
        this.intangible = intangible;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        if (this.intangible)
            itemStack.setData(DataComponentTypes.INTANGIBLE_PROJECTILE);
    }
}
