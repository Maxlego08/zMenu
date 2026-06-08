package fr.maxlego08.menu.test.common;

import fr.maxlego08.menu.api.annotations.*;

import fr.maxlego08.menu.api.utils.PlatformType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class VersionFilter {

    private VersionFilter() {
    }

    public static boolean passes(@NotNull Class<?> clazz) {
        if (clazz.isAnnotationPresent(PaperOnly.class) && !PlatformType.isPaper()) {
            return false;
        }
        if (clazz.isAnnotationPresent(SpigotOnly.class) && PlatformType.isPaper()) {
            return false;
        }

        RequiresPlugin requiresPlugin = clazz.getAnnotation(RequiresPlugin.class);
        if (requiresPlugin != null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(requiresPlugin.value());
            if (plugin == null || !plugin.isEnabled()) {
                return false;
            }

            String requiredVersionStr = requiresPlugin.version();
            if (!requiredVersionStr.isEmpty()) {
                PluginVersion pluginVersion = PluginVersion.parse(plugin.getDescription().getVersion());
                PluginVersion requiredVersion = PluginVersion.parse(requiredVersionStr);
                if (!requiresPlugin.type().compare(pluginVersion.compareTo(requiredVersion))) {
                    return false;
                }
            }
        }

        MinecraftVersion serverVersion = MinecraftVersion.getCurrentVersion();

        SinceVersion since = clazz.getAnnotation(SinceVersion.class);
        if (since != null) {
            MinecraftVersion minimum = MinecraftVersion.parse(since.value());
            if (!serverVersion.isAtLeast(minimum)) {
                return false;
            }
        }

        UntilVersion until = clazz.getAnnotation(UntilVersion.class);
        if (until != null) {
            MinecraftVersion maximum = MinecraftVersion.parse(until.value());
            return serverVersion.isAtMost(maximum);
        }

        return true;

    }

}
