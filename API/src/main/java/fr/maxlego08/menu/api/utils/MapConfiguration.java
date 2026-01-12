package fr.maxlego08.menu.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This interface provides typed access to a map of key-value pairs, allowing retrieval
 * of values of different types with or without default values if the key is not present in the map.
 */
public interface MapConfiguration {

    /**
     * Retrieves a string value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The string value associated with the key or null if the key is not present.
     */
    @Nullable
    String getString(@NotNull String key);

    /**
     * Retrieves a string value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The string value associated with the key or the default value.
     */
    String getString(String key, String defaultValue);

    /**
     * Retrieves an integer value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The integer value associated with the key or 0 if the key is not present.
     */
    int getInt(String key);

    /**
     * Retrieves an integer value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The integer value associated with the key or the default value.
     */
    int getInt(String key, int defaultValue);

    /**
     * Retrieves a long value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The long value associated with the key or 0L if the key is not present.
     */
    long getLong(String key);

    /**
     * Retrieves a long value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The long value associated with the key or the default value.
     */
    long getLong(String key, long defaultValue);

    /**
     * Retrieves a list of strings from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The list of strings associated with the key or an empty list if the key is not present.
     */
    List<String> getStringList(String key);

    /**
     * Retrieves a list of strings from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The list of strings associated with the key or the default value.
     */
    List<String> getStringList(String key, List<String> defaultValue);

    /**
     * Retrieves a list of integers from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The list of integers associated with the key or an empty list if the key is not present.
     */
    List<Integer> getIntList(String key);

    /**
     * Retrieves a list of integers from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The list of integers associated with the key or the default value.
     */
    List<Integer> getIntList(String key, List<Integer> defaultValue);

    /**
     * Retrieves a boolean value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The boolean value associated with the key or false if the key is not present.
     */
    boolean getBoolean(String key);

    /**
     * Retrieves a boolean value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The boolean value associated with the key or the default value.
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Retrieves a double value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The double value associated with the key or 0.0 if the key is not present.
     */
    double getDouble(String key);

    /**
     * Retrieves a double value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The double value associated with the key or the default value.
     */
    double getDouble(String key, double defaultValue);

    /**
     * Retrieves an object from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The object associated with the key or null if the key is not present.
     */
    Object getObject(String key);

    /**
     * Retrieves an object from the map based on the provided key.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The object associated with the key or null if the key is not present.
     */
    Object getObject(String key, Object defaultValue);

    /**
     * Retrieves a float value from the map based on the provided key.
     *
     * @param key The key to retrieve the value.
     * @return The float value associated with the key or 0.0f if the key is not present.
     */
    float getFloat(String key);

    /**
     * Retrieves a float value from the map based on the provided key.
     * If the key is not present, returns the specified default value.
     *
     * @param key          The key to retrieve the value.
     * @param defaultValue The default value if the key is not present.
     * @return The float value associated with the key or the default value.
     */
    float getFloat(String key, float defaultValue);

    List<?> getList(String key);

    boolean contains(String key);
}
