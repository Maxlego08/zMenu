package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.PotionContentsComponent;
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

public class LegacyPotionContentsComponent extends PotionContentsComponent {

    public LegacyPotionContentsComponent(@Nullable ResolvableRegistryEntry<PotionType> basePotionType, @Nullable Resolvable<String> customName, @Nullable ResolvableColor color, @NotNull List<ResolvablePotionEffect> potionEffects) {
        super(basePotionType, customName, color, potionEffects);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, PotionMeta.class, potionMeta -> {
            Resolvable.applyResolvable(context, this.getBasePotionType(), potionMeta::setBasePotionType);
            Resolvable.applyResolvable(context, this.getColor(), potionMeta::setColor);
            for (ResolvablePotionEffect effect : this.getPotionEffects()) {
                PotionEffect resolvedEffect = Resolvable.resolve(context, effect);
                if (resolvedEffect != null) {
                    potionMeta.addCustomEffect(resolvedEffect, true);
                }
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply PotionContentsComponent to item: " + itemStack.getType().name());
    }
}
