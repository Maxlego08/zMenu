package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.LodestoneTrackerComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableLodestoneLocation;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyLodestoneTrackerComponent extends LodestoneTrackerComponent {

    public LegacyLodestoneTrackerComponent(@NotNull ResolvableBoolean lodestoneTracked, @Nullable ResolvableLodestoneLocation lodestoneLocation) {
        super(lodestoneTracked, lodestoneLocation);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CompassMeta.class, compassMeta -> {

            this.applyResolvable(context, compassMeta::setLodestoneTracked, this.isLodestoneTracked());

            Resolvable.applyResolvable(context, this.getLodestoneLocation(), compassMeta::setLodestone);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply LodestoneTrackerComponent to itemStack: " + itemStack.getType().name());
    }
}
