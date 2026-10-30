package fr.maxlego08.menu.storage.memory;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.storage.spi.AnimationStore;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import fr.maxlego08.menu.api.storage.spi.PlayerDataStore;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.menu.api.storage.spi.StorageCapability;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps zMenu's data in memory until the server stops. Used with {@code storage-type: NONE} and
 * on test servers.
 */
public final class MemoryStorageProvider implements StorageProvider {

    private static final CompletableFuture<Void> DONE = CompletableFuture.completedFuture(null);

    private final Map<UUID, Map<String, PlayerDataEntry>> playerData = new ConcurrentHashMap<>();
    private final Map<UUID, String> savedInventories = new ConcurrentHashMap<>();
    private final Map<UUID, Set<String>> worldVisits = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, Long>> animations = new ConcurrentHashMap<>();

    private final PlayerDataStore playerDataStore = new PlayerDataStore() {

        @Override
        public @NotNull CompletableFuture<List<PlayerDataEntry>> loadAll() {
            List<PlayerDataEntry> entries = new ArrayList<>();
            playerData.values().forEach(values -> entries.addAll(values.values()));
            return CompletableFuture.completedFuture(entries);
        }

        @Override
        public @NotNull CompletableFuture<List<PlayerDataEntry>> load(@NotNull UUID playerId) {
            Map<String, PlayerDataEntry> values = playerData.get(playerId);
            return CompletableFuture.completedFuture(values == null ? new ArrayList<>() : new ArrayList<>(values.values()));
        }

        @Override
        public @NotNull CompletableFuture<Set<String>> keys() {
            Set<String> keys = new HashSet<>();
            playerData.values().forEach(values -> keys.addAll(values.keySet()));
            return CompletableFuture.completedFuture(keys);
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteExpired(long now) {
            playerData.values().forEach(values -> values.values().removeIf(entry -> entry.expiresAt() != 0 && entry.expiresAt() < now));
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> save(@NotNull List<PlayerDataEntry> entries) {
            entries.forEach(entry -> playerData.computeIfAbsent(entry.playerId(), id -> new ConcurrentHashMap<>()).put(entry.key(), entry));
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId, @NotNull String key) {
            Map<String, PlayerDataEntry> values = playerData.get(playerId);
            if (values != null) values.remove(key);
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> deletePlayer(@NotNull UUID playerId) {
            playerData.remove(playerId);
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteKey(@NotNull String key) {
            playerData.values().forEach(values -> values.remove(key));
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> deleteAll() {
            playerData.clear();
            return DONE;
        }
    };

    private final SavedInventoryStore savedInventoryStore = new SavedInventoryStore() {

        @Override
        public @NotNull CompletableFuture<Map<UUID, String>> loadAll() {
            return CompletableFuture.completedFuture(new HashMap<>(savedInventories));
        }

        @Override
        public @NotNull CompletableFuture<Optional<String>> load(@NotNull UUID playerId) {
            return CompletableFuture.completedFuture(Optional.ofNullable(savedInventories.get(playerId)));
        }

        @Override
        public @NotNull CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory) {
            savedInventories.put(playerId, inventory);
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId) {
            savedInventories.remove(playerId);
            return DONE;
        }
    };

    private final WorldVisitStore worldVisitStore = new WorldVisitStore() {

        @Override
        public @NotNull CompletableFuture<Set<String>> load(@NotNull UUID playerId) {
            Set<String> worlds = worldVisits.get(playerId);
            return CompletableFuture.completedFuture(worlds == null ? new HashSet<>() : new HashSet<>(worlds));
        }

        @Override
        public @NotNull CompletableFuture<Void> markVisited(@NotNull UUID playerId, @NotNull String worldName) {
            worldVisits.computeIfAbsent(playerId, id -> ConcurrentHashMap.newKeySet()).add(worldName);
            return DONE;
        }
    };

    private final AnimationStore animationStore = new AnimationStore() {

        @Override
        public @NotNull CompletableFuture<Map<String, Long>> load(@NotNull UUID playerId) {
            Map<String, Long> played = animations.get(playerId);
            return CompletableFuture.completedFuture(played == null ? new HashMap<>() : new HashMap<>(played));
        }

        @Override
        public @NotNull CompletableFuture<Void> markPlayed(@NotNull UUID playerId, @NotNull String animationKey, long playedAt) {
            animations.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>()).put(animationKey, playedAt);
            return DONE;
        }

        @Override
        public @NotNull CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey) {
            Map<String, Long> played = animations.get(playerId);
            if (played == null) return DONE;
            if (animationKey == null) played.clear();
            else played.remove(animationKey);
            return DONE;
        }
    };

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key("memory");
    }

    @Override
    public @NotNull Set<StorageCapability> capabilities() {
        return EnumSet.noneOf(StorageCapability.class);
    }

    @Override
    public @NotNull CompletableFuture<Void> open(@NotNull StorageContext context) {
        return DONE;
    }

    @Override
    public @NotNull CompletableFuture<Void> close() {
        return DONE;
    }

    @Override
    public @NotNull PlayerDataStore playerData() {
        return this.playerDataStore;
    }

    @Override
    public @NotNull SavedInventoryStore savedInventories() {
        return this.savedInventoryStore;
    }

    @Override
    public @NotNull WorldVisitStore worldVisits() {
        return this.worldVisitStore;
    }

    @Override
    public @NotNull AnimationStore animations() {
        return this.animationStore;
    }

    /**
     * Opened inventories are not logged in memory.
     */
    @Override
    public @NotNull Optional<InventoryLogStore> inventoryLogs() {
        return Optional.empty();
    }
}
