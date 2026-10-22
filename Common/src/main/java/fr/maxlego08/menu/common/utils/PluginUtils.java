package fr.maxlego08.menu.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public final class PluginUtils {

    private PluginUtils() {
    }

    /**
     * Finds a plugin by its name, whatever its case.
     *
     * @param pluginName the name of the plugin, may be null
     * @return the plugin, if one has this name
     */
    public static Optional<Plugin> getPluginIgnoreCase(String pluginName) {
        if (pluginName == null) {
            return Optional.empty();
        }
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(pluginName)) {
                return Optional.of(plugin);
            }
        }
        return Optional.empty();
    }
}
