package fr.maxlego08.menu.api.storage.dto;

import java.util.UUID;

/**
 * @deprecated no longer used; see {@link fr.maxlego08.menu.api.storage.spi.WorldVisitStore}.
 */
@Deprecated(since = "1.1.2.2", forRemoval = true)
public record FirstWorldJoinDTO(UUID playerId, String worldName) {
}
