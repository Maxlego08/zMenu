package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class LodestoneTrackerComponent extends ItemComponent {
    private final boolean lodestoneTracked;
    private final @Nullable Location lodestoneLocation;

    public LodestoneTrackerComponent(boolean lodestoneTracked, @Nullable Location lodestoneLocation) {
        this.lodestoneTracked = lodestoneTracked;
        this.lodestoneLocation = lodestoneLocation;
    }

    public boolean isLodestoneTracked() {
        return this.lodestoneTracked;
    }

    public @Nullable Location getLodestoneLocation() {
        return this.lodestoneLocation;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CompassMeta.class, compassMeta -> {
            compassMeta.setLodestoneTracked(this.lodestoneTracked);
            compassMeta.setLodestone(this.lodestoneLocation);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply LodestoneTrackerComponent to itemStack: " + itemStack.getType().name());
    }
}
