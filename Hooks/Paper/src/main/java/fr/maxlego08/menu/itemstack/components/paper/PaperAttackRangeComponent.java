package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperAttackRangeComponent extends AttackRangeComponent {

    public PaperAttackRangeComponent(
            @NotNull ResolvableFloat minReach, @NotNull ResolvableFloat maxReach,
            @NotNull ResolvableFloat minCreativeReach, @NotNull ResolvableFloat maxCreativeReach,
            @NotNull ResolvableFloat hitboxMargin, @NotNull ResolvableFloat mobFactor) {
        super(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {

        AttackRange.Builder builder = AttackRange.attackRange();

        this.applyResolvable(context, builder::minReach, this.minReach);
        this.applyResolvable(context, builder::maxReach, this.maxReach);
        this.applyResolvable(context, builder::minCreativeReach, this.minCreativeReach);
        this.applyResolvable(context, builder::maxCreativeReach, this.maxCreativeReach);
        this.applyResolvable(context, builder::hitboxMargin, this.hitboxMargin);
        this.applyResolvable(context, builder::mobFactor, this.mobFactor);


        itemStack.setData(
                DataComponentTypes.ATTACK_RANGE,
                builder.build()
        );
    }
}