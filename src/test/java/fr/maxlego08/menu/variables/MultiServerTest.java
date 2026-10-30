package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.sql.SqlStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Two servers sharing one database: changes, clears and players move from one to the other.
 * The servers share a SQLite file with {@code messaging-type: SQL}, which turns on the event table
 * and the sessions that MySQL and MariaDB use.
 */
class MultiServerTest {

    @TempDir
    File folder;

    private SqlStorageProvider providerA;
    private SqlStorageProvider providerB;
    private ZVariableService serverA;
    private ZVariableService serverB;

    private SqlStorageProvider open(String serverId) {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "SQLITE");
        configuration.set("messaging-type", "SQL");
        configuration.set("messaging-poll-interval", 0.25);
        SqlStorageProvider provider = new SqlStorageProvider();
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), serverId)).join();
        return provider;
    }

    private static ZVariableService service(SqlStorageProvider provider, String serverId, long handoffTimeout) {
        return new ZVariableService(null, provider.variables(), serverId, provider.broker().orElseThrow(), provider.sessions().orElseThrow(), handoffTimeout);
    }

    @BeforeEach
    void start() {
        this.providerA = this.open("a");
        this.providerB = this.open("b");
        this.serverA = service(this.providerA, "a", 2000);
        this.serverB = service(this.providerB, "b", 2000);
        this.serverA.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
        this.serverB.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
    }

    @AfterEach
    void stop() {
        this.providerA.close().join();
        this.providerB.close().join();
    }

    private static void eventually(Supplier<Boolean> condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.get()) return;
            Thread.sleep(50);
        }
        assertTrue(condition.get(), "the condition never became true");
    }

    private static String cached(ZVariableService service, VariableRef ref) {
        return service.getCached(ref).map(Variable::text).orElse(null);
    }

    @Test
    void aGlobalChangeReachesTheOtherServer() throws InterruptedException {
        VariableRef kills = VariableRef.global("kills");
        VariableRef event = VariableRef.global("event");

        this.serverA.increment(kills, BigDecimal.valueOf(3), null).join();
        this.serverA.set(event, "started", 0);

        eventually(() -> "3".equals(cached(this.serverB, kills)) && "started".equals(cached(this.serverB, event)));
    }

    @Test
    void globalIncrementsFromBothServersAddUp() throws InterruptedException {
        VariableRef votes = VariableRef.global("votes");
        List<CompletableFuture<BigDecimal>> futures = new ArrayList<>();
        for (int index = 0; index < 25; index++) {
            futures.add(this.serverA.increment(votes, BigDecimal.ONE, null));
            futures.add(this.serverB.increment(votes, BigDecimal.ONE, null));
        }
        futures.forEach(CompletableFuture::join);

        assertEquals("50", this.providerA.variables().load(votes).join().map(Variable::text).orElse(null));
        eventually(() -> "50".equals(cached(this.serverA, votes)) && "50".equals(cached(this.serverB, votes)));
    }

    @Test
    void aClearedNameDisappearsOnTheOtherServer() throws InterruptedException {
        VariableRef ref = VariableRef.global("season");
        this.serverA.set(ref, "1", 0);
        eventually(() -> "1".equals(cached(this.serverB, ref)));

        this.serverA.deleteName(VariableScope.GLOBAL, "season").join();

        eventually(() -> cached(this.serverB, ref) == null);
    }

    @Test
    void aPlayerMovingServersFindsTheChangesMadeOnTheFirstOne() throws Exception {
        UUID playerId = UUID.randomUUID();
        VariableRef coins = VariableRef.player(playerId, "coins");

        this.serverA.prepareLogin(playerId);
        this.serverA.load(VariableScope.PLAYER, playerId.toString()).join();
        this.serverA.increment(coins, BigDecimal.valueOf(5), null);
        assertTrue(this.serverA.hasPending(VariableScope.PLAYER, playerId.toString()));

        CompletableFuture<Void> login = CompletableFuture.runAsync(() -> this.serverB.prepareLogin(playerId));
        Thread.sleep(500);
        assertFalse(login.isDone(), "server b must wait for server a to release the player");

        this.serverA.releasePlayer(playerId).join();
        login.get(5, TimeUnit.SECONDS);

        this.serverB.load(VariableScope.PLAYER, playerId.toString()).join();
        assertEquals("5", cached(this.serverB, coins));
    }

    @Test
    void aJoiningPlayerIsReadAnywayWhenTheOtherServerNeverAnswers() throws Exception {
        ZVariableService impatient = service(this.providerB, "b", 600);
        UUID playerId = UUID.randomUUID();
        this.serverA.prepareLogin(playerId);

        long start = System.currentTimeMillis();
        impatient.prepareLogin(playerId);
        long waited = System.currentTimeMillis() - start;

        assertTrue(waited >= 500 && waited < 4000, "waited " + waited + " ms");
        assertEquals(Optional.of("b"), this.providerA.sessions().orElseThrow().load(playerId).join().map(session -> session.serverId()));
    }

    @Test
    void savedInventoriesStayOnTheirServer() {
        UUID playerId = UUID.randomUUID();
        this.providerA.savedInventories().save(playerId, "0:abc").join();

        assertEquals(Optional.of("0:abc"), this.providerA.savedInventories().load(playerId).join());
        assertEquals(Optional.empty(), this.providerB.savedInventories().load(playerId).join());
    }
}
