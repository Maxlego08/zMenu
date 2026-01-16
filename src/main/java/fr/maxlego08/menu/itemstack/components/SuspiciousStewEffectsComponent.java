package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record SuspiciousStewEffectsComponent(
    @NotNull List<PotionEffect> effects
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, SuspiciousStewMeta.class, suspiciousStewMeta -> {
            for (PotionEffect effect : this.effects) {
                suspiciousStewMeta.addCustomEffect(effect, true);
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply SuspiciousStewEffectsComponent to item: " + itemStack.getType().name());
    }
}
