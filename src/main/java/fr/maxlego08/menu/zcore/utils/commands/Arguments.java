package fr.maxlego08.menu.zcore.utils.commands;

import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class Arguments extends ZUtils {

    protected String[] args;
    protected int parentCount = 0;

    protected String argAsString(int index) {
        try {
            return this.args[index + this.parentCount];
        } catch (Exception e) {
            return null;
        }
    }

    protected String argAsString(int index, String defaultValue) {
        try {
            return this.args[index + this.parentCount];
        } catch (Exception e) {
            return defaultValue;
        }
    }


    protected boolean argAsBoolean(int index) {
        return Boolean.parseBoolean(argAsString(index));
    }

    protected boolean argAsBoolean(int index, boolean defaultValue) {
        try {
            return Boolean.valueOf(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected int argAsInteger(int index) {
        return Integer.parseInt(argAsString(index));
    }

    protected int argAsInteger(int index, int defaultValue) {
        try {
            return Integer.valueOf(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected long argAsLong(int index) {
        return Long.parseLong(argAsString(index));
    }

    protected long argAsLong(int index, long defaultValue) {
        try {
            return Long.parseLong(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected double argAsDouble(int index, double defaultValue) {
        try {
            return Double.parseDouble(argAsString(index).replace(",", "."));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected double argAsDouble(int index) {
        return Double.parseDouble(argAsString(index).replace(",", "."));
    }

    protected Player argAsPlayer(int index) {
        return Bukkit.getPlayer(argAsString(index));
    }

    protected Player argAsPlayer(int index, Player defaultValue) {
        try {
            return Bukkit.getPlayer(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected OfflinePlayer argAsOfflinePlayer(int index) {
        return OfflinePlayerCache.get(argAsString(index));
    }

    protected OfflinePlayer argAsOfflinePlayer(int index, OfflinePlayer defaultValue) {
        try {
            return OfflinePlayerCache.get(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected Location argAsLocation(int index) {
        return changeStringLocationToLocationEye(argAsString(index));
    }

    protected Location argAsLocation(int index, Location defaultValue) {
        try {
            return changeStringLocationToLocationEye(argAsString(index));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected EntityType argAsEntityType(int index) {
        return EntityType.valueOf(argAsString(index).toUpperCase());
    }

    protected EntityType argAsEntityType(int index, EntityType defaultValue) {
        try {
            return EntityType.valueOf(argAsString(index).toUpperCase());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    protected World argAsWorld(int index) {
        try {
            return Bukkit.getWorld(argAsString(index));
        } catch (Exception e) {
            return null;
        }
    }

    protected World argAsWorld(int index, World world) {
        try {
            return Bukkit.getWorld(argAsString(index));
        } catch (Exception e) {
            return world;
        }
    }
}
