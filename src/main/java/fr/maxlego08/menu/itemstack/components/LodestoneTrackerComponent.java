package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record LodestoneTrackerComponent(
        boolean lodestoneTracked,
        @Nullable Location lodestoneLocation
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CompassMeta.class, compassMeta -> {
            compassMeta.setLodestoneTracked(this.lodestoneTracked);
            compassMeta.setLodestone(this.lodestoneLocation);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply LodestoneTrackerComponent to itemStack: " + itemStack.getType().name());
    }
}
