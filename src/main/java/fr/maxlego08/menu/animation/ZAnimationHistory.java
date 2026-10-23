package fr.maxlego08.menu.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.slot.AnimationHistory;
import fr.maxlego08.menu.api.storage.StorageManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ZAnimationHistory implements AnimationHistory {

    private final MenuPlugin plugin;
    private final Map<UUID, Set<String>> playedThisSession = new ConcurrentHashMap<>();

    /**
     * When each player last saw the animations that remember themselves, read from the database
     * once on join. A player missing from this map has not been read back yet.
     */
    private final Map<UUID, Map<String, Long>> playedAt = new ConcurrentHashMap<>();

    public ZAnimationHistory(@NotNull MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load(@NotNull Player player) {
        UUID uniqueId = player.getUniqueId();
        StorageManager storageManager = this.plugin.getStorageManager();

        if (!storageManager.isEnable()) {
            this.playedAt.put(uniqueId, new ConcurrentHashMap<>());
            return;
        }

        this.plugin.getScheduler().runAsync(task -> {
            Map<String, Long> played = new ConcurrentHashMap<>(storageManager.loadPlayedAnimations(uniqueId));
            if (!player.isOnline()) return;

            this.playedAt.merge(uniqueId, played, (known, loaded) -> {
                loaded.forEach((key, playedAt) -> known.merge(key, playedAt, Math::max));
                return known;
            });
        });
    }

    @Override
    public boolean isLoaded(@NotNull Player player) {
        return this.playedAt.containsKey(player.getUniqueId());
    }

    @Override
    public void unload(@NotNull UUID uniqueId) {
        this.playedThisSession.remove(uniqueId);
        this.playedAt.remove(uniqueId);
    }

    @Override
    public boolean hasPlayedThisSession(@NotNull Player player, @NotNull String animationKey) {
        Set<String> played = this.playedThisSession.get(player.getUniqueId());
        return played != null && played.contains(animationKey);
    }

    @Override
    public void markPlayedThisSession(@NotNull Player player, @NotNull String animationKey) {
        this.playedThisSession.computeIfAbsent(player.getUniqueId(), key -> ConcurrentHashMap.newKeySet()).add(animationKey);
    }

    @Override
    public @Nullable Long getPlayedAt(@NotNull Player player, @NotNull String animationKey) {
        Map<String, Long> played = this.playedAt.get(player.getUniqueId());
        return played == null ? null : played.get(animationKey);
    }

    @Override
    public void markPlayed(@NotNull Player player, @NotNull String animationKey, long playedAt) {
        this.playedAt.computeIfAbsent(player.getUniqueId(), key -> new ConcurrentHashMap<>()).put(animationKey, playedAt);
        this.plugin.getStorageManager().markAnimationPlayed(player.getUniqueId(), animationKey, playedAt);
    }

    @Override
    public void forget(@NotNull UUID uniqueId, @Nullable String animationKey) {
        Map<String, Long> played = this.playedAt.get(uniqueId);
        if (played != null) {
            if (animationKey == null) played.clear();
            else played.remove(animationKey);
        }

        Set<String> session = this.playedThisSession.get(uniqueId);
        if (session != null) {
            if (animationKey == null) session.clear();
            else session.remove(animationKey);
        }

        this.plugin.getStorageManager().clearPlayedAnimations(uniqueId, animationKey);
    }
}
