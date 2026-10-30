package fr.maxlego08.menu.storage.migrations;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.sarah.database.Migration;

/**
 * The variables of every scope. {@code number_value} is set for numeric values, so the database
 * can add to them; {@code expires_at} is 0 for a value that never expires.
 */
public class VariablesMigration extends Migration {
    @Override
    public void up() {
        this.create(Tables.VARIABLES, table -> {
            table.string("scope", 16).primary();
            table.string("owner", 64).primary();
            table.string("name", 255).primary();
            table.longText("text_value").nullable();
            table.decimal("number_value", 38, 10).nullable();
            table.bigInt("expires_at");
            table.bigInt("version");
            table.timestamps();
        });
        this.index(Tables.VARIABLES, "expires_at");
    }
}
