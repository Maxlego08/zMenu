package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.itemstack.ZMapView;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MapIdComponent(
    int mapId
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, MapMeta.class, mapMeta -> {
            MapView mapView = mapMeta.getMapView();
            if (mapView != null) {
                mapMeta.setMapView(new ZMapView(this.mapId, mapView));
            } else {
                mapMeta.setMapView(new ZMapView(this.mapId));
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MapIdComponent to itemStack: " + itemStack.getType().name());
    }
}
