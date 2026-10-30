package fr.maxlego08.menu.api.storage.dto;

import java.util.Date;
import java.util.UUID;

/**
 * @deprecated a row of the old {@code player_datas} table; player data lives in
 * {@link fr.maxlego08.menu.api.variable.Variable}s.
 */
@Deprecated(since = "1.1.2.2", forRemoval = true)
public record DataDTO(UUID player_id, String key, String data, Date expired_at) {
}
