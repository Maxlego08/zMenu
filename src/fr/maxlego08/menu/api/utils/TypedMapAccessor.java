package fr.maxlego08.menu.api.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class provides typed access to a {@code Map<String, Object>}, allowing retrieval
 * of values of different types with or without default values if the key is not present in the map.
 */
public class TypedMapAccessor implements MapConfiguration {

    private final Map<String, Object> map;

    /**
     * Constructs a TypedMapAccessor with the specified map.
     *
     * @param map The map containing key-value pairs.
     */
    public TypedMapAccessor(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Retrieves a string value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The string value associated with the key or null if the key is not present.
     */
    @Override
    public String getString(String key) {
        return String.valueOf(map.get(key));
    }

    /**
     * Retrieves a string value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The string value associated with the key or the default value.
     */
    @Override
    public String getString(String key, String defaultValue) {
        return map.containsKey(key) ? String.valueOf(map.get(key)) : defaultValue;
    }

    /**
     * Retrieves an integer value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The integer value associated with the key or 0 if the key is not present or not a number.
     */
    @Override
    public int getInt(String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * Retrieves an integer value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present or not a number.
     * @return The integer value associated with the key or the default value.
     */
    @Override
    public int getInt(String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * Retrieves a long value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The long value associated with the key or 0L if the key is not present or not a number.
     */
    @Override
    public long getLong(String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    /**
     * Retrieves a long value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present or not a number.
     * @return The long value associated with the key or the default value.
     */
    @Override
    public long getLong(String key, long defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    /**
     * Retrieves a list of strings from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The list of strings associated with the key or an empty list if the key is not present.
     */
    @Override
    public List<String> getStringList(String key) {
        return (List<String>) map.getOrDefault(key, Collections.emptyList());
    }

    /**
     * Retrieves a list of strings from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The list of strings associated with the key or the default value.
     */
    @Override
    public List<String> getStringList(String key, List<String> defaultValue) {
        return (List<String>) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a list of integers from the map based on the provided key.
     * If the values in the list are instances of Number, they are converted to Integer.
     *
     * @param key The key to retrieve the value.
     * @return The list of integers associated with the key or an empty list if the key is not present or if the values are not numeric.
     */
    @Override
    public List<Integer> getIntList(String key) {
        Object value = map.get(key);
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            return convertToIntegerList(list);
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves a list of integers from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     * If the values in the list are instances of Number, they are converted to Integer.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present or if the values are not numeric.
     * @return The list of integers associated with the key or the default value.
     */
    @Override
    public List<Integer> getIntList(String key, List<Integer> defaultValue) {
        Object value = map.get(key);
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            return convertToIntegerList(list);
        }
        return defaultValue;
    }

    /**
     * Helper method to convert a list of objects to a list of integers.
     * If an element is a Number, it is converted to an Integer.
     * If an element is not a Number, it is skipped.
     *
     * @param list The list of objects to convert.
     * @return A list of integers converted from the original list.
     */
    private List<Integer> convertToIntegerList(List<?> list) {
        List<Integer> intList = new ArrayList<>();
        for (Object obj : list) {
            if (obj instanceof Number) {
                intList.add(((Number) obj).intValue());
            }
        }
        return intList;
    }

    /**
     * Retrieves a boolean value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The boolean value associated with the key or false if the key is not present.
     */
    @Override
    public boolean getBoolean(String key) {
        return (boolean) map.getOrDefault(key, false);
    }

    /**
     * Retrieves a boolean value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The boolean value associated with the key or the default value.
     */
    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return (boolean) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a double value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The double value associated with the key or 0.0 if the key is not present or not a number.
     */
    @Override
    public double getDouble(String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    /**
     * Retrieves a double value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present or not a number.
     * @return The double value associated with the key or the default value.
     */
    @Override
    public double getDouble(String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Retrieves an object from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The object associated with the key or null if the key is not present.
     */
    @Override
    public Object getObject(String key) {
        return map.get(key);
    }

    /**
     * Retrieves an object from the map based on the provided key.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The object associated with the key or null if the key is not present.
     */
    @Override
    public Object getObject(String key, Object defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a float value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The float value associated with the key or 0.0f if the key is not present or not a number.
     */
    @Override
    public float getFloat(String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return 0.0f;
    }

    /**
     * Retrieves a float value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present or not a number.
     * @return The float value associated with the key or the default value.
     */
    @Override
    public float getFloat(String key, float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }

    @Override
    public List<?> getList(String key) {
        return (List<?>) map.get(key);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public String toString() {
        return "TypedMapAccessor{" +
                "map=" + map +
                '}';
    }
}
