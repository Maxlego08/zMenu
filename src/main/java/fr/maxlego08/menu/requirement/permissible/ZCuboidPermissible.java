package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.CuboidPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.cuboid.Region;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ZCuboidPermissible extends CuboidPermissible {

    private final Region region;

    public ZCuboidPermissible(List<Action> denyActions, List<Action> successActions, Region region) {
        super(denyActions, successActions);
        this.region = region;
    }

    @Override
    public Region getRegion() {
        return this.region;
    }

    @Override
    public boolean isValid() {
        return this.region != null;
    }

    @Override
    public boolean hasPermission(@NonNull Player player, Button button, @NonNull InventoryEngine inventoryEngine, @NonNull Placeholders placeholders) {
        return this.region.contains(player.getLocation());
    }
}
