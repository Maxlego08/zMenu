package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.loader.actions.TeleportLoader;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class TeleportAction extends Action {

    private final MenuPlugin plugin;
    private final Location location;

    public TeleportAction(MenuPlugin plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "teleport");
        String worldName = this.location.getWorld() == null ? null : this.location.getWorld().getName();
        if (worldName != null && !TeleportLoader.DEFAULT_WORLD.equals(worldName)) map.put("world", worldName);
        if (this.location.getX() != 0.0) map.put("x", this.location.getX());
        if (this.location.getY() != 0.0) map.put("y", this.location.getY());
        if (this.location.getZ() != 0.0) map.put("z", this.location.getZ());
        if (this.location.getYaw() != 0.0f) map.put("yaw", this.location.getYaw());
        if (this.location.getPitch() != 0.0f) map.put("pitch", this.location.getPitch());
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.plugin.getScheduler().runAtEntity(player, w -> this.plugin.getScheduler().teleportAsync(player, this.location));
    }
}
