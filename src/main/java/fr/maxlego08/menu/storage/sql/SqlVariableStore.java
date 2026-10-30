package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.menu.api.storage.Tables;
import fr.maxlego08.menu.api.storage.spi.VariableMutation;
import fr.maxlego08.menu.api.storage.spi.VariableStore;
import fr.maxlego08.menu.api.variable.Variable;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Schema;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

final class SqlVariableStore implements VariableStore {

    private final SqlDatabase database;

    SqlVariableStore(SqlDatabase database) {
        this.database = database;
    }

    private static void whereRef(Schema table, VariableRef ref) {
        table.where("scope", ref.scope().id());
        table.where("owner", ref.owner());
        table.where("name", ref.name());
    }

    private static BigDecimal toNumber(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal decimal) return decimal;
        return new BigDecimal(value.toString());
    }

    static Variable toVariable(VariableScope scope, Map<String, Object> row) {
        VariableRef ref = new VariableRef(scope, SqlRows.text(row, "owner"), SqlRows.text(row, "name"));
        BigDecimal number = toNumber(SqlRows.value(row, "number_value"));
        long expiresAt = SqlRows.number(row, "expires_at");
        if (number != null) return Variable.ofNumber(ref, number, expiresAt);
        String text = SqlRows.text(row, "text_value");
        return new Variable(ref, text == null ? "" : text, null, expiresAt);
    }

    /**
     * Writes a value. The version counts the writes of the row, starting at 1.
     */
    static void upsert(RequestHelper helper, Variable variable) {
        helper.upsert(Tables.VARIABLES, table -> {
            table.string("scope", variable.ref().scope().id()).primary();
            table.string("owner", variable.ref().owner()).primary();
            table.string("name", variable.ref().name()).primary();
            table.object("text_value", variable.number() == null ? variable.text() : null);
            table.object("number_value", variable.number());
            table.bigInt("expires_at", variable.expiresAt());
            table.increment("version", 1);
        });
    }

    /**
     * Adds to a value in the database itself.
     *
     * <p>An existing row is updated with {@code COALESCE(number_value, 0) + ?}, so a text value
     * that is not a number counts as 0. When there is no row, an upsert inserts the amount, or adds
     * it if another server inserted the row in the meantime.</p>
     */
    private static void increment(RequestHelper helper, VariableRef ref, BigDecimal delta, long expiresAtIfCreated) {
        int updated = helper.update(Tables.VARIABLES, table -> {
            table.expression("number_value", "COALESCE(`number_value`, 0) + ?", delta);
            table.object("text_value", null);
            table.expression("version", "`version` + 1");
            whereRef(table, ref);
        });
        if (updated > 0) return;

        helper.upsert(Tables.VARIABLES, table -> {
            table.string("scope", ref.scope().id()).primary();
            table.string("owner", ref.owner()).primary();
            table.string("name", ref.name()).primary();
            table.increment("number_value", delta);
            table.bigInt("expires_at", expiresAtIfCreated).insertOnly();
            table.increment("version", 1);
        });
    }

    private static BigDecimal readNumber(RequestHelper helper, VariableRef ref) {
        List<BigDecimal> values = helper.query("SELECT `number_value` FROM " + Tables.VARIABLES + " WHERE `scope` = ? AND `owner` = ? AND `name` = ?",
                resultSet -> resultSet.getBigDecimal(1), ref.scope().id(), ref.owner(), ref.name());
        BigDecimal value = values.isEmpty() ? null : values.get(0);
        return value == null ? BigDecimal.ZERO : value;
    }

    @Override
    public @NotNull CompletableFuture<List<Variable>> loadOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.database.read(() -> {
            List<Variable> variables = new ArrayList<>();
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.VARIABLES, table -> {
                table.where("scope", scope.id());
                table.where("owner", owner);
            })) {
                variables.add(toVariable(scope, row));
            }
            return variables;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> apply(@NotNull List<VariableMutation> mutations) {
        if (mutations.isEmpty()) return CompletableFuture.completedFuture(null);

        return this.database.write(() -> this.database.requestHelper().inTransaction(helper -> {
            for (VariableMutation mutation : mutations) {
                switch (mutation) {
                    case VariableMutation.Set set -> upsert(helper, set.variable());
                    case VariableMutation.Increment increment -> increment(helper, increment.ref(), increment.delta(), increment.expiresAtIfCreated());
                    case VariableMutation.Delete delete -> helper.delete(Tables.VARIABLES, table -> whereRef(table, delete.ref()));
                }
            }
            return null;
        }));
    }

    @Override
    public @NotNull CompletableFuture<BigDecimal> incrementAndGet(@NotNull VariableRef ref, @NotNull BigDecimal delta, long expiresAtIfCreated) {
        return this.database.writeAndGet(() -> this.database.requestHelper().inTransaction(helper -> {
            increment(helper, ref, delta, expiresAtIfCreated);
            return readNumber(helper, ref);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteName(@NotNull VariableScope scope, @NotNull String name) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.VARIABLES, table -> {
            table.where("scope", scope.id());
            table.where("name", name);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteOwner(@NotNull VariableScope scope, @NotNull String owner) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.VARIABLES, table -> {
            table.where("scope", scope.id());
            table.where("owner", owner);
        }));
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteScope(@NotNull VariableScope scope) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.VARIABLES, table -> table.where("scope", scope.id())));
    }

    @Override
    public @NotNull CompletableFuture<Set<String>> names(@NotNull VariableScope scope) {
        return this.database.read(() -> {
            Set<String> names = new HashSet<>();
            for (Map<String, Object> row : this.database.requestHelper().select(Tables.VARIABLES, table -> {
                table.distinct();
                table.addSelect("name");
                table.where("scope", scope.id());
            })) {
                String name = SqlRows.text(row, "name");
                if (name != null) names.add(name);
            }
            return names;
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> deleteExpired(long now) {
        return this.database.write(() -> this.database.requestHelper().delete(Tables.VARIABLES, table -> {
            table.where("expires_at", ">", 0);
            table.where("expires_at", "<", now);
        }));
    }
}
