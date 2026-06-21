package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
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

@SuppressWarnings("unused")
public class BannerPatternsComponent extends ItemComponent {
    private final @Nullable List<ResolvableBannerPattern> resolvablePatterns;

    public BannerPatternsComponent(@Nullable List<ResolvableBannerPattern> resolvablePatterns) {
        this.resolvablePatterns = resolvablePatterns;
    }

    @Nullable
    public List<ResolvableBannerPattern> getPatterns() {
        return this.resolvablePatterns;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BannerMeta.class, bannerMeta -> {
            Resolvable.applyResolvable(context, this.resolvablePatterns, bannerMeta::setPatterns);
        });
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Unable to apply BannerPatternsComponent to item: " + itemStack);
        }
    }

}
