package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.TeleportAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;

public class TeleportLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public TeleportLoader(MenuPlugin plugin) {
        super("teleport", "tp");
        this.plugin = plugin;
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String world = accessor.getString("world", "world");
        double x = Double.parseDouble(accessor.getString("x", "0.0"));
        double y = Double.parseDouble(accessor.getString("y", "0.0"));
        double z = Double.parseDouble(accessor.getString("z", "0.0"));
        float yaw = Float.parseFloat(accessor.getString("yaw", "0.0"));
        float pitch = Float.parseFloat(accessor.getString("pitch", "0.0"));
        return new TeleportAction(this.plugin, new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }
}
