package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
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

@SuppressWarnings("unused")
public class SuspiciousStewEffectsComponent extends ItemComponent {

    private final @NotNull List<PotionEffect> effects;

    public SuspiciousStewEffectsComponent(@NotNull List<PotionEffect> effects) {
        this.effects = effects;
    }

    public @NotNull List<PotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, SuspiciousStewMeta.class, suspiciousStewMeta -> {
            for (PotionEffect effect : this.effects) {
                suspiciousStewMeta.addCustomEffect(effect, true);
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply SuspiciousStewEffectsComponent to item: " + itemStack.getType().name());
    }

}
