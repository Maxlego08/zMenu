package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class PlayerOpenInventoryMigration extends Migration {

    @Override
    public void up() {
        this.create(Tables.PLAYER_OPEN_INVENTORIES, table -> {
            table.autoIncrement("id");
            table.uuid("player_id");
            table.string("plugin", 255);
            table.string("inventory", 255);
            table.integer("page");
            table.longText("old_inventories");
            table.timestamps();
        });
    }
}