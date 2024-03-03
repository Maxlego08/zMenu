package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import fr.maxlego08.menu.save.Config;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static fr.maxlego08.menu.zcore.logger.Logger.getLogger;

public class PapiUtils extends TranslationHelper {

    private static volatile Placeholder placeholder;
    private transient final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    private Placeholder use() {
        if (placeholder == null) {
            placeholder = Placeholder.Placeholders.getPlaceholder();
        }
        return placeholder;
    }

    public String papi(String placeHolder, OfflinePlayer player) {
        if (placeHolder == null) return null;
        if (player == null) return placeHolder;
        if (!placeHolder.contains("%")) return placeHolder;
        getLogger().log("placeHolder "+placeHolder);
        String cacheKey = placeHolder + ";" + player.getUniqueId().toString();
        CacheEntry cachedResult = cache.get(cacheKey);

        if (cachedResult != null && cachedResult.isValid()) {
            return cachedResult.value;
        }

        String result = this.use().setPlaceholders(player, placeHolder).replace("%player%", player.getName());

        getLogger().log("result "+result);
        cache.put(cacheKey, new CacheEntry(result, System.currentTimeMillis()));
        return result;
    }
    public String papi(String placeHolder, Player player) {
        if (placeHolder == null) return null;
        if (player == null) return placeHolder;
        if (!placeHolder.contains("%")) return placeHolder;

        String cacheKey = placeHolder + ";" + player.getUniqueId().toString();
        CacheEntry cachedResult = cache.get(cacheKey);

        if (cachedResult != null && cachedResult.isValid()) {
            return cachedResult.value;
        }

        String result = this.use().setPlaceholders(player, placeHolder).replace("%player%", player.getName());

        cache.put(cacheKey, new CacheEntry(result, System.currentTimeMillis()));
        return result;
    }

    public List<String> papi(List<String> placeHolders, Player player) {
        if (player == null) return placeHolders;
        return placeHolders.stream().map(placeHolder -> papi(placeHolder, player)).collect(Collectors.toList());
    }
    public List<String> papi(List<String> placeHolders, OfflinePlayer player) {
        if (player == null) return placeHolders;
        return placeHolders.stream().map(placeHolder -> papi(placeHolder, player)).collect(Collectors.toList());
    }

    private static class CacheEntry {
        String value;
        long timeStamp; // Time when the cache entry was created

        public CacheEntry(String value, long timeStamp) {
            this.value = value;
            this.timeStamp = timeStamp;
        }

        public boolean isValid() {
            // Check if the cache entry is still valid (not older than 1000 milliseconds)
            return System.currentTimeMillis() - timeStamp < Config.cachePlaceholderAPI;
        }
    }
}
