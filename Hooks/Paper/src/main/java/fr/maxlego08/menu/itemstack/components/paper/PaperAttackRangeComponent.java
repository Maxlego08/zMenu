package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperAttackRangeComponent extends AttackRangeComponent {
    private final AttackRange attackRange;

    public PaperAttackRangeComponent(AttackRange attackRange) {
        super(attackRange.minReach(), attackRange.maxReach(), attackRange.minCreativeReach(), attackRange.maxCreativeReach(), attackRange.hitboxMargin(), attackRange.mobFactor());
        this.attackRange = attackRange;
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.ATTACK_RANGE, this.attackRange);
    }
}
