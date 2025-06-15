package fr.maxlego08.menu.api.storage.dto;

import java.util.UUID;

public record InventoryDTO(UUID player_id, String inventory) {
}
