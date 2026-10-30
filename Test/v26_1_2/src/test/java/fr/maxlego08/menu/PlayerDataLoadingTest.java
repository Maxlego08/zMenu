package fr.maxlego08.menu;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.PlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.players.ZData;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.storage.ZStorageManager;
import fr.maxlego08.menu.variables.ZVariableService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Player data and variables are read when needed and written through the storage provider.
 */
public class PlayerDataLoadingTest {

    private static ServerMock server;
    private static ZMenuPlugin plugin;
    private static ZStorageManager storage;
    private static ZVariableService variables;

    @BeforeAll
    public static void init() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        storage = (ZStorageManager) plugin.getStorageManager();
        variables = plugin.getVariableService();
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    private static void store(VariableRef ref, String value) {
        storage.getProvider().variables().apply(List.of(new VariableMutation.Set(Variable.of(ref, value, 0)))).join();
    }

    private static Map<String, String> stored(VariableScope scope, String owner) {
        Map<String, String> values = new TreeMap<>();
        storage.getProvider().variables().loadOwner(scope, owner).join().forEach(variable -> values.put(variable.ref().name(), variable.text()));
        return values;
    }

    private static String value(PlayerData playerData, String key) {
        return playerData.getData(key).map(Data::getValue).map(Object::toString).orElse(null);
    }

    @Test
    public void anOfflinePlayerIsOnlyInMemoryOnceLoaded() {
        UUID playerId = UUID.randomUUID();
        store(VariableRef.player(playerId, "coins"), "12");

        assertTrue(plugin.getDataManager().getPlayer(playerId).isEmpty());

        PlayerData playerData = plugin.getDataManager().loadPlayer(playerId).join();
        assertEquals("12", value(playerData, "coins"));
        assertTrue(plugin.getDataManager().getPlayer(playerId).isPresent());
    }

    @Test
    public void aJoiningPlayerIsLoaded() {
        PlayerMock player = new PlayerMock(server, "Loaded", UUID.randomUUID());
        store(VariableRef.player(player.getUniqueId(), "rank"), "vip");

        server.addPlayer(player);

        Optional<PlayerData> playerData = plugin.getDataManager().getPlayer(player.getUniqueId());
        assertTrue(playerData.isPresent());
        assertEquals("vip", value(playerData.get(), "rank"));
    }

    @Test
    public void aValueSetInMemoryWinsOverTheDatabase() {
        UUID playerId = UUID.randomUUID();
        store(VariableRef.player(playerId, "coins"), "1");

        plugin.getDataManager().addData(playerId, new ZData("coins", "50", 0));
        PlayerData playerData = plugin.getDataManager().loadPlayer(playerId).join();

        assertEquals("50", value(playerData, "coins"));
    }

    @Test
    public void anIncrementBeforeTheReadAddsToTheStoredValue() {
        UUID playerId = UUID.randomUUID();
        VariableRef ref = VariableRef.player(playerId, "coins");
        store(ref, "10");

        variables.increment(ref, BigDecimal.valueOf(5), null);
        PlayerData playerData = plugin.getDataManager().loadPlayer(playerId).join();

        assertEquals("15", value(playerData, "coins"));
        storage.flush();
        assertEquals("15", stored(VariableScope.PLAYER, playerId.toString()).get("coins"));
    }

    @Test
    public void playerIncrementsWaitForTheBatchAndAddUp() {
        UUID playerId = UUID.randomUUID();
        VariableRef ref = VariableRef.player(playerId, "clicks");
        plugin.getDataManager().loadPlayer(playerId).join();

        for (int index = 0; index < 10; index++) variables.increment(ref, BigDecimal.ONE, null);

        assertTrue(variables.hasPending(VariableScope.PLAYER, playerId.toString()));
        assertEquals("10", variables.getCached(ref).map(Variable::text).orElse(null));

        store(ref, "100");
        storage.flush();
        assertFalse(variables.hasPending(VariableScope.PLAYER, playerId.toString()));
        assertEquals("110", stored(VariableScope.PLAYER, playerId.toString()).get("clicks"));
    }

    @Test
    public void globalAndServerVariablesAreWrittenAtOnce() {
        VariableRef global = VariableRef.global("event_kills");
        VariableRef local = VariableRef.server(variables.serverId(), "event_kills");

        assertEquals(0, BigDecimal.valueOf(3).compareTo(variables.increment(global, BigDecimal.valueOf(3), null).join()));
        variables.set(local, "open", 0);

        assertEquals("3", stored(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).get("event_kills"));
        assertEquals("open", stored(VariableScope.SERVER, variables.serverId()).get("event_kills"));
    }

    @Test
    public void theDataActionWritesItsScope() {
        PlayerMock player = server.addPlayer();
        ZActionPlayerData add = new ZActionPlayerData(plugin, "votes", ActionPlayerDataType.ADD, "2", "0", false, VariableScope.GLOBAL);
        ZActionPlayerData set = new ZActionPlayerData(plugin, "motd", ActionPlayerDataType.SET, "hello", "0", false, VariableScope.SERVER);

        add.execute(player, plugin.getDataManager(), new Placeholders());
        add.execute(player, plugin.getDataManager(), new Placeholders());
        set.execute(player, plugin.getDataManager(), new Placeholders());

        assertEquals("4", variables.getCached(VariableRef.global("votes")).map(Variable::text).orElse(null));
        assertEquals("hello", variables.getCached(VariableRef.server(variables.serverId(), "motd")).map(Variable::text).orElse(null));
        assertTrue(plugin.getDataManager().getData(player.getUniqueId(), "votes").isEmpty());
    }

    @Test
    public void namesIncludeNamesOfPlayersNotInMemory() {
        store(VariableRef.player(UUID.randomUUID(), "unloaded_key"), "1");
        variables.enable();

        assertTrue(plugin.getDataManager().getKeys().contains("unloaded_key"));
    }
}
