package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.SuspiciousStewEffectsComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacySuspiciousStewEffectsComponent extends SuspiciousStewEffectsComponent {

    public LegacySuspiciousStewEffectsComponent(@NotNull List<ResolvablePotionEffect> effects) {
        super(effects);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, SuspiciousStewMeta.class, suspiciousStewMeta -> {
            for (ResolvablePotionEffect effect : this.getEffects()) {
                if (effect != null) {
                    PotionEffect resolvedEffect = effect.resolve(context);
                    if (resolvedEffect != null) {
                        suspiciousStewMeta.addCustomEffect(resolvedEffect, true);
                    }
                }
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply SuspiciousStewEffectsComponent to item: " + itemStack.getType().name());
    }
}
