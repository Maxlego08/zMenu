package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.BaseColorComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyBaseColorComponent extends BaseColorComponent {

    public LegacyBaseColorComponent(@NotNull Resolvable<DyeColor> baseColorResolvable) {
        super(baseColorResolvable);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        DyeColor color = Resolvable.resolve(context, this.getBaseColorResolvable());
        if (color == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, BlockStateMeta.class, blockStateMeta -> {
            if (blockStateMeta.getBlockState() instanceof Banner banner) {
                banner.setBaseColor(color);
                blockStateMeta.setBlockState(banner);
            }
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply BaseColorComponent to item: " + itemStack.getType().name());
        }
    }
}
