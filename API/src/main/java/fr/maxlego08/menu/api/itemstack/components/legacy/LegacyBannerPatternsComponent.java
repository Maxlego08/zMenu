package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.BannerPatternsComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableBannerPattern;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyBannerPatternsComponent extends BannerPatternsComponent {

    public LegacyBannerPatternsComponent(@Nullable List<ResolvableBannerPattern> resolvablePatterns) {
        super(resolvablePatterns);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BannerMeta.class, bannerMeta -> {
            Resolvable.applyResolvable(context, this.getPatterns(), bannerMeta::setPatterns);
        });
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Unable to apply BannerPatternsComponent to item: " + itemStack);
        }
    }
}
