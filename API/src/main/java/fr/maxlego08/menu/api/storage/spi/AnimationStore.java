package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * When each player last saw the slot animations that remember themselves (played once, or with a
 * cooldown).
 */
@ApiStatus.Experimental
public interface AnimationStore {

    /**
     * @return the time each animation last played, in milliseconds since the epoch
     */
    @NotNull
    CompletableFuture<Map<String, Long>> load(@NotNull UUID playerId);

    @NotNull
    CompletableFuture<Void> markPlayed(@NotNull UUID playerId, @NotNull String animationKey, long playedAt);

    /**
     * @param animationKey the animation to forget, or null to forget all of them
     */
    @NotNull
    CompletableFuture<Void> clear(@NotNull UUID playerId, @Nullable String animationKey);

    /**
     * @return every player who saw an animation, for {@link StorageCapability#EXPORT}
     */
    @NotNull
    default CompletableFuture<Set<UUID>> players() {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("This store cannot list its players"));
    }
}
