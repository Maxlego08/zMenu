package fr.maxlego08.menu.api.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class provides typed access to a {@code Map<String, Object>}, allowing retrieval
 * of values of different types with or without default values if the key is not present in the map.
 */
public class TypedMapAccessor {

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
    public String getString(String key) {
        return (String) map.get(key);
    }

    /**
     * Retrieves a string value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The string value associated with the key or the default value.
     */
    public String getString(String key, String defaultValue) {
        return (String) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves an integer value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The integer value associated with the key or 0 if the key is not present.
     */
    public int getInt(String key) {
        return (int) map.getOrDefault(key, 0);
    }

    /**
     * Retrieves an integer value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The integer value associated with the key or the default value.
     */
    public int getInt(String key, int defaultValue) {
        return (int) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a long value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The long value associated with the key or 0L if the key is not present.
     */
    public long getLong(String key) {
        return (long) map.getOrDefault(key, 0L);
    }

    /**
     * Retrieves a long value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The long value associated with the key or the default value.
     */
    public long getLong(String key, long defaultValue) {
        return (long) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a list of strings from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The list of strings associated with the key or an empty list if the key is not present.
     */
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
    public List<String> getStringList(String key, List<String> defaultValue) {
        return (List<String>) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a list of integers from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The list of integers associated with the key or an empty list if the key is not present.
     */
    public List<Integer> getIntList(String key) {
        return (List<Integer>) map.getOrDefault(key, Collections.emptyList());
    }

    /**
     * Retrieves a list of integers from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The list of integers associated with the key or the default value.
     */
    public List<Integer> getIntList(String key, List<Integer> defaultValue) {
        return (List<Integer>) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a boolean value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The boolean value associated with the key or false if the key is not present.
     */
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
    public boolean getBoolean(String key, boolean defaultValue) {
        return (boolean) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a double value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The double value associated with the key or 0.0 if the key is not present.
     */
    public double getDouble(String key) {
        return (double) map.getOrDefault(key, 0.0);
    }

    /**
     * Retrieves a double value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The double value associated with the key or the default value.
     */
    public double getDouble(String key, double defaultValue) {
        return (double) map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves an object from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The object associated with the key or null if the key is not present.
     */
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
    public Object getObject(String key, Object defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    /**
     * Retrieves a float value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The float value associated with the key or 0.0f if the key is not present.
     */
    public float getFloat(String key) {
        return (float) map.getOrDefault(key, 0.0f);
    }

    /**
     * Retrieves a float value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The float value associated with the key or the default value.
     */
    public float getFloat(String key, float defaultValue) {
        return (float) map.getOrDefault(key, defaultValue);
    }
}
