package fr.maxlego08.menu.api.storage.spi;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * One stored player data value.
 *
 * @param playerId  the player
 * @param key       the key
 * @param value     the value, as text
 * @param expiresAt when it expires, in milliseconds since the epoch, or 0 when it never does
 */
@ApiStatus.Experimental
public record PlayerDataEntry(@NotNull UUID playerId, @NotNull String key, @NotNull String value, long expiresAt) {
}
