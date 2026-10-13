package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    public @Nullable Object serialize() {
        return this.repairCost.serialize();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, repairCost -> itemStack.setData(DataComponentTypes.REPAIR_COST, repairCost), this.repairCost);
    }
}
