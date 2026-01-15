package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.zcore.utils.itemstack.DamageReductionRecord;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BlocksAttacksComponent(
        float blockDelaySeconds,
        float disableCooldownScale,
        List<@NotNull DamageReductionRecord> damageReductions,
        float itemDamageThreshold,
        float itemDamageBase,
        float itemDamageFactor,
        Sound blockSound,
        Sound disabledSound
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.BlocksAttacksComponent blocksAttacks = itemMeta.getBlocksAttacks();
            blocksAttacks.setBlockDelaySeconds(this.blockDelaySeconds);
            blocksAttacks.setDisableCooldownScale(this.disableCooldownScale);

            for (DamageReductionRecord record : this.damageReductions) {
                blocksAttacks.addDamageReduction(
                    record.damageTypes(),
                    record.base(),
                    record.factor(),
                    record.horizontalBlockingAngle()
                );
            }

            blocksAttacks.setItemDamageThreshold(this.itemDamageThreshold);
            blocksAttacks.setItemDamageBase(this.itemDamageBase);
            blocksAttacks.setItemDamageFactor(this.itemDamageFactor);

            blocksAttacks.setBlockSound(this.blockSound);
            blocksAttacks.setDisableSound(this.disabledSound);


            itemStack.setItemMeta(itemMeta);
        }
    }
}
