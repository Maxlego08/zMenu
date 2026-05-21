package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.itemstack.ZDamageReductionRecord;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class BlocksAttacksComponent extends ItemComponent {
    private final float blockDelaySeconds;
    private final float disableCooldownScale;
    private final List<ZDamageReductionRecord> damageReductions;
    private final float itemDamageThreshold;
    private final float itemDamageBase;
    private final float itemDamageFactor;
    private final Sound blockSound;
    private final Sound disabledSound;

    public BlocksAttacksComponent(
        float blockDelaySeconds,
        float disableCooldownScale,
        List<@NotNull ZDamageReductionRecord> damageReductions,
        float itemDamageThreshold,
        float itemDamageBase,
        float itemDamageFactor,
        Sound blockSound,
        Sound disabledSound
    ) {
        this.blockDelaySeconds = blockDelaySeconds;
        this.disableCooldownScale = disableCooldownScale;
        this.damageReductions = damageReductions;
        this.itemDamageThreshold = itemDamageThreshold;
        this.itemDamageBase = itemDamageBase;
        this.itemDamageFactor = itemDamageFactor;
        this.blockSound = blockSound;
        this.disabledSound = disabledSound;
    }

    public float getBlockDelaySeconds() {
        return this.blockDelaySeconds;
    }

    public float getDisableCooldownScale() {
        return this.disableCooldownScale;
    }

    public List<ZDamageReductionRecord> getDamageReductions() {
        return this.damageReductions;
    }

    public float getItemDamageThreshold() {
        return this.itemDamageThreshold;
    }

    public float getItemDamageBase() {
        return this.itemDamageBase;
    }

    public float getItemDamageFactor() {
        return this.itemDamageFactor;
    }

    public Sound getBlockSound() {
        return this.blockSound;
    }

    public Sound getDisabledSound() {
        return this.disabledSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.BlocksAttacksComponent blocksAttacks = itemMeta.getBlocksAttacks();
            blocksAttacks.setBlockDelaySeconds(this.blockDelaySeconds);
            blocksAttacks.setDisableCooldownScale(this.disableCooldownScale);

            for (ZDamageReductionRecord record : this.damageReductions) {
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
