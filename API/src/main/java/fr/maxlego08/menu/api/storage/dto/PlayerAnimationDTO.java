package fr.maxlego08.menu.api.storage.dto;

import java.util.UUID;

/**
 * The last time a player saw one animation.
 *
 * @param playerId     the player
 * @param animationKey the animation, as the inventory identifies it
 * @param playedAt     when it last played, in milliseconds since the epoch
 */
public record PlayerAnimationDTO(UUID playerId, String animationKey, long playedAt) {
}
