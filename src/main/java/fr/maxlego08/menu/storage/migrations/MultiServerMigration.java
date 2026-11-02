package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

/**
 * The tables used when several servers share the database: inventories saved per server, the
 * events servers send each other, the server each player is on, and the running servers.
 */
public class MultiServerMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.SAVED_INVENTORIES, table -> {
            table.string("server_id", 64).primary();
            table.string("player_id", 36).primary();
            table.longText("inventory");
            table.timestamps();
        });
        this.create(Tables.STORAGE_EVENTS, table -> {
            table.autoIncrementBigInt("id");
            table.string("origin", 64);
            table.string("type", 32);
            table.string("scope", 16).nullable();
            table.string("owner", 64).nullable();
            table.string("name", 255).nullable();
            table.bigInt("created_at");
        });
        this.create(Tables.PLAYER_SESSIONS, table -> {
            table.string("player_id", 36).primary();
            table.string("server_id", 64);
            table.bool("online");
            table.bigInt("heartbeat");
        });
        this.create(Tables.SERVERS, table -> {
            table.string("server_id", 64).primary();
            table.string("instance_id", 36);
            table.bigInt("heartbeat");
        });
    }
}
