package fr.maxlego08.menu.api.storage.dto;

import java.util.UUID;

/**
 * The last time a player saw one animation.
 *
 * @param playerId     the player
 * @param animationKey the animation, as the inventory identifies it
 * @param playedAt     when it last played, in milliseconds since the epoch
 * @deprecated no longer used; see {@link fr.maxlego08.menu.api.storage.spi.AnimationStore}.
 */
@Deprecated(since = "1.1.2.2", forRemoval = true)
public record PlayerAnimationDTO(UUID playerId, String animationKey, long playedAt) {
}
