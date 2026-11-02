package fr.maxlego08.menu.hooks.dialogs.click;

import fr.maxlego08.menu.api.ZMenuKeys;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.LongSupplier;

/**
 * The clicks of the dialogs a player sees, one table per player: the areas of canvases and the custom click
 * buttons of dialogs.
 * <p>
 * Each click gets a custom click event holding the number of the table and of the click. Opening a dialog
 * starts a new table, so the clicks of the dialogs shown before stop working and are forgotten at once:
 * the memory used is one table per player, whatever the number of clicks. A click also ends after its
 * lifetime or its uses, and the table ends when the player leaves.
 * <p>
 * The keys of the payload hold a colon, which dialog input keys cannot, so the values a dialog submits next
 * to them never hide them.
 */
public final class DialogClicks {

    /**
     * The identifier of the custom click events of zMenu dialogs.
     */
    public static final Key KEY = ZMenuKeys.key("dialog_click");

    /**
     * The most clicks one table holds: past it, a new table starts.
     */
    public static final int MAX_AREAS = 4096;

    /**
     * The uses of a click that works until it expires.
     */
    public static final int UNLIMITED_USES = ClickCallback.UNLIMITED_USES;

    private static final String TABLE_KEY = "zmenu:t";
    private static final String AREA_KEY = "zmenu:a";

    private final Map<UUID, Table> tables = new ConcurrentHashMap<>();
    private final ThreadLocal<UUID> viewer = new ThreadLocal<>();
    private final AtomicInteger nextTable = new AtomicInteger();
    private final LongSupplier clock;

    /**
     * @param clock The current time, in milliseconds.
     */
    public DialogClicks(@NotNull LongSupplier clock) {
        this.clock = clock;
    }

    private record Table(int id, @NotNull Map<Integer, Area> areas, @NotNull AtomicInteger next) {
    }

    private record Area(@NotNull BiConsumer<Player, DialogResponseView> action, long expiresAt, @NotNull AtomicInteger usesLeft) {
    }

    private @NotNull Table newTable() {
        return new Table(this.nextTable.incrementAndGet(), new ConcurrentHashMap<>(), new AtomicInteger());
    }

    /**
     * Starts a new table for a player: the clicks created before stop working.
     *
     * @param player The player.
     */
    public void reset(@NotNull UUID player) {
        this.tables.put(player, this.newTable());
    }

    /**
     * Starts a new table for the player a dialog is shown to and runs what builds and shows it. While it runs,
     * every click added goes to that table, whoever the dialog is built for (its target player). If it throws,
     * the table of the dialog shown before is restored, so its clicks keep working.
     *
     * @param viewer The player the dialog is shown to.
     * @param show   Builds and shows the dialog.
     */
    public void open(@NotNull UUID viewer, @NotNull Runnable show) {
        Table previous = this.tables.get(viewer);
        Table table = this.newTable();
        this.tables.put(viewer, table);
        UUID outer = this.viewer.get();
        this.viewer.set(viewer);
        try {
            show.run();
        } catch (RuntimeException | Error exception) {
            if (previous == null) this.tables.remove(viewer, table);
            else this.tables.replace(viewer, table, previous);
            throw exception;
        } finally {
            if (outer == null) this.viewer.remove();
            else this.viewer.set(outer);
        }
    }

    /**
     * Forgets the table of a player.
     *
     * @param player The player.
     */
    public void clear(@NotNull UUID player) {
        this.tables.remove(player);
    }

    /**
     * Adds a click to the table of a player, for a text or canvas area.
     *
     * @param player         The player the dialog is shown to.
     * @param action         What a click does. The view is null outside a dialog action.
     * @param uses           How many clicks work, or {@link #UNLIMITED_USES}.
     * @param lifetimeMillis How long the click works, in milliseconds.
     * @return The click event.
     */
    public @NotNull ClickEvent add(@NotNull UUID player, @NotNull BiConsumer<Player, DialogResponseView> action, int uses, long lifetimeMillis) {
        return ClickEvent.custom(KEY, this.register(player, action, uses, lifetimeMillis));
    }

    /**
     * Adds a click to the table of a player, for a dialog button. The click receives the values of the
     * dialog inputs.
     *
     * @param player         The player the dialog is shown to.
     * @param action         What a click does.
     * @param uses           How many clicks work, or {@link #UNLIMITED_USES}.
     * @param lifetimeMillis How long the click works, in milliseconds.
     * @return The dialog action.
     */
    public @NotNull DialogAction dialogAction(@NotNull UUID player, @NotNull BiConsumer<Player, DialogResponseView> action, int uses, long lifetimeMillis) {
        return DialogAction.customClick(KEY, this.register(player, action, uses, lifetimeMillis));
    }

    private @NotNull BinaryTagHolder register(@NotNull UUID player, @NotNull BiConsumer<Player, DialogResponseView> action, int uses, long lifetimeMillis) {
        UUID viewer = this.viewer.get();
        Table table = this.tables.compute(viewer == null ? player : viewer,(uuid, current) -> current == null || current.next().get() >= MAX_AREAS ? this.newTable() : current);
        int area = table.next().getAndIncrement();
        table.areas().put(area, new Area(action, this.clock.getAsLong() + lifetimeMillis, new AtomicInteger(uses)));
        return BinaryTagHolder.binaryTagHolder("{\"" + TABLE_KEY + "\":" + table.id() + ",\"" + AREA_KEY + "\":" + area + "}");
    }

    /**
     * Takes one use of a click.
     *
     * @param player  The player who clicked.
     * @param payload The payload of the click event as SNBT, its keys in any order, with or without the
     *                values of the dialog inputs.
     * @return What the click does, or null when the click is from an older table, expired, used up or unknown.
     */
    public @Nullable BiConsumer<Player, DialogResponseView> use(@NotNull UUID player, @NotNull String payload) {
        Map<String, String> entries = topLevelEntries(payload);
        if (entries == null) return null;
        Integer tableId = parseInt(entries.get(TABLE_KEY));
        Integer areaId = parseInt(entries.get(AREA_KEY));
        if (tableId == null || areaId == null) return null;
        Table table = this.tables.get(player);
        if (table == null || table.id() != tableId) return null;
        Area area = table.areas().get(areaId);
        if (area == null) return null;
        if (this.clock.getAsLong() > area.expiresAt()) {
            table.areas().remove(areaId);
            return null;
        }
        if (area.usesLeft().get() != UNLIMITED_USES) {
            int left = area.usesLeft().getAndUpdate(value -> value > 0 ? value - 1 : value);
            if (left <= 0) return null;
            if (left == 1) table.areas().remove(areaId);
        }
        return area.action();
    }

    private static @Nullable Integer parseInt(@Nullable String value) {
        if (value == null) return null;
        String number = value.endsWith("b") || value.endsWith("B") || value.endsWith("s") || value.endsWith("S") || value.endsWith("l") || value.endsWith("L")
                ? value.substring(0, value.length() - 1) : value;
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    /**
     * Reads the entries of the outer compound of an SNBT text, without looking inside quoted strings, lists or
     * nested compounds: a value a player typed in a dialog input can never be read as one of zMenu's keys.
     *
     * @param snbt The SNBT text.
     * @return The raw values by key, quotes removed, or null when the text is not a compound.
     */
    static @Nullable Map<String, String> topLevelEntries(@NotNull String snbt) {
        Map<String, String> entries = new HashMap<>();
        int[] cursor = {skipSpaces(snbt, 0)};
        if (cursor[0] >= snbt.length() || snbt.charAt(cursor[0]) != '{') return null;
        cursor[0]++;
        while (true) {
            cursor[0] = skipSpaces(snbt, cursor[0]);
            if (cursor[0] >= snbt.length()) return null;
            if (snbt.charAt(cursor[0]) == '}') return entries;
            String key = readToken(snbt, cursor, ':');
            if (key == null) return null;
            cursor[0] = skipSpaces(snbt, cursor[0]);
            if (cursor[0] >= snbt.length() || snbt.charAt(cursor[0]) != ':') return null;
            cursor[0] = skipSpaces(snbt, cursor[0] + 1);
            if (cursor[0] >= snbt.length()) return null;
            char first = snbt.charAt(cursor[0]);
            if (first == '{' || first == '[') {
                cursor[0] = skipNested(snbt, cursor[0]);
                if (cursor[0] < 0) return null;
            } else {
                String value = readToken(snbt, cursor, ',');
                if (value == null) return null;
                entries.putIfAbsent(key, value);
            }
            cursor[0] = skipSpaces(snbt, cursor[0]);
            if (cursor[0] < snbt.length() && snbt.charAt(cursor[0]) == ',') cursor[0]++;
        }
    }

    private static int skipSpaces(@NotNull String text, int index) {
        while (index < text.length() && Character.isWhitespace(text.charAt(index))) index++;
        return index;
    }

    private static @Nullable String readToken(@NotNull String text, int @NotNull [] cursor, char end) {
        int index = cursor[0];
        if (index >= text.length()) return null;
        char quote = text.charAt(index);
        if (quote == '"' || quote == '\'') {
            StringBuilder builder = new StringBuilder();
            index++;
            while (index < text.length()) {
                char character = text.charAt(index++);
                if (character == '\\' && index < text.length()) {
                    builder.append(text.charAt(index++));
                } else if (character == quote) {
                    cursor[0] = index;
                    return builder.toString();
                } else {
                    builder.append(character);
                }
            }
            return null;
        }
        int start = index;
        while (index < text.length()) {
            char character = text.charAt(index);
            if (character == end || character == '}' || character == ',') break;
            index++;
        }
        cursor[0] = index;
        return text.substring(start, index).trim();
    }

    private static int skipNested(@NotNull String text, int index) {
        int depth = 0;
        while (index < text.length()) {
            char character = text.charAt(index);
            if (character == '"' || character == '\'') {
                int[] cursor = {index};
                if (readToken(text, cursor, ',') == null) return -1;
                index = cursor[0];
                continue;
            }
            if (character == '{' || character == '[') depth++;
            if (character == '}' || character == ']') {
                depth--;
                if (depth == 0) return index + 1;
            }
            index++;
        }
        return -1;
    }

    /**
     * @return The number of players with a table.
     */
    public int size() {
        return this.tables.size();
    }
}
