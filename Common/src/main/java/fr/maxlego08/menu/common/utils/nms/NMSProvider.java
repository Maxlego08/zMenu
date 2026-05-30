package fr.maxlego08.menu.common.utils.nms;

import fr.maxlego08.menu.api.utils.ReflectionsCache;
import fr.maxlego08.menu.common.MinecraftVersion;
import fr.maxlego08.menu.nms.NMSHandler;
import fr.maxlego08.menu.nms.NMSVersion;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.Set;

public class NMSProvider {

    private static NMSHandler handler;

    public static NMSHandler getHandler() {
        if (handler == null) {
            handler = loadHandler();
        }
        return handler;
    }

    private static NMSHandler loadHandler() {
        MinecraftVersion current = MinecraftVersion.getCurrentVersion();
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("zMenu");
        if (plugin == null) return null;

        Reflections reflections = ReflectionsCache.getInstance().getOrCreate(plugin, "fr.maxlego08.menu.nms");
        Set<Class<?>> candidates = reflections.getTypesAnnotatedWith(NMSVersion.class);

        Class<? extends NMSHandler> bestMatch = null;
        MinecraftVersion bestVersion = null;

        for (Class<?> clazz : candidates) {
            if (!NMSHandler.class.isAssignableFrom(clazz)) continue;

            NMSVersion annotation = clazz.getAnnotation(NMSVersion.class);
            MinecraftVersion required = MinecraftVersion.parse(annotation.value());

            if (current.isAtLeast(required)) {
                if (bestVersion == null || required.isAfter(bestVersion)) {
                    bestVersion = required;
                    bestMatch = (Class<? extends NMSHandler>) clazz;
                }
            }
        }

        if (bestMatch != null) {
            try {
                return bestMatch.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                Logger.info("Could not instantiate NMS handler " + bestMatch.getName() + ": " + e.getMessage());
            }
        }

        return null;
    }
}

