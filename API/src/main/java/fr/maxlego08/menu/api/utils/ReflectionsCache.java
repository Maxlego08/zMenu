package fr.maxlego08.menu.api.utils;

import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionsCache {

    private static ReflectionsCache instance;

    private final Map<CacheKey, Reflections> cache;

    private ReflectionsCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public static synchronized ReflectionsCache getInstance() {
        if (instance == null) {
            instance = new ReflectionsCache();
        }
        return instance;
    }

    public Reflections getOrCreate(Plugin plugin, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be null or empty");
        }

        ClassLoader classLoader = plugin.getClass().getClassLoader();
        CacheKey key = new CacheKey(packageName, classLoader);

        return cache.computeIfAbsent(key, k -> createReflections(classLoader, packageName));
    }

    private Reflections createReflections(ClassLoader classLoader, String packageName) {
        return new Reflections(new ConfigurationBuilder()
                .forPackage(packageName, classLoader)
                .addClassLoaders(classLoader)
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes));
    }

    private record CacheKey(String packageName, int classLoaderHash) {
        CacheKey(String packageName, ClassLoader classLoader) {
            this(packageName, System.identityHashCode(classLoader));
        }
    }
}
