package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SpigotAttackRangeComponent extends AttackRangeComponent {


    public SpigotAttackRangeComponent(@NotNull Resolvable<Float> minReach, @NotNull Resolvable<Float> maxReach, @NotNull Resolvable<Float> minCreativeReach, @NotNull Resolvable<Float> maxCreativeReach, @NotNull Resolvable<Float> hitboxMargin, @NotNull Resolvable<Float> mobFactor) {
        super(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.AttackRangeComponent attackRange = itemMeta.getAttackRange();

        this.applyDataComponent(attackRange::setMinReach, context, minReach);
        this.applyDataComponent(attackRange::setMaxReach, context, maxReach);
        this.applyDataComponent(attackRange::setMinCreativeReach, context, minCreativeReach);
        this.applyDataComponent(attackRange::setMaxCreativeReach, context, maxCreativeReach);
        this.applyDataComponent(attackRange::setHitboxMargin, context, hitboxMargin);
        this.applyDataComponent(attackRange::setMobFactor, context, mobFactor);

        itemStack.setItemMeta(itemMeta);
    }
}
