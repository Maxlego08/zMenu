package fr.maxlego08.menu.variables;

import com.github.fppt.jedismock.RedisServer;
import fr.maxlego08.menu.api.storage.spi.StorageContext;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.storage.redis.RedisMessageBroker;
import fr.maxlego08.menu.storage.redis.RedisStorageProvider;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
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
 * Two servers sharing one Redis: changes travel on pub/sub and players are handed over.
 */
class RedisMultiServerTest {

    @TempDir
    File folder;

    private RedisServer redis;
    private RedisStorageProvider providerA;
    private RedisStorageProvider providerB;
    private ZVariableService serverA;
    private ZVariableService serverB;

    private RedisStorageProvider open(YamlConfiguration configuration, String serverId) {
        RedisStorageProvider provider = new RedisStorageProvider();
        provider.open(new StorageContext(null, configuration, this.folder, Logger.getLogger("zMenu-test"), serverId)).join();
        return provider;
    }

    private static ZVariableService service(RedisStorageProvider provider, String serverId) {
        return new ZVariableService(null, provider.variables(), serverId, provider.broker().orElseThrow(), provider.sessions().orElseThrow(), 2000);
    }

    private static void eventually(Supplier<Boolean> condition) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            if (condition.get()) return;
            Thread.sleep(25);
        }
        assertTrue(condition.get(), "the condition never became true");
    }

    @BeforeEach
    void start() throws Exception {
        this.redis = RedisServer.newRedisServer();
        this.redis.start();

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("storage-type", "REDIS");
        configuration.set("redis.uri", "redis://" + this.redis.getHost() + ":" + this.redis.getBindPort() + "/0");

        this.providerA = this.open(configuration, "a");
        this.providerB = this.open(configuration, "b");
        this.serverA = service(this.providerA, "a");
        this.serverB = service(this.providerB, "b");
        this.serverA.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();
        this.serverB.load(VariableScope.GLOBAL, VariableRef.GLOBAL_OWNER).join();

        eventually(() -> ((RedisMessageBroker) this.providerA.broker().orElseThrow()).isSubscribed()
                && ((RedisMessageBroker) this.providerB.broker().orElseThrow()).isSubscribed());
    }

    @AfterEach
    void stop() throws Exception {
        this.providerA.close().join();
        this.providerB.close().join();
        this.redis.stop();
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
