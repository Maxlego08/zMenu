package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.SqliteConnection;
import fr.maxlego08.sarah.database.DatabaseType;
import fr.maxlego08.sarah.logger.JULogger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The values of the old {@code player_datas} table are copied into {@code variables} once.
 */
class LegacyPlayerDataCopyTest {

    @TempDir
    File folder;

    @Test
    void oldPlayerDataIsCopiedOnce() {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "SQLITE");
        SqlStorageProvider provider = new SqlStorageProvider();
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"))).join();

        var logger = JULogger.from(Logger.getLogger("zMenu-test"));
        RequestHelper helper = new RequestHelper(new SqliteConnection(new DatabaseConfiguration("zmenu_", "", "", 0, "", "", false, DatabaseType.SQLITE), this.folder, logger), logger);

        UUID player = UUID.randomUUID();
        long expiresAt = 4_102_444_800_000L;
        for (int index = 0; index < 1200; index++) {
            int number = index;
            helper.insert(Tables.PLAYER_DATAS, table -> {
                table.uuid("player_id", UUID.randomUUID());
                table.string("key", "key" + number);
                table.string("data", String.valueOf(number));
            });
        }
        helper.insert(Tables.PLAYER_DATAS, table -> {
            table.uuid("player_id", player);
            table.string("key", "rank");
            table.string("data", "vip");
            table.object("expired_at", new Date(expiresAt));
        });

        assertEquals(1201, SqlStorageProvider.copyLegacyPlayerData(helper));
        assertEquals(0, SqlStorageProvider.copyLegacyPlayerData(helper));

        Map<String, Variable> variables = new HashMap<>();
        provider.variables().loadOwner(VariableScope.PLAYER, player.toString()).join().forEach(variable -> variables.put(variable.ref().name(), variable));
        assertEquals("vip", variables.get("rank").text());
        assertNull(variables.get("rank").number());
        assertEquals(expiresAt, variables.get("rank").expiresAt());

        provider.close().join();
    }
}
