package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.PaperResolvableDeathProtection;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperDeathProtectionComponent extends ItemComponent {
    private final PaperResolvableDeathProtection deathProtectionResolvable;

    public PaperDeathProtectionComponent(@NotNull PaperResolvableDeathProtection deathProtectionResolvable) {
        this.deathProtectionResolvable = deathProtectionResolvable;
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.deathProtectionResolvable, resolved -> itemStack.setData(DataComponentTypes.DEATH_PROTECTION, resolved));
    }
}
