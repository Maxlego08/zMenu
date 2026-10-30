package fr.maxlego08.menu;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.storage.ZStorageManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Player data is read when it is needed instead of for every player at startup.
 */
public class PlayerDataLoadingTest {

    private static ServerMock server;
    private static ZMenuPlugin plugin;
    private static ZStorageManager storage;

    @BeforeAll
    public static void init() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        storage = (ZStorageManager) plugin.getStorageManager();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void anOfflinePlayerIsOnlyInMemoryOnceLoaded() {
        UUID playerId = UUID.randomUUID();
        storage.getProvider().playerData().save(List.of(new PlayerDataEntry(playerId, "coins", "12", 0))).join();

        assertTrue(plugin.getDataManager().getPlayer(playerId).isEmpty());

        PlayerData playerData = plugin.getDataManager().loadPlayer(playerId).join();
        assertEquals("12", playerData.getData("coins").map(Data::getValue).map(Object::toString).orElse(null));
        assertTrue(plugin.getDataManager().getPlayer(playerId).isPresent());
    }

    @Test
    public void aJoiningPlayerIsLoaded() {
        PlayerMock player = new PlayerMock(server, "Loaded", UUID.randomUUID());
        storage.getProvider().playerData().save(List.of(new PlayerDataEntry(player.getUniqueId(), "rank", "vip", 0))).join();

        server.addPlayer(player);

        Optional<PlayerData> playerData = plugin.getDataManager().getPlayer(player.getUniqueId());
        assertTrue(playerData.isPresent());
        assertEquals("vip", playerData.get().getData("rank").map(Data::getValue).map(Object::toString).orElse(null));
    }

    @Test
    public void aValueInMemoryWinsOverTheDatabase() {
        UUID playerId = UUID.randomUUID();
        storage.getProvider().playerData().save(List.of(new PlayerDataEntry(playerId, "coins", "1", 0))).join();

        plugin.getDataManager().addData(playerId, new ZData("coins", "50", 0));
        PlayerData playerData = plugin.getDataManager().loadPlayer(playerId).join();

        assertEquals("50", playerData.getData("coins").map(Data::getValue).map(Object::toString).orElse(null));
    }

    @Test
    public void valuesWaitingToBeWrittenAreSeenByALoad() {
        UUID playerId = UUID.randomUUID();
        storage.upsertData(playerId, new ZData("coins", "7", 0));

        assertTrue(storage.hasPendingData(playerId));
        assertEquals(List.of(new PlayerDataEntry(playerId, "coins", "7", 0)), storage.loadPlayerData(playerId).join());

        storage.flush();
        assertTrue(!storage.hasPendingData(playerId));
        assertEquals(List.of(new PlayerDataEntry(playerId, "coins", "7", 0)), storage.getProvider().playerData().load(playerId).join());
    }

    @Test
    public void knownKeysIncludeKeysOfPlayersNotInMemory() {
        storage.getProvider().playerData().save(List.of(new PlayerDataEntry(UUID.randomUUID(), "unloaded_key", "1", 0))).join();
        plugin.getDataManager().loadPlayers();

        assertTrue(plugin.getDataManager().getKeys().contains("unloaded_key"));
    }
}
