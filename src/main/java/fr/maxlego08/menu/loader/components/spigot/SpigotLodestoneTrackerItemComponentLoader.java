package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.LodestoneTrackerComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class SpigotLodestoneTrackerItemComponentLoader extends ItemComponentLoader {

    public SpigotLodestoneTrackerItemComponentLoader(){
        super("lodestone-tracker");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if(componentSection == null) return null;
        boolean lodestoneTracked = componentSection.getBoolean("tracked", true);
        ConfigurationSection targetSection = componentSection.getConfigurationSection("target");
        Location targetLocation = null;
        if (targetSection != null){
            List<Integer> postIntArray = targetSection.getIntegerList("post");
            if (postIntArray.size() == 3){
                double x = postIntArray.get(0);
                double y = postIntArray.get(1);
                double z = postIntArray.get(2);
                String worldName = targetSection.getString("dimension");
                if (worldName != null){
                    try {
                        World world = Bukkit.getWorld(worldName);
                        targetLocation = new Location(world, x, y, z);
                    } catch (Exception e) { // Invalid world name
                    }
                }
            }
        }
        return new LodestoneTrackerComponent(lodestoneTracked, targetLocation);
    }
}
