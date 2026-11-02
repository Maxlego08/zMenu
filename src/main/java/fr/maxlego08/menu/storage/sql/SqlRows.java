package fr.maxlego08.menu.storage.sql;

import fr.maxlego08.sarah.RequestHelper;
import fr.maxlego08.sarah.database.Schema;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Reads columns from the rows Sarah returns as maps.
 *
 * <p>Rows are read by column name rather than mapped onto a record: Sarah matches record
 * components to columns by their Java name, so {@code worldName} never matched the
 * {@code world_name} column and the visited worlds and played animations always came back empty.</p>
 */
final class SqlRows {

    private SqlRows() {
    }

    static Object value(Map<String, Object> row, String column) {
        Object value = row.get(column);
        if (value != null || row.containsKey(column)) return value;
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(column)) return entry.getValue();
        }
        return null;
    }

    static String text(Map<String, Object> row, String column) {
        Object value = value(row, column);
        return value == null ? null : value.toString();
    }

    /**
     * @return the distinct values of a column
     */
    static Set<String> distinct(RequestHelper helper, String table, String column, Consumer<Schema> conditions) {
        Set<String> values = new HashSet<>();
        for (Map<String, Object> row : helper.select(table, schema -> {
            schema.distinct();
            schema.addSelect(column);
            conditions.accept(schema);
        })) {
            String value = text(row, column);
            if (value != null) values.add(value);
        }
        return values;
    }

    static Set<UUID> distinctPlayers(RequestHelper helper, String table) {
        Set<UUID> players = new HashSet<>();
        for (String value : distinct(helper, table, "player_id", schema -> {
        })) {
            players.add(UUID.fromString(value));
        }
        return players;
    }

    static long number(Map<String, Object> row, String column) {
        Object value = value(row, column);
        if (value instanceof Number number) return number.longValue();
        return value == null ? 0 : Long.parseLong(value.toString());
    }
}
