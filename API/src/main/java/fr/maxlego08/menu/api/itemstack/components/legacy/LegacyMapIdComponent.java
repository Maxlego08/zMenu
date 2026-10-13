package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.MapIdComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.itemstack.ZMapView;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyMapIdComponent extends MapIdComponent {

    public LegacyMapIdComponent(ResolvableInt mapId) {
        super(mapId);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Integer value = this.getMapId().resolve(context);
        if (value == null) return;

        boolean apply = ItemUtil.editMeta(itemStack, MapMeta.class, mapMeta -> {
            MapView mapView = mapMeta.getMapView();
            if (mapView != null) {
                mapMeta.setMapView(new ZMapView(value, mapView));
            } else {
                mapMeta.setMapView(new ZMapView(value));
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MapIdComponent to itemStack: " + itemStack.getType().name());
    }
}
