package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class FirstWorldJoinMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.FIRST_WORLD_JOIN, table -> {
            table.uuid("player_id").primary();
            table.string("world_name", 255).primary();
            table.timestamps();
        });
    }
}
