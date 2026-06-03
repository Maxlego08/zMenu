package fr.maxlego08.menu.common;

import fr.maxlego08.menu.api.annotations.PaperOnly;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.annotations.SpigotOnly;
import fr.maxlego08.menu.api.annotations.UntilVersion;
import fr.maxlego08.menu.api.configuration.annotation.RequiresPlugin;
import fr.maxlego08.menu.api.utils.PlatformType;
import org.bukkit.Bukkit;
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
        if (requiresPlugin != null && !Bukkit.getPluginManager().isPluginEnabled(requiresPlugin.value())) {
            return false;
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
