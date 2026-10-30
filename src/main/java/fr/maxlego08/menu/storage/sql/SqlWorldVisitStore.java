package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.WorldVisitStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class SqlWorldVisitStore implements WorldVisitStore {

    private final SqlDatabase database;

    SqlWorldVisitStore(SqlDatabase database) {
        this.database = database;
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> load(@NotNull UUID playerId) {
        return this.database.read(() -> {
            Set<String> worlds = new HashSet<>();
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.FIRST_WORLD_JOIN, table -> table.where("player_id", playerId))) {
                String world = SqlRows.text(row, "world_name");
                if (world != null) worlds.add(world);
            }
            return worlds;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> markVisited(@NotNull UUID playerId, @NotNull String worldName) {
        return this.database.write(() -> this.database.requestHelper().upsert(Tables.FIRST_WORLD_JOIN, table -> {
            table.uuid("player_id", playerId).primary();
            table.string("world_name", worldName).primary();
        }));
    }
}
