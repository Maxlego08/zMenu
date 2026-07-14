package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
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

@SuppressWarnings("unused")
public class LodestoneTrackerComponent extends ItemComponent {
    private final @NotNull ResolvableBoolean lodestoneTracked;
    private final @Nullable ResolvableLodestoneLocation lodestoneLocation;

    public LodestoneTrackerComponent(@NotNull ResolvableBoolean lodestoneTracked, @Nullable ResolvableLodestoneLocation lodestoneLocation) {
        this.lodestoneTracked = lodestoneTracked;
        this.lodestoneLocation = lodestoneLocation;
    }

    public @NotNull ResolvableBoolean isLodestoneTracked() {
        return this.lodestoneTracked;
    }

    public @Nullable ResolvableLodestoneLocation getLodestoneLocation() {
        return this.lodestoneLocation;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CompassMeta.class, compassMeta -> {

            this.applyResolvable(context, compassMeta::setLodestoneTracked, this.lodestoneTracked);

            Resolvable.applyResolvable(context, this.lodestoneLocation, compassMeta::setLodestone);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply LodestoneTrackerComponent to itemStack: " + itemStack.getType().name());
    }
}
