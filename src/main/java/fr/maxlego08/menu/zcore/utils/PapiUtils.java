package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import fr.maxlego08.menu.api.configuration.Config;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PapiUtils extends TranslationHelper {

    private static volatile Placeholder placeholder;
    private transient final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private Placeholder use() {
        if (placeholder == null) {
            placeholder = Placeholder.Placeholders.getPlaceholder();
        }
        return placeholder;
    }

    public Object papi(Object placeHolder, OfflinePlayer player, boolean useCache) {
        return placeHolder instanceof String ? papi((String) placeHolder, player, useCache) : placeHolder;
    }

    public String papi(String placeHolder, OfflinePlayer player, boolean useCache) {
        if (placeHolder == null) return null;
        if (player == null) return placeHolder;
        if (!placeHolder.contains("%")) return placeHolder;
        String cacheKey = placeHolder + ";" + player.getUniqueId();
        CacheEntry cachedResult = cache.get(cacheKey);

        if (Config.enableCachePlaceholderAPI && cachedResult != null && cachedResult.isValid() && useCache) {
            return cachedResult.value;
        }

        String result = this.use().setPlaceholders(player, placeHolder).replace("%player%", player.getName());

        if (Config.enableCachePlaceholderAPI) {
            this.cache.put(cacheKey, new CacheEntry(result, System.currentTimeMillis()));
        }

        return result;
    }

    public List<String> papi(List<String> placeHolders, OfflinePlayer player, boolean useCache) {
        if (player == null) return placeHolders;
        return placeHolders.stream().map(placeHolder -> papi(placeHolder, player, useCache)).collect(Collectors.toList());
    }

    /**
     * @param timeStamp Time when the cache entry was created
     */
    private record CacheEntry(String value, long timeStamp) {

        public boolean isValid() {
                // Check if the cache entry is still valid (not older than 1000 milliseconds)
                return System.currentTimeMillis() - timeStamp < Config.cachePlaceholderAPI;
            }
        }
}
