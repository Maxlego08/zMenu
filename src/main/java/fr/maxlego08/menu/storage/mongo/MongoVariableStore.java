package fr.maxlego08.menu.storage.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.Decimal128;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Variables in the {@code variables} collection, one document per variable:
 * {@code {_id, scope, owner, name, text | number, expiresAt, version}}. A number is kept in
 * {@code number} as a {@link Decimal128} and a text in {@code text}; the other field is absent, so
 * {@code $inc} can add to a missing number.
 */
final class MongoVariableStore implements VariableStore {

    private final MongoConnection connection;

    MongoVariableStore(MongoConnection connection) {
        this.connection = connection;
    }

    private MongoCollection<Document> collection() {
        return this.connection.collection("variables");
    }

    static String id(VariableRef ref) {
        return ref.scope().id() + "\u0000" + ref.owner() + "\u0000" + ref.name();
    }

    private static BigDecimal number(Object value) {
        if (value instanceof Decimal128 decimal) return decimal.bigDecimalValue();
        if (value instanceof Number number) return new BigDecimal(number.toString());
        return null;
    }

    private static Variable toVariable(Document document) {
        VariableScope scope = VariableScope.parse(document.getString("scope"));
        VariableRef ref = new VariableRef(scope == null ? VariableScope.PLAYER : scope, document.getString("owner"), document.getString("name"));
        Object expires = document.get("expiresAt");
        long expiresAt = expires instanceof Number value ? value.longValue() : 0;
        BigDecimal number = number(document.get("number"));
        if (number != null) return Variable.ofNumber(ref, number, expiresAt);
        String text = document.getString("text");
        return new Variable(ref, text == null ? "" : text, null, expiresAt);
    }

    private void set(Variable variable) {
        VariableRef ref = variable.ref();
        Bson value = variable.number() == null
                ? Updates.combine(Updates.set("text", variable.text()), Updates.unset("number"))
                : Updates.combine(Updates.set("number", new Decimal128(variable.number())), Updates.unset("text"));
        this.collection().updateOne(Filters.eq("_id", id(ref)), Updates.combine(
                Updates.set("scope", ref.scope().id()),
                Updates.set("owner", ref.owner()),
                Updates.set("name", ref.name()),
                Updates.set("expiresAt", variable.expiresAt()),
                value,
                Updates.inc("version", 1L)), new UpdateOptions().upsert(true));
    }

    /**
     * Adds to the number with {@code $inc}, which MongoDB applies atomically. A text has no
     * {@code number} field, so it starts from 0 and the text is removed; an existing document
     * keeps its expiry time.
     */
    private BigDecimal increment(VariableRef ref, BigDecimal delta, long expiresAtIfCreated) {
        Document document = this.collection().findOneAndUpdate(Filters.eq("_id", id(ref)), Updates.combine(
                Updates.inc("number", new Decimal128(delta)),
                Updates.unset("text"),
                Updates.inc("version", 1L),
                Updates.setOnInsert("scope", ref.scope().id()),
                Updates.setOnInsert("owner", ref.owner()),
                Updates.setOnInsert("name", ref.name()),
                Updates.setOnInsert("expiresAt", expiresAtIfCreated)), new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        BigDecimal value = document == null ? null : number(document.get("number"));
        return value == null ? delta : value;
    }

    private Bson owner(VariableScope scope, String owner) {
        return Filters.and(Filters.eq("scope", scope.id()), Filters.eq("owner", owner));
    }

    @Override
    public @NotNull CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.connection.threads().read(() -> {
            List<Variable> variables = new ArrayList<>();
            for (Document document : this.collection().find(this.owner(scope, owner))) {
                variables.add(toVariable(document));
            }
            return variables;
        });
    }

    @Override
    public @NotNull CompletableFuture<Optional<Variable>> load(@NotNull VariableRef ref) {
        return this.connection.threads().read(() -> Optional.ofNullable(this.collection().find(Filters.eq("_id", id(ref))).first()).map(MongoVariableStore::toVariable));
    }

    @Override
    public @NotNull CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations) {
        if (mutations.isEmpty()) return CompletableFuture.completedFuture(null);
        return this.connection.threads().write(() -> {
            for (VariableMutation mutation : mutations) {
                switch (mutation) {
                    case VariableMutation.Set set -> this.set(set.variable());
                    case VariableMutation.Increment increment -> this.increment(increment.ref(), increment.delta(), increment.expiresAtIfCreated());
                    case VariableMutation.Delete delete -> this.collection().deleteOne(Filters.eq("_id", id(delete.ref())));
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
        return this.connection.threads().write(() -> this.collection().deleteMany(Filters.and(Filters.eq("scope", scope.id()), Filters.eq("name", name))));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.connection.threads().write(() -> this.collection().deleteMany(this.owner(scope, owner)));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
        return this.connection.threads().write(() -> this.collection().deleteMany(Filters.eq("scope", scope.id())));
    }

    private Set<String> distinct(String field, VariableScope scope) {
        Set<String> values = new HashSet<>();
        this.collection().distinct(field, Filters.eq("scope", scope.id()), String.class).into(values);
        return values;
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> owners(@NotNull VariableScope scope) {
        return this.connection.threads().read(() -> this.distinct("owner", scope));
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> names(@NotNull VariableScope scope) {
        return this.connection.threads().read(() -> this.distinct("name", scope));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteExpired(long now) {
        return this.connection.threads().write(() -> this.collection().deleteMany(Filters.and(Filters.gt("expiresAt", 0L), Filters.lt("expiresAt", now))));
    }
}
