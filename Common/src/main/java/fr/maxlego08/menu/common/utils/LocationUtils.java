package fr.maxlego08.menu.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class LocationUtils extends PapiUtils {

    protected Location changeStringLocationToLocation(String string) {
        return changeStringLocationToLocationEye(string);
    }

    protected Location changeStringLocationToLocationEye(String string) {
        String[] locationArray = string.split(",");
        World w = Bukkit.getServer().getWorld(locationArray[0]);
        float x = Float.parseFloat(locationArray[1]);
        float y = Float.parseFloat(locationArray[2]);
        float z = Float.parseFloat(locationArray[3]);
        if (locationArray.length == 6) {
            float yaw = Float.parseFloat(locationArray[4]);
            float pitch = Float.parseFloat(locationArray[5]);
            return new Location(w, x, y, z, yaw, pitch);
        }
        return new Location(w, x, y, z);
    }

    protected String changeLocationToString(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    protected String changeLocationToStringEye(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    protected Chunk changeStringChuncToChunk(String chunk) {
        String[] a = chunk.split(",");
        World w = Bukkit.getServer().getWorld(a[0]);
        return w.getChunkAt(Integer.parseInt(a[1]), Integer.parseInt(a[2]));
    }

    protected String changeChunkToString(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

}
