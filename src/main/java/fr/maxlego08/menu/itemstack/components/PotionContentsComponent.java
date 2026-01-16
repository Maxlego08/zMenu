package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PotionContentsComponent(
    @Nullable PotionType basePotionType,
    @Nullable String customName,
    @Nullable Color color,
    @NotNull List<@NotNull PotionEffect> potionEffects
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, PotionMeta.class, potionMeta -> {
            potionMeta.setBasePotionType(this.basePotionType);
            potionMeta.setCustomName(this.customName);
            potionMeta.setColor(this.color);
            for (PotionEffect potionEffect : this.potionEffects) {
                potionMeta.addCustomEffect(potionEffect, true);
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("PotionContentsComponent couldn't be applied to " + itemStack.getType().name());
    }
}
