package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class AttackRangeComponent extends ItemComponent {

    private final float minReach;
    private final float maxReach;
    private final float minCreativeReach;
    private final float maxCreativeReach;
    private final float hitboxMargin;
    private final float mobFactor;

    public AttackRangeComponent(float minReach, float maxReach, float minCreativeReach, float maxCreativeReach, float hitboxMargin, float mobFactor) {
        this.minReach = minReach;
        this.maxReach = maxReach;
        this.minCreativeReach = minCreativeReach;
        this.maxCreativeReach = maxCreativeReach;
        this.hitboxMargin = hitboxMargin;
        this.mobFactor = mobFactor;
    }

    public float getMinReach() {
        return this.minReach;
    }

    public float getMaxReach() {
        return this.maxReach;
    }

    public float getMinCreativeReach() {
        return this.minCreativeReach;
    }

    public float getMaxCreativeReach() {
        return this.maxCreativeReach;
    }

    public float getHitboxMargin() {
        return this.hitboxMargin;
    }

    public float getMobFactor() {
        return this.mobFactor;
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
