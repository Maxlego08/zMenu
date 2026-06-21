package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RepairCostComponent extends ItemComponent {
    private final ResolvableInt repairCost;

    public RepairCostComponent(int repairCost) {
        this.repairCost = ResolvableInt.of(repairCost);
    }

    public RepairCostComponent(ResolvableInt repairCost) {
        this.repairCost = repairCost;
    }

    public ResolvableInt getRepairCost() {
        return this.repairCost;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        boolean apply = ItemUtil.editMeta(itemStack, Repairable.class, repairable -> {

            Resolvable.applyResolvable(context, this.repairCost, repairable::setRepairCost);

        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply RepairCostComponent to item: " + itemStack.getType().name());
    }
}
