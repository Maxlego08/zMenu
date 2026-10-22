package fr.maxlego08.menu.common.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PapiUtils extends TranslationHelper {

    private static final long MAXIMUM_CACHE_SIZE = 10_000;

    private static volatile Placeholder placeholder;
    private static volatile Cache<String, String> cache;
    private static volatile long cacheDuration = -1;

    private Placeholder use() {
        if (placeholder == null) {
            placeholder = Placeholder.Placeholders.getPlaceholder();
        }
        return placeholder;
    }

    private static Cache<String, String> getCache() {
        long duration = Configuration.cachePlaceholderAPI;
        Cache<String, String> current = cache;
        if (current != null && cacheDuration == duration) return current;

        synchronized (PapiUtils.class) {
            current = cache;
            if (current == null || cacheDuration != duration) {
                current = CacheBuilder.newBuilder()
                        .maximumSize(MAXIMUM_CACHE_SIZE)
                        .expireAfterWrite(Math.max(duration, 0), TimeUnit.MILLISECONDS)
                        .build();
                cacheDuration = duration;
                cache = current;
            }
            return current;
        }
    }

    public Object papi(Object placeHolder, OfflinePlayer player, boolean useCache) {
        return placeHolder instanceof String ? this.papi((String) placeHolder, player, useCache) : placeHolder;
    }

    public String papi(String placeHolder, OfflinePlayer player, boolean useCache) {
        if (placeHolder == null) return null;
        if (player == null) return placeHolder;
        if (!placeHolder.contains("%")) return placeHolder;

        if (!Configuration.enableCachePlaceholderAPI) {
            return this.parse(placeHolder, player);
        }

        Cache<String, String> cache = getCache();
        String cacheKey = placeHolder + ";" + player.getUniqueId();

        if (useCache) {
            String cachedResult = cache.getIfPresent(cacheKey);
            if (cachedResult != null) return cachedResult;
        }

        String result = this.parse(placeHolder, player);
        cache.put(cacheKey, result);
        return result;
    }

    private String parse(String placeHolder, OfflinePlayer player) {
        String result = this.use().setPlaceholders(player, placeHolder);
        String playerName = player.getName();
        return playerName == null ? result : result.replace("%player%", playerName);
    }

    public List<String> papi(List<String> placeHolders, OfflinePlayer player, boolean useCache) {
        if (player == null) return placeHolders;
        List<String> parsed = new ArrayList<>(placeHolders.size());
        for (String placeHolder : placeHolders) {
            parsed.add(this.papi(placeHolder, player, useCache));
        }
        return parsed;
    }
}
