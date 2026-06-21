package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableLodestoneLocation extends Resolvable<Location> {

    private final @Nullable ResolvableInt x;
    private final @Nullable ResolvableInt y;
    private final @Nullable ResolvableInt z;
    private final @NotNull Resolvable<String> world;

    public ResolvableLodestoneLocation(
            @Nullable ResolvableInt x,
            @Nullable ResolvableInt y,
            @Nullable ResolvableInt z,
            @NotNull Resolvable<String> world
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    @Override
    public @Nullable Location resolve(@NotNull BuildContext context) {
        Integer xVal = this.x != null ? this.x.resolve(context) : null;
        Integer yVal = this.y != null ? this.y.resolve(context) : null;
        Integer zVal = this.z != null ? this.z.resolve(context) : null;
        if (xVal == null || yVal == null || zVal == null) return null;

        String worldName = this.world.resolve(context);
        if (worldName == null) return null;

        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;

        return new Location(world, xVal, yVal, zVal);
    }
}
