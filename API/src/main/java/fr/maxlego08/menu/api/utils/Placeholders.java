package fr.maxlego08.menu.api.utils;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Placeholder container and parser, with parent/child scopes.
 *
 * <h2>Scopes</h2>
 * <ul>
 *   <li>{@link #child()} creates a child scope. The child reads its own placeholders first, then its
 *       parent's, then the grandparent's, and so on (a child can shadow a parent key).</li>
 *   <li>{@link #register(String, String)} writes to the current scope only. The parent never sees it.</li>
 *   <li>{@link #register(String, String, Scope)} can write up to the parent or to the root of the chain.</li>
 *   <li>Dropping a child is the cleanup: its placeholders disappear with it.</li>
 * </ul>
 */
public final class Placeholders {
    public enum Scope {
        LOCAL,
        PARENT,
        ROOT
    }

    @FunctionalInterface
    public interface Modifier {
        @Nullable String apply(@NotNull String value, @NotNull String[] args);
    }

    private static final String[] NO_ARGS = new String[0];
    private static final Map<String, Modifier> MODIFIERS = new ConcurrentHashMap<>();
    private static final Map<String, Modifier> LEGACY_PREFIXES = new LinkedHashMap<>();

    static {
        registerModifier("upper", (v, a) -> v.toUpperCase(Locale.ROOT));
        registerModifier("lower", (v, a) -> v.toLowerCase(Locale.ROOT));
        registerModifier("capitalize", (v, a) -> v.isEmpty() ? v
                : v.substring(0, 1).toUpperCase(Locale.ROOT) + v.substring(1).toLowerCase(Locale.ROOT));
        registerModifier("trim", (v, a) -> v.trim());
        registerModifier("length", (v, a) -> String.valueOf(v.length()));
        registerModifier("empty", (v, a) -> String.valueOf(v.isBlank()));
        registerModifier("isnumber", (v, a) -> String.valueOf(parseNumber(v) != null));
        registerModifier("safe", (v, a) -> v.replaceAll("[;&§\\r\\n]", ""));
        registerModifier("default", (v, a) -> v.isBlank() && a.length > 0 ? a[0] : v);
        registerModifier("max", (v, a) -> {
            int n = Integer.parseInt(a[0]);
            return v.length() <= n ? v : v.substring(0, n);
        });
        registerModifier("pad", (v, a) -> {
            int width = Integer.parseInt(a[0]);
            StringBuilder sb = new StringBuilder();
            sb.repeat("0", Math.max(0, width - v.length()));
            return sb.append(v).toString();
        });

        registerModifier("int", numeric(d -> String.valueOf((long) d)));
        registerModifier("round", numeric(d -> String.valueOf(Math.round(d))));
        registerModifier("floor", numeric(d -> String.valueOf((long) Math.floor(d))));
        registerModifier("ceil", numeric(d -> String.valueOf((long) Math.ceil(d))));
        registerModifier("abs", numeric(d -> formatNumber(Math.abs(d))));
        registerModifier("negative", numeric(d -> String.valueOf(d < 0)));
        registerModifier("even", numeric(d -> String.valueOf(((long) d) % 2 == 0)));
        registerModifier("0f", numeric(d -> String.format(Locale.US, "%.0f", d)));
        registerModifier("1f", numeric(d -> String.format(Locale.US, "%.1f", d)));
        registerModifier("2f", numeric(d -> String.format(Locale.US, "%.2f", d)));
        registerModifier("3f", numeric(d -> String.format(Locale.US, "%.3f", d)));
        registerModifier("comma", numeric(d -> d == Math.rint(d)
                ? String.format(Locale.US, "%,d", (long) d)
                : String.format(Locale.US, "%,.2f", d)));
        registerModifier("add_one", numeric(d -> formatNumber(d + 1)));
        registerModifier("remove_one", numeric(d -> formatNumber(d - 1)));
        registerModifier("add", (v, a) -> {
            Double d = parseNumber(v), n = parseNumber(a[0]);
            return d == null || n == null ? null : formatNumber(d + n);
        });
        registerModifier("sub", (v, a) -> {
            Double d = parseNumber(v), n = parseNumber(a[0]);
            return d == null || n == null ? null : formatNumber(d - n);
        });

        LEGACY_PREFIXES.put("upper_", MODIFIERS.get("upper"));
        LEGACY_PREFIXES.put("lower_", MODIFIERS.get("lower"));
        LEGACY_PREFIXES.put("capitalize_", (v, a) -> v.isEmpty() ? v : v.substring(0, 1).toUpperCase(Locale.ROOT) + v.substring(1));
        LEGACY_PREFIXES.put("add_one_", MODIFIERS.get("add_one"));
        LEGACY_PREFIXES.put("remove_one_", MODIFIERS.get("remove_one"));
    }

    private final Placeholders parent;
    private final Map<String, String> placeholders;

    public Placeholders() {
        this(new ConcurrentHashMap<>(), null);
    }

    public Placeholders(@NotNull Map<String, String> placeholders) {
        this(placeholders, null);
    }

    private Placeholders(@NotNull Map<String, String> placeholders, @Nullable Placeholders parent) {
        Preconditions.checkNotNull(placeholders, "placeholders cannot be null");
        this.placeholders = placeholders;
        this.parent = parent;
    }

    public static void registerModifier(@NotNull String name, @NotNull Modifier modifier) {
        Preconditions.checkNotNull(name, "name cannot be null");
        Preconditions.checkArgument(!name.isBlank(), "name cannot be blank");
        Preconditions.checkNotNull(modifier, "modifier cannot be null");
        MODIFIERS.put(name.toLowerCase(Locale.ROOT), modifier);
    }

    /**
     * Creates a child scope. The child sees everything this scope sees, but what the child registers
     * stays in the child (unless registered with {@link Scope#PARENT} or {@link Scope#ROOT}).
     */
    @NotNull
    public Placeholders child() {
        return new Placeholders(new ConcurrentHashMap<>(), this);
    }

    /**
     * Returns the parent scope, or null when this scope is the root.
     * @return the parent scope, or null when this scope is the root.
     */
    @Nullable
    public Placeholders getParent() {
        return this.parent;
    }

    /**
     * Returns the root scope (the top of the chain). This instance when it has no parent.
     * @return the root scope (the top of the chain). This instance when it has no parent.
     */
    @NotNull
    public Placeholders getRoot() {
        Placeholders current = this;
        while (current.parent != null) current = current.parent;
        return current;
    }

    /**
     * Finds a value in this scope, then in each parent. Returns null when the key is unknown everywhere.
     * @param key the key to look up
     * @return the value associated with the key, or null if not found
     */
    @Nullable
    public String get(@NotNull String key) {
        Preconditions.checkNotNull(key, "key cannot be null");
        for (Placeholders scope = this; scope != null; scope = scope.parent) {
            String value = scope.placeholders.get(key);
            if (value != null) return value;
        }
        return null;
    }

    /**
     * Checks if a key is present in this scope or any of its parent scopes.
     *
     * @param key the key to check for
     * @return true if the key is present, false otherwise
     */
    public boolean has(@NotNull String key) {
        return this.get(key) != null;
    }

    /**
     * Snapshot of everything visible from this scope (parents first, children override).
     * The returned map is a copy: changing it does not change any scope.
     */
    @NotNull
    public Map<String, String> flatten() {
        List<Placeholders> chain = new ArrayList<>();
        for (Placeholders scope = this; scope != null; scope = scope.parent) chain.add(scope);

        Map<String, String> result = new LinkedHashMap<>();
        for (int i = chain.size() - 1; i >= 0; i--) {
            result.putAll(chain.get(i).placeholders);
        }
        return result;
    }

    // ------------------------------------------------------------------ public API (unchanged signatures)

    /**
     * Registers a placeholder in the current scope. A null key is ignored and a null value is stored
     * as an empty string.
     */
    public void register(@Nullable String key, @Nullable String value) {
        this.register(key, value, Scope.LOCAL);
    }

    /**
     * Registers a placeholder in the given scope.
     * {@link Scope#PARENT} and {@link Scope#ROOT} fall back to the current scope when there is no parent.
     */
    public void register(@Nullable String key, @Nullable String value, @NotNull Scope scope) {
        if (key == null) return;
        Placeholders target = switch (scope) {
            case LOCAL -> this;
            case PARENT -> this.parent != null ? this.parent : this;
            case ROOT -> this.getRoot();
        };
        target.placeholders.put(key, value == null ? "" : value);
    }

    /**
     * @return the live map of this scope when it has no parent (same as before), otherwise a snapshot of
     * everything visible from this scope, see {@link #flatten()}.
     * @deprecated exposes the internal map. Use {@link #register(String, String)}, {@link #get(String)},
     * {@link #flatten()} and {@link #merge(Placeholders)} instead.
     */
    @Deprecated
    @NotNull
    public Map<String, String> placeholders() {
        return this.parent == null ? this.placeholders : this.flatten();
    }

    @NotNull
    public List<String> parse(@NotNull List<String> strings) {
        Preconditions.checkNotNull(strings, "strings cannot be null");
        List<String> parsed = new ArrayList<>(strings.size());
        for (String string : strings) {
            parsed.add(this.parse(string));
        }
        return parsed;
    }

    @NotNull
    public String parse(@NotNull String string) {
        Preconditions.checkNotNull(string, "string cannot be null");
        if (string.indexOf('%') < 0 || !this.hasAny()) return string;
        return parseInternal(string, this::get);
    }

    /**
     * Parses {@code string} with one single key/value, without registering it.
     *
     * @deprecated prefer registering the key and calling {@link #parse(String)}.
     */
    @Deprecated
    @NotNull
    public String parse(@NotNull String string, @NotNull String key, @NotNull String value) {
        Preconditions.checkNotNull(string, "string cannot be null");
        Preconditions.checkNotNull(key, "key cannot be null");
        Preconditions.checkNotNull(value, "value cannot be null");
        if (string.indexOf('%') < 0) return string;
        return parseInternal(string, k -> k.equals(key) ? value : null);
    }

    /** Copies the placeholders registered in {@code placeholders} (that scope only, not its parents) into this scope. */
    public void merge(@NotNull Placeholders placeholders) {
        this.placeholders.putAll(placeholders.placeholders);
    }

    private boolean hasAny() {
        for (Placeholders scope = this; scope != null; scope = scope.parent) {
            if (!scope.placeholders.isEmpty()) return true;
        }
        return false;
    }

    /**
     * Parses a string, replacing placeholders with their values.
     *
     * @param input the string to parse
     * @param lookup a function to look up placeholder values
     * @return the parsed string
     */
    private static String parseInternal(String input, Function<String, String> lookup) {
        int length = input.length();
        StringBuilder out = new StringBuilder(length + 16);
        int i = 0;

        while (i < length) {
            char c = input.charAt(i);
            if (c != '%') {
                out.append(c);
                i++;
                continue;
            }

            int end = input.indexOf('%', i + 1);
            if (end < 0) {
                out.append(input, i, length);
                break;
            }

            String resolved = resolve(input.substring(i + 1, end), lookup);
            if (resolved == null) {
                out.append('%');
                i++;
            } else {
                out.append(resolved);
                i = end + 1;
            }
        }
        return out.toString();
    }

    /**
     * Resolves a placeholder token to its value.
     *
     * @param token the token to resolve
     * @param lookup a function to look up placeholder values
     * @return the resolved value, or null if the token is not recognized
     */
    @Nullable
    private static String resolve(String token, Function<String, String> lookup) {
        if (token.isEmpty()) return null;

        String direct = lookup.apply(token);
        if (direct != null) return direct;

        for (int dot = token.lastIndexOf('.'); dot > 0; dot = token.lastIndexOf('.', dot - 1)) {
            String base = lookup.apply(token.substring(0, dot));
            if (base != null) {
                return applyChain(base, token.substring(dot + 1));
            }
        }

        for (Map.Entry<String, Modifier> entry : LEGACY_PREFIXES.entrySet()) {
            String prefix = entry.getKey();
            if (token.startsWith(prefix)) {
                String base = lookup.apply(token.substring(prefix.length()));
                if (base != null) {
                    return applyOne(entry.getValue(), base, NO_ARGS);
                }
            }
        }
        return null;
    }

    @NotNull
    private static String applyChain(String value, String chain) {
        for (String segment : chain.split("\\.", -1)) {
            int colon = segment.indexOf(':');
            String name = (colon < 0 ? segment : segment.substring(0, colon)).toLowerCase(Locale.ROOT);
            String[] args = colon < 0 ? NO_ARGS : segment.substring(colon + 1).split(":", -1);

            Modifier modifier = MODIFIERS.get(name);
            if (modifier == null) return "";

            String result = tryApply(modifier, value, args);
            if (result == null) return "";
            value = result;
        }
        return value;
    }

    @NotNull
    private static String applyOne(Modifier modifier, String value, String[] args) {
        String result = tryApply(modifier, value, args);
        return result == null ? "" : result;
    }

    @Nullable
    private static String tryApply(Modifier modifier, String value, String[] args) {
        try {
            return modifier.apply(value, args);
        } catch (RuntimeException e) {   // bad argument, missing argument, ...
            return null;
        }
    }

    private static Modifier numeric(DoubleFunction<String> function) {
        return (value, args) -> {
            Double number = parseNumber(value);
            return number == null ? null : function.apply(number);
        };
    }

    @Nullable
    private static Double parseNumber(String value) {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Formats a number for display.
     *
     * @param d the number to format
     * @return the formatted string
     */
    private static String formatNumber(double d) {
        return d == Math.rint(d) && Math.abs(d) < 1e15 ? String.valueOf((long) d) : String.valueOf(d);
    }
}