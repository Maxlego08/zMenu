package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class PlayerInventoriesMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.PLAYER_INVENTORIES, table -> {
            table.uuid("player_id");
            table.longText("inventory");
            table.timestamps();
        });
    }
}
