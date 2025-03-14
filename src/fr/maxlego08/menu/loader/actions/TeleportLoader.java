package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.ActionBarAction;
import fr.maxlego08.menu.requirement.actions.TeleportAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.File;

public class TeleportLoader implements ActionLoader {

    private final MenuPlugin plugin;

    public TeleportLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "teleport,tp";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String world = accessor.getString("world", "world");
        double x = accessor.getDouble("x", 0.0D);
        double y = accessor.getDouble("y", 0.0D);
        double z = accessor.getDouble("z", 0.0D);
        float yaw = accessor.getFloat("yaw", 0.0F);
        float pitch = accessor.getFloat("pitch", 0.0F);
        return new TeleportAction(this.plugin, new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
    }
}
