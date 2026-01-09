package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.api.utils.cuboid.Cuboid;
import fr.maxlego08.menu.api.utils.cuboid.Region;
import fr.maxlego08.menu.requirement.permissible.ZCuboidPermissible;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CuboidPermissibleLoader extends PermissibleLoader {

    private final ButtonManager buttonManager;

    public CuboidPermissibleLoader(ButtonManager buttonManager) {
        super("cuboid");
        this.buttonManager = buttonManager;
    }

    @Override
    public Permissible load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {

        List<Action> denyActions = loadAction(this.buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(this.buttonManager, accessor, "success", path, file);

        var cuboids = accessor.getStringList("cuboids", new ArrayList<>());
        if (cuboids.isEmpty()) {
            return new ZCuboidPermissible(denyActions, successActions, null);
        }

        List<Cuboid> cuboidList = new ArrayList<>(cuboids.size());
        for (String cuboid : cuboids) {
            cuboidList.add(this.stringToCuboid(cuboid));
        }
        var region = new Region(cuboidList);

        return new ZCuboidPermissible(denyActions, successActions, region);
    }

    private Cuboid stringToCuboid(String string) {

        var values = string.split(",");
        var world = Bukkit.getWorld(values[0]);

        var first = new Location(world, Double.parseDouble(values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
        var second = new Location(world, Double.parseDouble(values[4]), Double.parseDouble(values[5]), Double.parseDouble(values[6]));

        return new Cuboid(first, second);
    }
}
