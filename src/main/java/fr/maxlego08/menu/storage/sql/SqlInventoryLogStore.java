package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.InventoryLogStore;
import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Schema;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

final class SqlInventoryLogStore implements InventoryLogStore {

    private final SqlDatabase database;

    SqlInventoryLogStore(SqlDatabase database) {
        this.database = database;
    }

    @Override
    public @NotNull CompletableFuture<Void> append(@NotNull List<InventoryOpenRecord> records) {
        if (records.isEmpty()) return CompletableFuture.completedFuture(null);

        List<Schema> schemas = new ArrayList<>(records.size());
        for (InventoryOpenRecord record : records) {
            schemas.add(SchemaBuilder.insert(Tables.PLAYER_OPEN_INVENTORIES, table -> {
                table.uuid("player_id", record.playerId());
                table.string("plugin", record.plugin());
                table.string("inventory", record.inventory());
                table.bigInt("page", record.page());
                table.string("old_inventories", String.join(",", record.previousInventories()));
            }));
        }
        return this.database.write(() -> this.database.requestHelper().insertMultiple(schemas));
    }
}
