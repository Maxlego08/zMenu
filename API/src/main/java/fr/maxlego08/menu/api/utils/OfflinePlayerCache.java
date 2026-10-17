package fr.maxlego08.menu.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OfflinePlayerCache {

    private static final Map<String, OfflinePlayer> offlinePlayer = new ConcurrentHashMap<>();

    /**
     * Get the OfflinePlayer object with cache.
     *
     * @param playerName the player name.
     */
    @NotNull
    public static OfflinePlayer get(@NotNull String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) return player;
        return offlinePlayer.computeIfAbsent(playerName, Bukkit::getOfflinePlayer);
    }

    public static void clearCache(){
        offlinePlayer.clear();
    }
}
