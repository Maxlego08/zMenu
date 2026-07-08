package fr.maxlego08.menu.api.utils;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.maxlego08.menu.api.annotations.AutoListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@AutoListener
public class OfflinePlayerCache implements Listener {
    private static final Cache<UUID, OfflinePlayer> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    private static final BiMap<UUID, String> offlinesPlayers = HashBiMap.create();

    private static final Map<String, OfflinePlayer> offlinePlayer = new ConcurrentHashMap<>();

    private static boolean isInitialized = false;

    public OfflinePlayerCache() {
        if (!isInitialized) {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null) {
                    addToCache(offlinePlayer.getUniqueId(), offlinePlayer.getName());
                }
            }
            isInitialized = true;
        }
    }

    /**
     * Get the OfflinePlayer object with cache.
     *
     * @param playerName the player name.
     */
    @NotNull
    public static OfflinePlayer get(@NotNull String playerName) {
        return offlinePlayer.computeIfAbsent(playerName, name -> {
            Player player = Bukkit.getPlayer(name);
            return player != null ? player : Bukkit.getOfflinePlayer(name);
        });
    }

    @NotNull
    public static OfflinePlayer get(@NotNull UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId cannot be null");
        OfflinePlayer offlinePlayer = cache.getIfPresent(playerId);
        if (offlinePlayer == null) {
            offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            cache.put(playerId, offlinePlayer);
        }
        return offlinePlayer;
    }

    public static String getName(@NotNull UUID playerId) {
        Preconditions.checkNotNull(playerId, "playerId cannot be null");
        return offlinesPlayers.get(playerId);
    }

    public static UUID getUUID(@NotNull String playerName) {
        Preconditions.checkNotNull(playerName, "playerName cannot be null");
        return offlinesPlayers.inverse().get(playerName);
    }

    public static void clearCache(){
        offlinePlayer.clear();
    }

    private static void addToCache(@NotNull UUID playerId, @NotNull String playerName) {
        Preconditions.checkNotNull(playerId, "playerId cannot be null");
        Preconditions.checkNotNull(playerName, "playerName cannot be null");
        offlinesPlayers.put(playerId, playerName);
    }

    public static CompletableFuture<Suggestions> suggestPlayerNames(@NotNull SuggestionsBuilder builder) {
        Preconditions.checkNotNull(builder, "builder cannot be null");
        String remaining = builder.getRemainingLowerCase();
        for (String playerName : offlinesPlayers.values()) {
            if (playerName.startsWith(remaining)) {
                builder.suggest(playerName);
            }
        }
        return builder.buildFuture();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            addToCache(player.getUniqueId(), player.getName());
        } else {
            if (!Objects.equals(offlinesPlayers.get(player.getUniqueId()), player.getName())) {
                addToCache(player.getUniqueId(), player.getName());
            }
        }
    }


}
