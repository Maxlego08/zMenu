package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.SavedInventoryStore;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

final class SqlSavedInventoryStore implements SavedInventoryStore {

    private final SqlDatabase database;

    SqlSavedInventoryStore(SqlDatabase database) {
        this.database = database;
    }

    @Override
    public @NotNull CompletableFuture<Map<UUID, String>> loadAll() {
        return this.database.read(() -> {
            List<Map<String, Object>> rows = this.database.requestHelper().select(Tables.PLAYER_INVENTORIES, table -> {
            });
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
            String inventory = null;
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.PLAYER_INVENTORIES, table -> table.where("player_id", playerId))) {
                inventory = SqlRows.text(row, "inventory");
            }
            return Optional.ofNullable(inventory);
        });
    }

    /**
     * Replaces the saved inventory of a player. The table has no key on the player, so the old row
     * is deleted first; both run on the writer thread, one after the other.
     */
    @Override
    public @NotNull CompletableFuture<Void> save(@NotNull UUID playerId, @NotNull String inventory) {
        return this.database.write(() -> {
            this.database.requestHelper().delete(Tables.PLAYER_INVENTORIES, table -> table.where("player_id", playerId));
            this.database.requestHelper().insert(Tables.PLAYER_INVENTORIES, table -> {
                table.uuid("player_id", playerId);
                table.string("inventory", inventory);
            });
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> delete(@NotNull UUID playerId) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.PLAYER_INVENTORIES, table -> table.where("player_id", playerId)));
    }
}
