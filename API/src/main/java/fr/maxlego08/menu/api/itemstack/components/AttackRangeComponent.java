package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.AttackRange;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AttackRangeComponent extends ItemComponent {

    private final ResolvableFloat minReach;
    private final ResolvableFloat maxReach;
    private final ResolvableFloat minCreativeReach;
    private final ResolvableFloat maxCreativeReach;
    private final ResolvableFloat hitboxMargin;
    private final ResolvableFloat mobFactor;

    public AttackRangeComponent(
            @Nullable ResolvableFloat minReach,
            @Nullable ResolvableFloat maxReach,
            @Nullable ResolvableFloat minCreativeReach,
            @Nullable ResolvableFloat maxCreativeReach,
            @Nullable ResolvableFloat hitboxMargin,
            @Nullable ResolvableFloat mobFactor) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minCreativeReach = minCreativeReach;
        this.maxCreativeReach = maxCreativeReach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
    }

    public ResolvableFloat getMinReach() {
        return this.minReach;
    }

    public ResolvableFloat getMaxReach() {
        return this.maxReach;
    }

    public ResolvableFloat getMinCreativeReach() {
        return this.minCreativeReach;
    }

    public ResolvableFloat getMaxCreativeReach() {
        return this.maxCreativeReach;
    }

    public ResolvableFloat getHitboxMargin() {
        return this.hitboxMargin;
    }

    public ResolvableFloat getMobFactor() {
        return this.mobFactor;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        AttackRange.Builder builder = AttackRange.attackRange();

        Resolvable.applyResolvable(context, this.minReach, builder::minReach);
        Resolvable.applyResolvable(context, this.maxReach, builder::maxReach);
        Resolvable.applyResolvable(context, this.minCreativeReach, builder::minCreativeReach);
        Resolvable.applyResolvable(context, this.maxCreativeReach, builder::maxCreativeReach);
        Resolvable.applyResolvable(context, this.hitboxMargin, builder::hitboxMargin);
        Resolvable.applyResolvable(context, this.mobFactor, builder::mobFactor);

        itemStack.setData(
                DataComponentTypes.ATTACK_RANGE,
                builder.build()
        );
    }
}