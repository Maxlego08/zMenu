package fr.maxlego08.menu.api.storage.dto;

import java.util.UUID;

/**
 * @deprecated a row of the old {@code player_inventories} table; see
 * {@link fr.maxlego08.menu.api.storage.spi.SavedInventoryStore}.
 */
@Deprecated(since = "1.1.2.2", forRemoval = true)
public record InventoryDTO(UUID player_id, String inventory) {
}
