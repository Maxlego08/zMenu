package fr.maxlego08.menu.storage.redis;

import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Variables in Redis. Each owner has two hashes sharing a hash tag, so they live on the same
 * Cluster node and scripts can change both at once:
 * <ul>
 *     <li>{@code <prefix>:v:{<scope>:<owner>}}: name to value;</li>
 *     <li>{@code <prefix>:m:{<scope>:<owner>}}: name to {@code N|<expiresAt>} for a number,
 *     {@code T|<expiresAt>} for a text.</li>
 * </ul>
 * Indexes kept beside them: the owners of each scope ({@code <prefix>:owners:<scope>}), how many
 * owners use each name ({@code <prefix>:names:<scope>}) and a sorted set of the expiry times
 * ({@code <prefix>:expiry}). They live on other Cluster nodes, so they are updated after the
 * scripts, not inside them.
 */
final class RedisVariableStore implements VariableStore {

    private static final String READ = """
            return {redis.call('HGET', KEYS[1], ARGV[1]) or false, redis.call('HGET', KEYS[2], ARGV[1]) or false}
            """;

    /**
     * @return 1 when the value is new
     */
    private static final String SET = """
            local created = redis.call('HSET', KEYS[1], ARGV[1], ARGV[2])
            redis.call('HSET', KEYS[2], ARGV[1], ARGV[3])
            return created
            """;

    /**
     * @return 1 when a value was removed
     */
    private static final String DELETE = """
            local removed = redis.call('HDEL', KEYS[1], ARGV[1])
            redis.call('HDEL', KEYS[2], ARGV[1])
            return removed
            """;

    /**
     * Adds to a number with HINCRBYFLOAT, so concurrent additions all count. A missing value or a
     * text starts from the amount; an existing value keeps its expiry time.
     */
    private static final String INCREMENT = """
            local meta = redis.call('HGET', KEYS[2], ARGV[1])
            if meta and string.sub(meta, 1, 1) == 'N' then
                return {redis.call('HINCRBYFLOAT', KEYS[1], ARGV[1], ARGV[2]), meta}
            end
            local expiresAt = ARGV[3]
            if meta then expiresAt = string.sub(meta, 3) end
            local created = redis.call('HSET', KEYS[1], ARGV[1], '0')
            local value = redis.call('HINCRBYFLOAT', KEYS[1], ARGV[1], ARGV[2])
            redis.call('HSET', KEYS[2], ARGV[1], 'N|' .. expiresAt)
            return {value, 'N|' .. expiresAt, created}
            """;

    private final RedisConnection connection;

    RedisVariableStore(RedisConnection connection) {
        this.connection = connection;
    }

    private String owner(VariableScope scope, String owner) {
        return "{" + scope.id() + ":" + owner + "}";
    }

    private List<String> keys(VariableScope scope, String owner) {
        String tag = this.owner(scope, owner);
        return List.of(this.connection.key("v", tag), this.connection.key("m", tag));
    }

    private String ownersKey(VariableScope scope) {
        return this.connection.key("owners", scope.id());
    }

    private String namesKey(VariableScope scope) {
        return this.connection.key("names", scope.id());
    }

    private String expiryKey() {
        return this.connection.key("expiry");
    }

    private static String expiryMember(VariableRef ref) {
        return ref.scope().id() + "\n" + ref.owner() + "\n" + ref.name();
    }

    private static String meta(Variable variable) {
        return (variable.number() == null ? "T|" : "N|") + variable.expiresAt();
    }

    private static Variable toVariable(VariableRef ref, String value, String meta) {
        long expiresAt = meta == null || meta.length() < 3 ? 0 : Long.parseLong(meta.substring(2));
        if (meta != null && meta.startsWith("N") && value != null) {
            return Variable.ofNumber(ref, new BigDecimal(value), expiresAt);
        }
        return new Variable(ref, value == null ? "" : value, null, expiresAt);
    }

    private static boolean isOne(Object reply) {
        return reply instanceof Number number && number.longValue() == 1;
    }

    /**
     * Counts one more owner using a name, in the hash {@code <prefix>:names:<scope>}.
     */
    private void nameAdded(VariableRef ref) {
        this.connection.jedis().hincrBy(this.namesKey(ref.scope()), ref.name(), 1);
    }

    /**
     * Counts one owner less using a name, and forgets the name once nobody uses it.
     */
    private void nameRemoved(VariableScope scope, String name, long count) {
        if (count <= 0) return;
        if (this.connection.jedis().hincrBy(this.namesKey(scope), name, -count) <= 0) {
            this.connection.jedis().hdel(this.namesKey(scope), name);
        }
    }

    private void index(VariableRef ref, long expiresAt, boolean created) {
        this.connection.jedis().sadd(this.ownersKey(ref.scope()), ref.owner());
        if (created) this.nameAdded(ref);
        if (expiresAt > 0) this.connection.jedis().zadd(this.expiryKey(), expiresAt, expiryMember(ref));
        else this.connection.jedis().zrem(this.expiryKey(), expiryMember(ref));
    }

    private BigDecimal increment(VariableRef ref, BigDecimal delta, long expiresAtIfCreated) {
        Object result = this.connection.jedis().eval(INCREMENT, this.keys(ref.scope(), ref.owner()),
                List.of(ref.name(), delta.toPlainString(), String.valueOf(expiresAtIfCreated)));
        List<?> values = (List<?>) result;
        String meta = String.valueOf(values.get(1));
        this.index(ref, Long.parseLong(meta.substring(2)), values.size() > 2 && isOne(values.get(2)));
        return new BigDecimal(String.valueOf(values.get(0)));
    }

    private void set(Variable variable) {
        VariableRef ref = variable.ref();
        String value = variable.number() == null ? variable.text() : variable.number().toPlainString();
        Object created = this.connection.jedis().eval(SET, this.keys(ref.scope(), ref.owner()), List.of(ref.name(), value, meta(variable)));
        this.index(ref, variable.expiresAt(), isOne(created));
    }

    private void delete(VariableRef ref) {
        Object removed = this.connection.jedis().eval(DELETE, this.keys(ref.scope(), ref.owner()), List.of(ref.name()));
        this.connection.jedis().zrem(this.expiryKey(), expiryMember(ref));
        if (isOne(removed)) this.nameRemoved(ref.scope(), ref.name(), 1);
    }

    @Override
    public @NotNull CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.connection.threads().read(() -> {
            List<String> keys = this.keys(scope, owner);
            Map<String, String> values = this.connection.jedis().hgetAll(keys.get(0));
            Map<String, String> metas = this.connection.jedis().hgetAll(keys.get(1));
            List<Variable> variables = new ArrayList<>(values.size());
            values.forEach((name, value) -> variables.add(toVariable(new VariableRef(scope, owner, name), value, metas.get(name))));
            return variables;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<Variable>> load(@NotNull VariableRef ref) {
        return this.connection.threads().read(() -> {
            List<?> result = (List<?>) this.connection.jedis().eval(READ, this.keys(ref.scope(), ref.owner()), List.of(ref.name()));
            Object value = result.isEmpty() ? null : result.get(0);
            if (value == null) return Optional.empty();
            Object meta = result.size() > 1 ? result.get(1) : null;
            return Optional.of(toVariable(ref, String.valueOf(value), meta == null ? null : String.valueOf(meta)));
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations) {
        if (mutations.isEmpty()) return CompletableFuture.completedFuture(null);
        return this.connection.threads().write(() -> {
            for (VariableMutation mutation : mutations) {
                switch (mutation) {
                    case VariableMutation.Set set -> this.set(set.variable());
                    case VariableMutation.Increment increment -> this.increment(increment.ref(), increment.delta(), increment.expiresAtIfCreated());
                    case VariableMutation.Delete delete -> this.delete(delete.ref());
                }
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<BigDecimal> incrementAndGet(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated) {
        return this.connection.threads().writeAndGet(() -> this.increment(ref, delta, expiresAtIfCreated));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name) {
        return this.connection.threads().write(() -> {
            for (String owner : this.connection.jedis().smembers(this.ownersKey(scope))) {
                this.delete(new VariableRef(scope, owner, name));
            }
            this.connection.jedis().hdel(this.namesKey(scope), name);
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.connection.threads().write(() -> {
            List<String> keys = this.keys(scope, owner);
            for (String name : this.connection.jedis().hkeys(keys.get(0))) {
                this.nameRemoved(scope, name, 1);
            }
            this.connection.jedis().del(keys.get(0));
            this.connection.jedis().del(keys.get(1));
            this.connection.jedis().srem(this.ownersKey(scope), owner);
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
        return this.connection.threads().write(() -> {
            for (String owner : this.connection.jedis().smembers(this.ownersKey(scope))) {
                List<String> keys = this.keys(scope, owner);
                this.connection.jedis().del(keys.get(0));
                this.connection.jedis().del(keys.get(1));
            }
            this.connection.jedis().del(this.ownersKey(scope));
            this.connection.jedis().del(this.namesKey(scope));
        });
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> owners(@NotNull VariableScope scope) {
        return this.connection.threads().read(() -> new HashSet<>(this.connection.jedis().smembers(this.ownersKey(scope))));
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> names(@NotNull VariableScope scope) {
        return this.connection.threads().read(() -> new HashSet<>(this.connection.jedis().hkeys(this.namesKey(scope))));
    }

    /**
     * Deletes the values whose expiry time in {@code <prefix>:expiry} has passed. A value replaced
     * since with a later expiry time has a later score, so it is not touched.
     */
    @Override
    public @NotNull CompletableFuture<Void> deleteExpired(long now) {
        return this.connection.threads().write(() -> {
            for (String member : this.connection.jedis().zrangeByScore(this.expiryKey(), 1, now)) {
                String[] parts = member.split("\n", 3);
                VariableScope scope = parts.length == 3 ? VariableScope.parse(parts[0]) : null;
                if (scope != null) this.delete(new VariableRef(scope, parts[1], parts[2]));
            }
            this.connection.jedis().zremrangeByScore(this.expiryKey(), 1, now);
        });
    }
}
