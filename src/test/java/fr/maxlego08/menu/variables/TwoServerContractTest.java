package fr.maxlego08.menu.variables;

import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.storage.spi.StorageProvider;
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
 * What two servers sharing one storage must see of each other, run against each shared backend:
 * changes and clears travel through the broker, additions from both servers all count, players are
 * handed over, and saved inventories stay on their server.
 */
abstract class TwoServerContractTest {

    @TempDir
    File folder;

    StorageProvider providerA;
    StorageProvider providerB;
    ZVariableService serverA;
    ZVariableService serverB;

    abstract StorageProvider newProvider();

    /**
     * @return the configuration both servers use; called once per test, after {@link #prepare()}
     */
    abstract YamlConfiguration configuration();

    /**
     * Gets the backend ready for a test, such as emptying a shared database.
     */
    void prepare() throws Exception {
    }

    /**
     * Waits until both brokers receive events, for brokers that need to connect first.
     */
    void awaitBrokers() throws Exception {
    }

    private StorageProvider open(YamlConfiguration configuration, String serverId) {
        StorageProvider provider = this.newProvider();
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), serverId)).join();
        return provider;
    }

    static ZVariableService service(StorageProvider provider, String serverId, long handoffTimeout) {
        return new ZVariableService(null, provider.variables(), serverId, provider.broker().orElseThrow(), provider.sessions().orElseThrow(), handoffTimeout);
    }

    static void eventually(Supplier<Boolean> condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 8000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.get()) return;
            Thread.sleep(50);
        }
        assertTrue(condition.get(), "the condition never became true");
    }

    static String cached(ZVariableService service, VariableRef ref) {
        return service.getCached(ref).map(Variable::text).orElse(null);
    }

    @BeforeEach
    void start() throws Exception {
        this.prepare();
        YamlConfiguration configuration = this.configuration();
        this.providerA = this.open(configuration, "a");
        this.providerB = this.open(configuration, "b");
        this.serverA = service(this.providerA, "a", 2000);
        this.serverB = service(this.providerB, "b", 2000);
        this.serverA.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
        this.serverB.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
        this.awaitBrokers();
    }

    /**
     * Releases the backend, once both providers are closed.
     */
    void cleanup() throws Exception {
    }

    @AfterEach
    void stop() throws Exception {
        try {
            if (this.providerA != null) this.providerA.close().join();
            if (this.providerB != null) this.providerB.close().join();
        } finally {
            this.cleanup();
        }
    }

    @Test
    void aGlobalChangeReachesTheOtherServer() throws InterruptedException {
        VariableRef kills = VariableRef.global("kills");
        VariableRef event = VariableRef.global("event");

        this.serverA.increment(kills, new BigDecimal("2.5"), null).join();
        this.serverA.set(event, "started", 0);

        eventually(() -> "2.5".equals(cached(this.serverB, kills)) && "started".equals(cached(this.serverB, event)));
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
    void aJoiningPlayerIsReadAnywayWhenTheOtherServerNeverAnswers() {
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
