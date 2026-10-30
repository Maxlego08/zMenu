package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.dto.DataDTO;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import fr.maxlego08.menu.api.storage.spi.PlayerDataStore;
import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class SqlPlayerDataStore implements PlayerDataStore {

    private final SqlDatabase database;

    SqlPlayerDataStore(SqlDatabase database) {
        this.database = database;
    }

    private static List<PlayerDataEntry> toEntries(List<DataDTO> rows) {
        List<PlayerDataEntry> entries = new ArrayList<>(rows.size());
        for (DataDTO row : rows) {
            if (row.player_id() == null || row.key() == null) continue;
            entries.add(new PlayerDataEntry(row.player_id(), row.key(), row.data() == null ? "" : row.data(), row.expired_at() == null ? 0 : row.expired_at().getTime()));
        }
        return entries;
    }

    @Override
    public @NotNull CompletableFuture<List<PlayerDataEntry>> loadAll() {
        return this.database.read(() -> toEntries(this.database.requestHelper().selectAll(Tables.PLAYER_DATAS, DataDTO.class)));
    }

    @Override
    public @NotNull CompletableFuture<List<PlayerDataEntry>> load(@NotNull UUID playerId) {
        return this.database.read(() -> toEntries(this.database.requestHelper().select(Tables.PLAYER_DATAS, DataDTO.class, table -> table.where("player_id", playerId))));
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> keys() {
        return this.database.read(() -> {
            Set<String> keys = new HashSet<>();
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.PLAYER_DATAS, table -> {
                table.distinct();
                table.addSelect("key");
            })) {
                String key = SqlRows.text(row, "key");
                if (key != null) keys.add(key);
            }
            return keys;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> save(@NotNull List<PlayerDataEntry> entries) {
        if (entries.isEmpty()) return CompletableFuture.completedFuture(null);

        List<Schema> schemas = new ArrayList<>(entries.size());
        for (PlayerDataEntry entry : entries) {
            schemas.add(SchemaBuilder.upsert(Tables.PLAYER_DATAS, table -> {
                table.uuid("player_id", entry.playerId()).primary();
                table.string("key", entry.key()).primary();
                table.string("data", entry.value());
                table.object("expired_at", entry.expiresAt() == 0 ? null : new Date(entry.expiresAt()));
            }));
        }
        return this.database.write(() -> this.database.requestHelper().upsertMultiple(schemas));
    }

    @Override
    public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId, @NotNull String key) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_DATAS, table -> {
            table.where("player_id", playerId);
            table.where("key", key);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> deletePlayer(@NotNull UUID playerId) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_DATAS, table -> table.where("player_id", playerId)));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteKey(@NotNull String key) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_DATAS, table -> table.where("key", key)));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteAll() {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_DATAS, table -> {
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteExpired(long now) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_DATAS, table -> table.where("expired_at", "<", new Date(now))));
    }
}
