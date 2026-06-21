package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.ResolvableDamageReduction;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class BlocksAttacksComponent extends ItemComponent {
    private final ResolvableFloat blockDelaySeconds;
    private final ResolvableFloat disableCooldownScale;
    private final @Nullable List<ResolvableDamageReduction> resolvableDamageReductions;
    private final ResolvableFloat itemDamageThreshold;
    private final ResolvableFloat itemDamageBase;
    private final ResolvableFloat itemDamageFactor;
    private final ResolvableSound blockSound;
    private final ResolvableSound disabledSound;

    public BlocksAttacksComponent(
            ResolvableFloat blockDelaySeconds,
        ResolvableFloat disableCooldownScale,
        @Nullable List<ResolvableDamageReduction> resolvableDamageReductions,
        ResolvableFloat itemDamageThreshold,
        ResolvableFloat itemDamageBase,
        ResolvableFloat itemDamageFactor,
        ResolvableSound blockSound,
        ResolvableSound disabledSound
    ) {
        this.blockDelaySeconds = blockDelaySeconds;
        this.disableCooldownScale = disableCooldownScale;
        this.resolvableDamageReductions = resolvableDamageReductions;
        this.itemDamageThreshold = itemDamageThreshold;
        this.itemDamageBase = itemDamageBase;
        this.itemDamageFactor = itemDamageFactor;
        this.blockSound = blockSound;
        this.disabledSound = disabledSound;
    }

    public ResolvableFloat getBlockDelaySeconds() {
        return this.blockDelaySeconds;
    }

    public ResolvableFloat getDisableCooldownScale() {
        return this.disableCooldownScale;
    }

    public @Nullable List<ResolvableDamageReduction> getDamageReductions() {
        return this.resolvableDamageReductions;
    }

    public ResolvableFloat getItemDamageThreshold() {
        return this.itemDamageThreshold;
    }

    public ResolvableFloat getItemDamageBase() {
        return this.itemDamageBase;
    }

    public ResolvableFloat getItemDamageFactor() {
        return this.itemDamageFactor;
    }

    public ResolvableSound getBlockSound() {
        return this.blockSound;
    }

    public ResolvableSound getDisabledSound() {
        return this.disabledSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.BlocksAttacksComponent blocksAttacks = itemMeta.getBlocksAttacks();

            this.applyResolvable(context, blocksAttacks::setBlockDelaySeconds, this.blockDelaySeconds);

            this.applyResolvable(context, blocksAttacks::setDisableCooldownScale, this.disableCooldownScale);

            this.applyResolvable(context, blocksAttacks::setItemDamageThreshold, this.itemDamageThreshold);

            this.applyResolvable(context, blocksAttacks::setItemDamageBase, this.itemDamageBase);

            this.applyResolvable(context, blocksAttacks::setItemDamageFactor, this.itemDamageFactor);

            this.applyResolvable(context, blocksAttacks::setBlockSound, this.blockSound);

            this.applyResolvable(context, blocksAttacks::setDisableSound, this.disabledSound);

            if (this.resolvableDamageReductions != null) {
                for (ResolvableDamageReduction reduction : this.resolvableDamageReductions) {
                    reduction.applyTo(blocksAttacks, context);
                }
            }

            itemStack.setItemMeta(itemMeta);
        }
    }


}
