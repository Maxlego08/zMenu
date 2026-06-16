package fr.maxlego08.menu.api.rules;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Helper class for extracting values from rule configuration maps.
 * Provides type-safe methods for common configuration value types.
 */
public final class RuleConfigHelper {

    private RuleConfigHelper() {
        // Utility class
    }

    /**
     * Gets a string value from the configuration map.
     *
     * @param map the configuration map
     * @param key the key to look up
     * @return the string value, or null if not present
     */
    public static String getString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    /**
     * Gets a string value with a default fallback.
     *
     * @param map          the configuration map
     * @param key          the key to look up
     * @param defaultValue the default value if not present
     * @return the string value or default
     */
    public static String getString(Map<String, Object> map, String key, String defaultValue) {
        String value = getString(map, key);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets a boolean value from the configuration map.
     *
     * @param map          the configuration map
     * @param key          the key to look up
     * @param defaultValue the default value if not present
     * @return the boolean value or default
     */
    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue) {
        Object value = map.get(key);
        if (value instanceof Boolean b) return b;
        if (value != null) return Boolean.parseBoolean(String.valueOf(value));
        return defaultValue;
    }

    /**
     * Gets an integer value from the configuration map.
     *
     * @param map          the configuration map
     * @param key          the key to look up
     * @param defaultValue the default value if not present or invalid
     * @return the integer value or default
     */
    public static int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number n) return n.intValue();
        if (value != null) {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * Gets a list of strings from the configuration map.
     *
     * @param map the configuration map
     * @param key the key to look up
     * @return list of strings (never null, may be empty)
     */
    @NotNull
    public static List<String> getStringList(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (!(raw instanceof List<?> list)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (Object o : list) {
            if (o != null) {
                result.add(String.valueOf(o));
            }
        }
        return result;
    }

    /**
     * Gets a list of integers from the configuration map.
     *
     * @param map the configuration map
     * @param key the key to look up
     * @return list of integers (never null, may be empty)
     */
    public static List<Integer> getIntegerList(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<Integer> result = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Number n) {
                result.add(n.intValue());
            } else if (o != null) {
                try {
                    result.add(Integer.parseInt(String.valueOf(o)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * Gets a list of maps from the configuration map.
     *
     * @param map the configuration map
     * @param key the key to look up
     * @return list of maps (never null, may be empty)
     */
    public static List<Map<String, Object>> getMapList(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (!(raw instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof Map<?, ?> m) {
                try {
                    //noinspection unchecked
                    result.add((Map<String, Object>) m);
                } catch (ClassCastException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * Gets an enum value from the configuration map.
     *
     * @param map          the configuration map
     * @param key          the key to look up
     * @param enumClass    the enum class to parse
     * @param defaultValue the default value if not present or invalid
     * @param <E>          the enum type
     * @return the enum value or default
     */
    @NotNull
    public static <E extends Enum<E>> E getEnum(Map<String, Object> map, String key, Class<E> enumClass, E defaultValue) {
        String value = getString(map, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}