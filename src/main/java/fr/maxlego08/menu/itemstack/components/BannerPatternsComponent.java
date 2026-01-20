package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class BannerPatternsComponent extends ItemComponent {
    private final @NotNull List<@NotNull Pattern> patterns;

    public BannerPatternsComponent(@NotNull List<@NotNull Pattern> patterns) {
        this.patterns = patterns;
    }

    public @NotNull List<@NotNull Pattern> getPatterns() {
        return this.patterns;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, BannerMeta.class, bannerMeta -> bannerMeta.setPatterns(this.patterns));
        if (!apply) {
            if (Configuration.enableDebug)
                Logger.info("Unable to apply BannerPatternsComponent to item: " + itemStack);
        }
    }
}
