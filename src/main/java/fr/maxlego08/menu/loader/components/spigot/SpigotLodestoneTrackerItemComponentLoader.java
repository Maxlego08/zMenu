package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LodestoneTrackerComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableLodestoneLocation;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotLodestoneTrackerItemComponentLoader extends ItemComponentLoader {

    public SpigotLodestoneTrackerItemComponentLoader(){
        super("lodestone-tracker");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableBoolean lodestoneTracked = this.asResolvableBoolean(componentSection, "tracked", true);
        ResolvableLodestoneLocation lodestoneLocation = this.parseTarget(componentSection.getConfigurationSection("target"));

        return new LodestoneTrackerComponent(lodestoneTracked, lodestoneLocation);
    }

    private @Nullable ResolvableLodestoneLocation parseTarget(@Nullable ConfigurationSection targetSection) {
        if (targetSection == null) return null;

        List<?> postList = targetSection.getList("post");
        if (postList == null || postList.size() < 3) return null;

        ResolvableInt x = toResolvableInt(postList.get(0), 0);
        ResolvableInt y = toResolvableInt(postList.get(1), 0);
        ResolvableInt z = toResolvableInt(postList.get(2), 0);

        Resolvable<String> world = toResolvableString(targetSection, "dimension", "world");

        return new ResolvableLodestoneLocation(x, y, z, world);
    }

    private static @NotNull ResolvableInt toResolvableInt(@Nullable Object value, int defaultValue) {
        if (value instanceof Number number) return ResolvableInt.of(number.intValue());
        if (value instanceof String expr) return ResolvableInt.of(expr);
        return ResolvableInt.of(defaultValue);
    }

    private static @NotNull Resolvable<String> toResolvableString(@NotNull ConfigurationSection section, @NotNull String key, @NotNull String defaultValue) {
        String value = section.getString(key);
        if (value == null || value.isEmpty()) return ResolvableString.of(defaultValue);
        return value.contains("%") ? ResolvableString.ofExpression(value) : ResolvableString.of(value);
    }
}
