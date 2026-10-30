package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.InventoryOpenRecord;
import fr.maxlego08.menu.api.storage.spi.PlayerDataEntry;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        this.provider.open(new StorageContext(null, this.configuration(), this.dataFolder, Logger.getLogger("zMenu-test"))).join();
    }

    @AfterEach
    void close() {
        this.provider.close().join();
    }

    private List<PlayerDataEntry> sorted(List<PlayerDataEntry> entries) {
        return entries.stream().sorted(Comparator.comparing(PlayerDataEntry::key)).toList();
    }

    @Test
    void playerDataIsSavedReplacedAndLoaded() {
        long expiresAt = 4_102_444_800_000L;
        this.provider.playerData().save(List.of(
                new PlayerDataEntry(PLAYER, "coins", "10", 0),
                new PlayerDataEntry(PLAYER, "rank", "vip", expiresAt),
                new PlayerDataEntry(OTHER, "coins", "3", 0))).join();
        this.provider.playerData().save(List.of(new PlayerDataEntry(PLAYER, "coins", "25", 0))).join();

        List<PlayerDataEntry> player = this.sorted(this.provider.playerData().load(PLAYER).join());
        assertEquals(List.of(new PlayerDataEntry(PLAYER, "coins", "25", 0), new PlayerDataEntry(PLAYER, "rank", "vip", expiresAt)), player);
        assertEquals(3, this.provider.playerData().loadAll().join().size());
    }

    @Test
    void playerDataIsDeletedByKeyPlayerAndAll() {
        this.provider.playerData().save(List.of(
                new PlayerDataEntry(PLAYER, "coins", "10", 0),
                new PlayerDataEntry(PLAYER, "rank", "vip", 0),
                new PlayerDataEntry(OTHER, "coins", "3", 0),
                new PlayerDataEntry(OTHER, "rank", "mvp", 0))).join();

        this.provider.playerData().delete(PLAYER, "rank").join();
        assertEquals(List.of(new PlayerDataEntry(PLAYER, "coins", "10", 0)), this.provider.playerData().load(PLAYER).join());

        this.provider.playerData().deleteKey("coins").join();
        assertEquals(List.of(new PlayerDataEntry(OTHER, "rank", "mvp", 0)), this.provider.playerData().loadAll().join());

        this.provider.playerData().deletePlayer(OTHER).join();
        assertTrue(this.provider.playerData().loadAll().join().isEmpty());

        this.provider.playerData().save(List.of(new PlayerDataEntry(PLAYER, "coins", "1", 0))).join();
        this.provider.playerData().deleteAll().join();
        assertTrue(this.provider.playerData().loadAll().join().isEmpty());
    }

    @Test
    void keysAreListedOnce() {
        this.provider.playerData().save(List.of(
                new PlayerDataEntry(PLAYER, "coins", "10", 0),
                new PlayerDataEntry(PLAYER, "rank", "vip", 0),
                new PlayerDataEntry(OTHER, "coins", "3", 0))).join();

        assertEquals(Set.of("coins", "rank"), this.provider.playerData().keys().join());
    }

    @Test
    void onlyExpiredValuesArePurged() {
        this.provider.playerData().save(List.of(
                new PlayerDataEntry(PLAYER, "expired", "1", 1_000L),
                new PlayerDataEntry(PLAYER, "later", "2", 4_102_444_800_000L),
                new PlayerDataEntry(PLAYER, "never", "3", 0))).join();

        this.provider.playerData().deleteExpired(1_700_000_000_000L).join();

        assertEquals(Set.of("later", "never"), this.provider.playerData().keys().join());
    }

    @Test
    void savedInventoryIsLoadedPerPlayer() {
        this.provider.savedInventories().save(PLAYER, "0:abc").join();

        assertEquals(Optional.of("0:abc"), this.provider.savedInventories().load(PLAYER).join());
        assertEquals(Optional.empty(), this.provider.savedInventories().load(OTHER).join());
    }

    @Test
    void writesApplyInTheOrderTheyAreMade() {
        this.provider.playerData().save(List.of(new PlayerDataEntry(PLAYER, "coins", "10", 0)));
        this.provider.playerData().delete(PLAYER, "coins");
        this.provider.savedInventories().save(PLAYER, "0:abc");
        this.provider.savedInventories().delete(PLAYER).join();

        assertTrue(this.provider.playerData().load(PLAYER).join().isEmpty());
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
