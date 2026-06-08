package fr.maxlego08.menu.common.utils.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class YamlFileCache {
    private static final Cache<Path, YamlFileCacheEntry> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

    public static Optional<YamlFileCacheEntry> getYamlFileEntry(Path path) {
        File file = path.toFile();

        if (!file.exists()) {
            cache.invalidate(path);
            return Optional.empty();
        }

        YamlFileCacheEntry entry = cache.getIfPresent(path);

        if (entry != null && entry.isUpToDate()) {
            return Optional.of(entry);
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        entry = new YamlFileCacheEntry(file, config);
        cache.put(path, entry);
        return Optional.of(entry);
    }

    public static Optional<YamlConfiguration> getYamlConfiguration(Path path) {
        return getYamlFileEntry(path).map(YamlFileCacheEntry::getYamlConfiguration);
    }

    public static void invalidateCache(Path path) {
        cache.invalidate(path);
    }

    public static void clearCache() {
        cache.invalidateAll();
    }



}
