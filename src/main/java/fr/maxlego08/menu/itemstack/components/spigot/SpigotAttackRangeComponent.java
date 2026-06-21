package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SpigotAttackRangeComponent extends AttackRangeComponent {

    public SpigotAttackRangeComponent(@NotNull ResolvableFloat minReach, @NotNull ResolvableFloat maxReach, @NotNull ResolvableFloat minCreativeReach, @NotNull ResolvableFloat maxCreativeReach, @NotNull ResolvableFloat hitboxMargin, @NotNull ResolvableFloat mobFactor) {
        super(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.AttackRangeComponent attackRange = itemMeta.getAttackRange();

        this.applyResolvable(context, attackRange::setMinReach, this.minReach);
        this.applyResolvable(context, attackRange::setMaxReach, this.maxReach);
        this.applyResolvable(context, attackRange::setMinCreativeReach, this.minCreativeReach);
        this.applyResolvable(context, attackRange::setMaxCreativeReach, this.maxCreativeReach);
        this.applyResolvable(context, attackRange::setHitboxMargin, this.hitboxMargin);
        this.applyResolvable(context, attackRange::setMobFactor, this.mobFactor);

        itemStack.setItemMeta(itemMeta);
    }
}
