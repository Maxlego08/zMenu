package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

public class PlayerAnimationMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.PLAYER_ANIMATIONS, table -> {
            table.uuid("player_id").primary();
            table.string("animation_key", 255).primary();
            table.bigInt("played_at");
            table.timestamps();
        });
    }
}
