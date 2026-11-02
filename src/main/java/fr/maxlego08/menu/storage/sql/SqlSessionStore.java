package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.SessionStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class SqlSessionStore implements SessionStore {

    private final SqlDatabase database;

    SqlSessionStore(SqlDatabase database) {
        this.database = database;
    }

    private static boolean flag(Object value) {
        if (value instanceof Boolean bool) return bool;
        if (value instanceof Number number) return number.intValue() != 0;
        return value != null && (value.toString().equalsIgnoreCase("true") || value.toString().equals("1"));
    }

    @Override
    public @NotNull CompletableFuture<Optional<PlayerSession>> load(@NotNull UUID playerId) {
        return this.database.read(() -> {
            List<Map<String, Object>> rows = this.database.requestHelper().select(Tables.PLAYER_SESSIONS, table -> table.where("player_id", playerId));
            if (rows.isEmpty()) return Optional.empty();
            Map<String, Object> row = rows.get(0);
            return Optional.of(new PlayerSession(playerId, SqlRows.text(row, "server_id"), flag(SqlRows.value(row, "online")), SqlRows.number(row, "heartbeat")));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> claim(@NotNull UUID playerId, @NotNull String serverId, long now) {
        return this.database.write(() -> this.database.requestHelper().upsert(Tables.PLAYER_SESSIONS, table -> {
            table.uuid("player_id", playerId).primary();
            table.string("server_id", serverId);
            table.bool("online", true);
            table.bigInt("heartbeat", now);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> release(@NotNull UUID playerId, @NotNull String serverId) {
        return this.database.write(() -> this.database.requestHelper().update(Tables.PLAYER_SESSIONS, table -> {
            table.bool("online", false);
            table.where("player_id", playerId);
            table.where("server_id", serverId);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Optional<ServerInstance>> loadServer(@NotNull String serverId) {
        return this.database.read(() -> {
            List<Map<String, Object>> rows = this.database.requestHelper().select(Tables.SERVERS, table -> table.where("server_id", serverId));
            if (rows.isEmpty()) return Optional.empty();
            Map<String, Object> row = rows.get(0);
            return Optional.of(new ServerInstance(serverId, SqlRows.text(row, "instance_id"), SqlRows.number(row, "heartbeat")));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> heartbeat(@NotNull String serverId, @NotNull String instanceId, long now) {
        return this.database.write(() -> {
            this.database.requestHelper().upsert(Tables.SERVERS, table -> {
                table.string("server_id", serverId).primary();
                table.string("instance_id", instanceId);
                table.bigInt("heartbeat", now);
            });
            this.database.requestHelper().update(Tables.PLAYER_SESSIONS, table -> {
                table.bigInt("heartbeat", now);
                table.where("server_id", serverId);
                table.where("online", true);
            });
        });
    }
}
