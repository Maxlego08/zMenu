package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What every {@link StorageProvider} must do, run against each built-in provider.
 */
abstract class StorageProviderContractTest {

    private static final UUID PLAYER = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID OTHER = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @TempDir
    File dataFolder;

    StorageProvider provider;

    abstract StorageProvider createProvider();

    YamlConfiguration configuration() {
        return new YamlConfiguration();
    }

    @BeforeEach
    void open() {
        this.provider = this.createProvider();
        this.provider.open(new StorageContext(null, this.configuration(), this.dataFolder, Logger.getLogger("zMenu-test"), "test")).join();
    }

    @AfterEach
    void close() {
        this.provider.close().join();
    }

    private static VariableRef player(UUID playerId, String name) {
        return VariableRef.player(playerId, name);
    }

    private static VariableMutation set(VariableRef ref, String value, long expiresAt) {
        return new VariableMutation.Set(Variable.of(ref, value, expiresAt));
    }

    private Map<String, Variable> owner(VariableScope scope, String owner) {
        Map<String, Variable> variables = new TreeMap<>();
        this.provider.variables().loadOwner(scope, owner).join().forEach(variable -> variables.put(variable.ref().name(), variable));
        return variables;
    }

    private Map<String, Variable> player(UUID playerId) {
        return this.owner(VariableScope.PLAYER, playerId.toString());
    }

    @Test
    void variablesAreSetReplacedAndLoadedPerOwner() {
        long expiresAt = 4_102_444_800_000L;
        this.provider.variables().apply(List.of(
                set(player(PLAYER, "coins"), "10", 0),
                set(player(PLAYER, "rank"), "vip", expiresAt),
                set(player(OTHER, "coins"), "3", 0),
                set(VariableRef.global("event"), "on", 0),
                set(VariableRef.server("lobby", "event"), "off", 0))).join();
        this.provider.variables().apply(List.of(set(player(PLAYER, "coins"), "25.50", 0))).join();

        Map<String, Variable> player = this.player(PLAYER);
        assertEquals(Set.of("coins", "rank"), player.keySet());
        assertEquals("25.5", player.get("coins").text());
        assertEquals(0, new BigDecimal("25.5").compareTo(player.get("coins").number()));
        assertEquals("vip", player.get("rank").text());
        assertNull(player.get("rank").number());
        assertEquals(expiresAt, player.get("rank").expiresAt());

        assertEquals("on", this.owner(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).get("event").text());
        assertEquals("off", this.owner(VariableScope.SERVER, "lobby").get("event").text());
        assertTrue(this.owner(VariableScope.SERVER, "other").isEmpty());
    }

    @Test
    void incrementsAddToNumbersAndReplaceText() {
        VariableRef coins = VariableRef.global("coins");
        VariableRef label = VariableRef.global("label");

        assertEquals(0, BigDecimal.valueOf(5).compareTo(this.provider.variables().incrementAndGet(coins, BigDecimal.valueOf(5), 0).join()));
        assertEquals(0, new BigDecimal("2.5").compareTo(this.provider.variables().incrementAndGet(coins, new BigDecimal("-2.5"), 0).join()));

        this.provider.variables().apply(List.of(set(label, "hello", 0))).join();
        assertEquals(0, BigDecimal.valueOf(3).compareTo(this.provider.variables().incrementAndGet(label, BigDecimal.valueOf(3), 0).join()));

        Map<String, Variable> global = this.owner(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER);
        assertEquals("2.5", global.get("coins").text());
        assertEquals("3", global.get("label").text());
    }

    @Test
    void anIncrementKeepsTheExpiryOfAnExistingValue() {
        VariableRef ref = player(PLAYER, "boost");
        this.provider.variables().apply(List.of(new VariableMutation.Increment(ref, BigDecimal.ONE, 4_102_444_800_000L))).join();
        this.provider.variables().apply(List.of(new VariableMutation.Increment(ref, BigDecimal.ONE, 1_000L))).join();

        Variable boost = this.player(PLAYER).get("boost");
        assertEquals("2", boost.text());
        assertEquals(4_102_444_800_000L, boost.expiresAt());
    }

    @Test
    void parallelIncrementsAllCount() {
        VariableRef ref = VariableRef.global("counter");
        List<CompletableFuture<BigDecimal>> futures = new ArrayList<>();
        for (int index = 0; index < 200; index++) {
            futures.add(CompletableFuture.supplyAsync(() -> this.provider.variables().incrementAndGet(ref, BigDecimal.ONE, 0).join()));
        }
        futures.forEach(CompletableFuture::join);

        assertEquals("200", this.owner(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).get("counter").text());
    }

    @Test
    void variablesAreDeletedByNameOwnerAndScope() {
        this.provider.variables().apply(List.of(
                set(player(PLAYER, "coins"), "10", 0),
                set(player(PLAYER, "rank"), "vip", 0),
                set(player(OTHER, "coins"), "3", 0),
                set(player(OTHER, "rank"), "mvp", 0),
                set(VariableRef.global("coins"), "1", 0))).join();

        this.provider.variables().apply(List.of(new VariableMutation.Delete(player(PLAYER, "rank")))).join();
        assertEquals(Set.of("coins"), this.player(PLAYER).keySet());

        this.provider.variables().deleteName(VariableScope.PLAYER, "coins").join();
        assertTrue(this.player(PLAYER).isEmpty());
        assertEquals(Set.of("rank"), this.player(OTHER).keySet());
        assertEquals(Set.of("coins"), this.owner(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).keySet());

        this.provider.variables().deleteOwner(VariableScope.PLAYER, OTHER.toString()).join();
        assertTrue(this.player(OTHER).isEmpty());

        this.provider.variables().apply(List.of(set(player(PLAYER, "coins"), "1", 0))).join();
        this.provider.variables().deleteScope(VariableScope.PLAYER).join();
        assertTrue(this.player(PLAYER).isEmpty());
        assertEquals(Set.of("coins"), this.owner(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).keySet());
    }

    @Test
    void namesAreListedOncePerScope() {
        this.provider.variables().apply(List.of(
                set(player(PLAYER, "coins"), "10", 0),
                set(player(PLAYER, "rank"), "vip", 0),
                set(player(OTHER, "coins"), "3", 0),
                set(VariableRef.global("event"), "on", 0))).join();

        assertEquals(Set.of("coins", "rank"), this.provider.variables().names(VariableScope.PLAYER).join());
        assertEquals(Set.of("event"), this.provider.variables().names(VariableScope.GLOBAL).join());
    }

    @Test
    void onlyExpiredValuesArePurged() {
        this.provider.variables().apply(List.of(
                set(player(PLAYER, "expired"), "1", 1_000L),
                set(player(PLAYER, "later"), "2", 4_102_444_800_000L),
                set(player(PLAYER, "never"), "3", 0))).join();

        this.provider.variables().deleteExpired(1_700_000_000_000L).join();

        assertEquals(Set.of("later", "never"), this.provider.variables().names(VariableScope.PLAYER).join());
    }

    @Test
    void savedInventoryIsLoadedPerPlayer() {
        this.provider.savedInventories().save(PLAYER, "0:abc").join();

        assertEquals(Optional.of("0:abc"), this.provider.savedInventories().load(PLAYER).join());
        assertEquals(Optional.empty(), this.provider.savedInventories().load(OTHER).join());
    }

    @Test
    void writesApplyInTheOrderTheyAreMade() {
        this.provider.variables().apply(List.of(set(player(PLAYER, "coins"), "10", 0)));
        this.provider.variables().apply(List.of(new VariableMutation.Delete(player(PLAYER, "coins"))));
        this.provider.savedInventories().save(PLAYER, "0:abc");
        this.provider.savedInventories().delete(PLAYER).join();

        assertTrue(this.player(PLAYER).isEmpty());
        assertTrue(this.provider.savedInventories().loadAll().join().isEmpty());
    }

    @Test
    void savedInventoryIsReplacedNotDuplicated() {
        this.provider.savedInventories().save(PLAYER, "0:first").join();
        this.provider.savedInventories().save(PLAYER, "0:second").join();
        this.provider.savedInventories().save(OTHER, "1:other").join();

        assertEquals(Map.of(PLAYER, "0:second", OTHER, "1:other"), this.provider.savedInventories().loadAll().join());

        this.provider.savedInventories().delete(PLAYER).join();
        assertEquals(Map.of(OTHER, "1:other"), this.provider.savedInventories().loadAll().join());
    }

    @Test
    void visitedWorldsAreReadBack() {
        this.provider.worldVisits().markVisited(PLAYER, "world").join();
        this.provider.worldVisits().markVisited(PLAYER, "world_nether").join();
        this.provider.worldVisits().markVisited(PLAYER, "world").join();

        assertEquals(Set.of("world", "world_nether"), this.provider.worldVisits().load(PLAYER).join());
        assertTrue(this.provider.worldVisits().load(OTHER).join().isEmpty());
    }

    @Test
    void playedAnimationsAreReadBackAndForgotten() {
        this.provider.animations().markPlayed(PLAYER, "shop:open", 1000L).join();
        this.provider.animations().markPlayed(PLAYER, "shop:open", 2000L).join();
        this.provider.animations().markPlayed(PLAYER, "menu:open", 3000L).join();

        assertEquals(Map.of("shop:open", 2000L, "menu:open", 3000L), this.provider.animations().load(PLAYER).join());

        this.provider.animations().clear(PLAYER, "shop:open").join();
        assertEquals(Map.of("menu:open", 3000L), this.provider.animations().load(PLAYER).join());

        this.provider.animations().clear(PLAYER, null).join();
        assertTrue(this.provider.animations().load(PLAYER).join().isEmpty());
    }

    @Test
    void inventoryLogAcceptsRecords() {
        this.provider.inventoryLogs().ifPresent(store -> store.append(List.of(
                new InventoryOpenRecord(PLAYER, "zMenu", "shop", 1, List.of("main", "warps")),
                new InventoryOpenRecord(OTHER, "zMenu", "main", 0, List.of()))).join());
    }
}
