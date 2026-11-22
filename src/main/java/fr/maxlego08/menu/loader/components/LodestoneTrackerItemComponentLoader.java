package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LodestoneTrackerComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyLodestoneTrackerComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableLodestoneLocation;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.Location;
import org.bukkit.World;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

@AutoComponentLoader
public class LodestoneTrackerItemComponentLoader extends ItemComponentLoader {

    public LodestoneTrackerItemComponentLoader(){
        super("lodestone-tracker");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableBoolean lodestoneTracked = this.asResolvableBoolean(componentSection, "tracked", true);
        ResolvableLodestoneLocation lodestoneLocation = this.parseTarget(componentSection.getSection("target"));

        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new LodestoneTrackerComponent(lodestoneTracked, lodestoneLocation)
                : new LegacyLodestoneTrackerComponent(lodestoneTracked, lodestoneLocation);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        LodestoneTracker tracker = itemStack.getData(DataComponentTypes.LODESTONE_TRACKER);
        if (tracker == null) return null;

        ResolvableLodestoneLocation lodestoneLocation = null;
        Location location = tracker.location();
        if (location != null) {
            World world = location.getWorld();
            if (world == null || Resolvable.isExpression(world.getName())) return null;
            lodestoneLocation = new ResolvableLodestoneLocation(
                    ResolvableInt.of(location.getBlockX()),
                    ResolvableInt.of(location.getBlockY()),
                    ResolvableInt.of(location.getBlockZ()),
                    ResolvableString.of(world.getName())
            );
        }
        return new LodestoneTrackerComponent(ResolvableBoolean.of(tracker.tracked()), lodestoneLocation);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof CompassMeta compassMeta)) return null;
        if (!compassMeta.hasLodestone() && !compassMeta.isLodestoneTracked()) return null;

        ResolvableLodestoneLocation lodestoneLocation = null;
        if (compassMeta.hasLodestone()) {
            Location location = compassMeta.getLodestone();
            if (location == null || !location.isWorldLoaded()) return null;
            World world = location.getWorld();
            if (world == null || Resolvable.isExpression(world.getName())) return null;
            lodestoneLocation = new ResolvableLodestoneLocation(
                    ResolvableInt.of(location.getBlockX()),
                    ResolvableInt.of(location.getBlockY()),
                    ResolvableInt.of(location.getBlockZ()),
                    ResolvableString.of(world.getName())
            );
        }
        return new LegacyLodestoneTrackerComponent(ResolvableBoolean.of(compassMeta.isLodestoneTracked()), lodestoneLocation);
    }

    private @Nullable ResolvableLodestoneLocation parseTarget(@Nullable Section targetSection) {
        if (targetSection == null) return null;

        List<?> postList = YamlSections.getList(targetSection, "pos");
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

    private static @NotNull Resolvable<String> toResolvableString(@NotNull Section section, @NotNull String key, @NotNull String defaultValue) {
        String value = section.getString(key);
        if (value == null || value.isEmpty()) return ResolvableString.of(defaultValue);
        return value.contains("%") ? ResolvableString.ofExpression(value) : ResolvableString.of(value);
    }
}
