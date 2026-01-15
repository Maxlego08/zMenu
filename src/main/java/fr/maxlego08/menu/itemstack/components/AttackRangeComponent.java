package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AttackRangeComponent(
        float minReach,
        float maxReach,
        float minCreativeReach,
        float maxCreativeReach,
        float hitboxMargin,
        float mobFactor
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
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
