package fr.maxlego08.menu.variables;

import de.bwaldvogel.mongo.MongoServer;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.MongoTestServers;
import fr.maxlego08.menu.storage.mongo.MongoStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Two servers sharing one MongoDB: changes travel through the events collection and players are
 * handed over.
 */
class MongoMultiServerTest {

    @TempDir
    File folder;

    private MongoServer mongo;
    private MongoStorageProvider providerA;
    private MongoStorageProvider providerB;
    private ZVariableService serverA;
    private ZVariableService serverB;

    private MongoStorageProvider open(YamlConfiguration configuration, String serverId) {
        MongoStorageProvider provider = new MongoStorageProvider();
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), serverId)).join();
        return provider;
    }

    private static ZVariableService service(MongoStorageProvider provider, String serverId) {
        return new ZVariableService(null, provider.variables(), serverId, provider.broker().orElseThrow(), provider.sessions().orElseThrow(), 2000);
    }

    private static void eventually(Supplier<Boolean> condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.get()) return;
            Thread.sleep(50);
        }
        assertTrue(condition.get(), "the condition never became true");
    }

    @BeforeEach
    void start() {
        this.mongo = MongoTestServers.newMongoServer();
        InetSocketAddress address = this.mongo.bind();
        YamlConfiguration configuration = MongoTestServers.mongoConfiguration(address);

        this.providerA = this.open(configuration, "a");
        this.providerB = this.open(configuration, "b");
        this.serverA = service(this.providerA, "a");
        this.serverB = service(this.providerB, "b");
        this.serverA.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
        this.serverB.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
    }

    @AfterEach
    void stop() {
        this.providerA.close().join();
        this.providerB.close().join();
        this.mongo.shutdownNow();
    }

    private static String cached(ZVariableService service, VariableRef ref) {
        return service.getCached(ref).map(Variable::text).orElse(null);
    }

    @Test
    void aGlobalChangeReachesTheOtherServer() throws InterruptedException {
        VariableRef kills = VariableRef.global("kills");
        this.serverA.increment(kills, new BigDecimal("2.5"), null).join();

        eventually(() -> "2.5".equals(cached(this.serverB, kills)));
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
    void aPlayerMovingServersFindsTheChangesMadeOnTheFirstOne() throws Exception {
        UUID playerId = UUID.randomUUID();
        VariableRef coins = VariableRef.player(playerId, "coins");

        this.serverA.prepareLogin(playerId);
        this.serverA.load(VariableScope.PLAYER, playerId.toString()).join();
        this.serverA.increment(coins, BigDecimal.valueOf(5), null);

        CompletableFuture<Void> login = CompletableFuture.runAsync(() -> this.serverB.prepareLogin(playerId));
        Thread.sleep(400);
        assertFalse(login.isDone(), "server b must wait for server a to release the player");

        this.serverA.releasePlayer(playerId).join();
        login.get(5, TimeUnit.SECONDS);

        this.serverB.load(VariableScope.PLAYER, playerId.toString()).join();
        assertEquals("5", cached(this.serverB, coins));
    }
}
