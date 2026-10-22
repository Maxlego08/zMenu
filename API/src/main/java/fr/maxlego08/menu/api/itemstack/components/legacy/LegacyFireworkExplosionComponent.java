package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.FireworkExplosionComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyFireworkExplosionComponent extends FireworkExplosionComponent {

    public LegacyFireworkExplosionComponent(@NotNull ResolvableFireworkEffect effect) {
        super(effect);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, FireworkEffectMeta.class, fireworkEffectMeta -> {
            Resolvable.applyResolvable(context, this.getEffect(), fireworkEffectMeta::setEffect);
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply FireworkExplosionComponent to itemStack: " + itemStack.getType().name());
        }
    }
}
