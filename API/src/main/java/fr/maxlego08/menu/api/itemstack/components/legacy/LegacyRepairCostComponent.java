package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.RepairCostComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyRepairCostComponent extends RepairCostComponent {

    public LegacyRepairCostComponent(ResolvableInt repairCost) {
        super(repairCost);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, Repairable.class, repairable -> {
            this.applyResolvable(context, repairable::setRepairCost, this.getRepairCost());
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply RepairCostComponent to item: " + itemStack.getType().name());
    }
}
