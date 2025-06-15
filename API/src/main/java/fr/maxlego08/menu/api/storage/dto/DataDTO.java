package fr.maxlego08.menu.api.storage.dto;

import java.util.Date;
import java.util.UUID;

public record DataDTO(UUID player_id, String key, String data, Date expired_at) {
}
