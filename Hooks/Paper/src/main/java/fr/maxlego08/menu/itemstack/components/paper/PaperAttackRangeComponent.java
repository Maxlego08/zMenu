package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperAttackRangeComponent extends AttackRangeComponent {

    public PaperAttackRangeComponent(
            @NotNull Resolvable<Float> minReach, @NotNull Resolvable<Float> maxReach,
            @NotNull Resolvable<Float> minCreativeReach, @NotNull Resolvable<Float> maxCreativeReach,
            @NotNull Resolvable<Float> hitboxMargin, @NotNull Resolvable<Float> mobFactor) {
        super(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        AttackRange.Builder builder = AttackRange.attackRange();

        this.applyDataComponent(builder::minReach, context, this.minReach);
        this.applyDataComponent(builder::maxReach, context, this.maxReach);
        this.applyDataComponent(builder::minCreativeReach, context, this.minCreativeReach);
        this.applyDataComponent(builder::maxCreativeReach, context, this.maxCreativeReach);
        this.applyDataComponent(builder::hitboxMargin, context, this.hitboxMargin);
        this.applyDataComponent(builder::mobFactor, context, this.mobFactor);

        itemStack.setData(
                DataComponentTypes.ATTACK_RANGE,
                builder.build()
        );
    }
}