package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import fr.maxlego08.sarah.RequestHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The inventories saved on this server. Each server keeps its own: items taken on one server are
 * only given back on that server, never on another one sharing the database.
 */
final class SqlSavedInventoryStore implements SavedInventoryStore {

    private final SqlDatabase database;
    private final String serverId;

    SqlSavedInventoryStore(SqlDatabase database, String serverId) {
        this.database = database;
        this.serverId = serverId;
    }

    /**
     * Copies the rows of the old {@code player_inventories} table, which had no server, to the
     * first server that starts with this version. Only done while {@code saved_inventories} is
     * empty, in one transaction; the old table is kept.
     *
     * @return how many inventories were copied
     */
    static int copyLegacyInventories(RequestHelper requestHelper, String serverId) {
        if (requestHelper.count(Tables.SAVED_INVENTORIES, table -> {
        }) > 0) return 0;
        if (requestHelper.count(Tables.PLAYER_INVENTORIES, table -> {
        }) == 0) return 0;

        return requestHelper.inTransaction(helper -> {
            Map<String, String> inventories = new HashMap<>();
            for (Map<String, Object> row : helper.select(Tables.PLAYER_INVENTORIES, table -> {
            })) {
                String playerId = SqlRows.text(row, "player_id");
                String inventory = SqlRows.text(row, "inventory");
                if (playerId != null && inventory != null) inventories.put(playerId, inventory);
            }
            inventories.forEach((playerId, inventory) -> helper.upsert(Tables.SAVED_INVENTORIES, table -> {
                table.string("server_id", serverId).primary();
                table.string("player_id", playerId).primary();
                table.string("inventory", inventory);
            }));
            return inventories.size();
        });
    }

    @Override
    public @NotNull CompletableFuture<Map<UUID, String>> loadAll() {
        return this.database.read(() -> {
            List<Map<String, Object>> rows = this.database.requestHelper().select(Tables.SAVED_INVENTORIES, table -> table.where("server_id", this.serverId));
            Map<UUID, String> inventories = new HashMap<>();
            for (Map<String, Object> row : rows) {
                String playerId = SqlRows.text(row, "player_id");
                String inventory = SqlRows.text(row, "inventory");
                if (playerId == null || inventory == null) continue;
                inventories.put(UUID.fromString(playerId), inventory);
            }
            return inventories;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<String>> load(@NotNull UUID playerId) {
        return this.database.read(() -> {
            List<Map<String, Object>> rows = this.database.requestHelper().select(Tables.SAVED_INVENTORIES, table -> {
                table.where("server_id", this.serverId);
                table.where("player_id", playerId);
            });
            return rows.isEmpty() ? Optional.empty() : Optional.ofNullable(SqlRows.text(rows.get(0), "inventory"));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory) {
        return this.database.write(() -> this.database.requestHelper().upsert(Tables.SAVED_INVENTORIES, table -> {
            table.string("server_id", this.serverId).primary();
            table.uuid("player_id", playerId).primary();
            table.string("inventory", inventory);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.SAVED_INVENTORIES, table -> {
            table.where("server_id", this.serverId);
            table.where("player_id", playerId);
        }));
    }
}
