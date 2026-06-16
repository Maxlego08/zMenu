package fr.maxlego08.menu.itemstack.components.spigot;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.AttackRangeComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SpigotAttackRangeComponent extends AttackRangeComponent {

    public SpigotAttackRangeComponent(float minReach, float maxReach, float minCreativeReach, float maxCreativeReach, float hitboxMargin, float mobFactor) {
        super(minReach, maxReach, minCreativeReach, maxCreativeReach, hitboxMargin, mobFactor);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.AttackRangeComponent attackRange = itemMeta.getAttackRange();
        attackRange.setMinReach(this.minReach);
        attackRange.setMaxReach(this.maxReach);
        attackRange.setMinCreativeReach(this.minCreativeReach);
        attackRange.setMaxCreativeReach(this.maxCreativeReach);
        attackRange.setHitboxMargin(this.hitboxMargin);
        attackRange.setMobFactor(this.mobFactor);
        itemStack.setItemMeta(itemMeta);
    }

}
