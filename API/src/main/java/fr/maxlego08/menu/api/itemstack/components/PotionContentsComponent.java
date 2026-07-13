package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class PotionContentsComponent extends ItemComponent {
    private final @Nullable ResolvableRegistryEntry<PotionType> basePotionType;
    private final @Nullable Resolvable<String> customName;
    private final @Nullable ResolvableColor color;
    private final @NotNull List<ResolvablePotionEffect> potionEffects;

    public PotionContentsComponent(@Nullable ResolvableRegistryEntry<PotionType> basePotionType, @Nullable Resolvable<String> customName, @Nullable ResolvableColor color, @NotNull List<ResolvablePotionEffect> potionEffects) {
        this.basePotionType = basePotionType;
        this.customName = customName;
        this.color = color;
        this.potionEffects = potionEffects;
    }

    public @Nullable ResolvableRegistryEntry<PotionType> getBasePotionType() {
        return this.basePotionType;
    }

    public @Nullable Resolvable<String> getCustomName() {
        return this.customName;
    }

    public @Nullable ResolvableColor getColor() {
        return this.color;
    }

    public @NotNull List<ResolvablePotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, PotionMeta.class, potionMeta -> {
            this.applyResolvable(context, potionMeta::setBasePotionType, this.basePotionType);

            this.applyResolvable(context, potionMeta::setCustomName, this.customName);

            this.applyResolvable(context, potionMeta::setColor, this.color);

            for (ResolvablePotionEffect resolvableEffect : this.potionEffects) {
                PotionEffect effect = resolvableEffect.resolve(context);
                if (effect != null) {
                    potionMeta.addCustomEffect(effect, true);
                }
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("PotionContentsComponent couldn't be applied to " + itemStack.getType().name());
    }
}
