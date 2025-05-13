package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class PlayerDataMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.PLAYER_DATAS, table -> {
            table.uuid("player_id").primary();
            table.string("key", 255).primary();
            table.longText("data");
            table.timestamp("expired_at").nullable();
            table.timestamps();
        });
    }
}
