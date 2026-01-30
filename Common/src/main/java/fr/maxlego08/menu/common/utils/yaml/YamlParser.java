package fr.maxlego08.menu.common.utils.yaml;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class YamlParser {

    private static final Placeholders PLACEHOLDER_PARSER = new Placeholders();

    /**
     * Parse a YamlConfiguration with placeholders replacements.
     * Iterates through the configuration structure recursively.
     *
     * @param configuration the configuration to parse
     * @param placeholders  the placeholders map (key -> value)
     * @return the parsed YamlConfiguration
     */
    @Contract("null, _ -> null")
    public static YamlConfiguration parseConfiguration(@Nullable YamlConfiguration configuration, @NotNull Map<String, Object> placeholders) {
        if (configuration == null) return null;

        try {
            YamlConfiguration parsedConfig = new YamlConfiguration();
            parseSection(configuration, parsedConfig, "", placeholders);
            return parsedConfig;
        } catch (Exception e) {
            if (Configuration.enableDebug)
                e.printStackTrace();
            return configuration;
        }
    }

    /**
     * Load a YAML file and parse it with placeholders replacements.
     *
     * @param file         the file to load
     * @param placeholders the placeholders map (key -> value)
     * @return the parsed YamlConfiguration
     */
    @NotNull
    public static YamlConfiguration loadAndParseFile(@NotNull File file, @NotNull Map<String, Object> placeholders) {
        try {
            Optional<YamlConfiguration> yamlConfiguration = YamlFileCache.getYamlConfiguration(file.toPath());
            if (yamlConfiguration.isEmpty()) {
                return new YamlConfiguration();
            }
            return parseConfiguration(yamlConfiguration.get(), placeholders);
        } catch (Exception exception) {
            if (Configuration.enableDebug)
                exception.printStackTrace();
            return new YamlConfiguration();
        }
    }

    private static void parseSection(@NotNull ConfigurationSection source, @NotNull ConfigurationSection destination,
                              @NotNull String currentPath, @NotNull Map<String, Object> placeholders) {
        for (String key : source.getKeys(false)) {
            String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;
            Object value = source.get(key);

            switch (value) {
                case ConfigurationSection section -> parseSection(section, destination.createSection(key), fullPath, placeholders);
                case List<?> list -> destination.set(key, parseList(list, placeholders));
                case String string -> destination.set(key, parseStringValue(string, placeholders));
                case null, default -> destination.set(key, value);
            }
        }
    }

    private static void parseMap(@NotNull Map<String, Object> map, @NotNull Map<String, Object> destination,
                                 @NotNull Map<String, Object> placeholders) {
        Map<String, List<?>> listPlaceholders = extractListPlaceholders(placeholders);
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            switch (value) {
                case ConfigurationSection section -> destination.put(key, configurationSectionToMap(section, placeholders));
                case List<?> list -> destination.put(key, parseList(list, placeholders));
                case String string -> {
                    String listPlaceholderKey = findListPlaceholderInString(string, listPlaceholders);

                    if (listPlaceholderKey != null) {
                        List<?> listValue = listPlaceholders.get(listPlaceholderKey);
                        List<String> expandedStrings = expandStringWithListPlaceholder(string, listPlaceholderKey, listValue, placeholders);
                        
                        if (expandedStrings.size() == 1) {
                            destination.put(key, expandedStrings.getFirst());
                        } else {
                            destination.put(key, expandedStrings);
                        }
                    } else {
                        destination.put(key, parseStringValue(string, placeholders));
                    }
                }
                case Map<?,?> nestedMap -> {
                    Map<String, Object> parsedMap = new HashMap<>();
                    //noinspection unchecked
                    parseMap((Map<String, Object>) nestedMap, parsedMap, placeholders);
                    destination.put(key, parsedMap);
                }
                case null, default -> destination.put(key, value);
            }
        }
    }

    @NotNull
    private static List<Object> parseList(@NotNull List<?> list, @NotNull Map<String, Object> placeholders) {
        List<Object> result = new ArrayList<>();
        Map<String, List<?>> listPlaceholders = extractListPlaceholders(placeholders);

        for (Object item : list) {
            switch (item) {
                case ConfigurationSection section -> result.add(configurationSectionToMap(section, placeholders));
                case List<?> nestedList -> result.add(parseList(nestedList, placeholders));
                case String string -> processStringValue(string, listPlaceholders, placeholders, result::add);
                case Map<?,?> map -> {
                    Map<String, Object> parsedMap = new HashMap<>();
                    //noinspection unchecked
                    parseMap((Map<String, Object>) map, parsedMap, placeholders);
                    result.add(parsedMap);
                }
                case null, default -> result.add(item);
            }
        }

        return result;
    }


    @NotNull
    private static String parseStringValue(@NotNull String value, @NotNull Map<String, Object> placeholders) {
        String result = value;

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            Object placeholderValue = entry.getValue();
            String replacementValue = placeholderValue != null ? placeholderValue.toString() : "";
            
            result = PLACEHOLDER_PARSER.parse(result, key, replacementValue);
        }

        return result;
    }

    /**
     * Converts a ConfigurationSection to a Map by parsing it.
     *
     * @param section the ConfigurationSection to convert
     * @param placeholders the placeholders map
     * @return the parsed Map
     */
    @NotNull
    private static Map<String, Object> configurationSectionToMap(@NotNull ConfigurationSection section, @NotNull Map<String, Object> placeholders) {
        YamlConfiguration temp = new YamlConfiguration();
        parseSection(section, temp, "", placeholders);
        return temp.getValues(true);
    }

    /**
     * Expands a string that contains list placeholders by creating multiple entries.
     * Returns a list containing the expanded strings.
     *
     * @param string the string to expand
     * @param listPlaceholderKey the key of the list placeholder
     * @param listValue the list of values to expand
     * @param placeholders the base placeholders map
     * @return list of expanded strings
     */
    @NotNull
    private static List<String> expandStringWithListPlaceholder(@NotNull String string, @NotNull String listPlaceholderKey,
                                                                  @NotNull List<?> listValue, @NotNull Map<String, Object> placeholders) {
        List<String> expandedStrings = new ArrayList<>();
        for (Object listElement : listValue) {
            Map<String, Object> tempPlaceholders = new HashMap<>(placeholders);
            tempPlaceholders.put(listPlaceholderKey, listElement);
            expandedStrings.add(parseStringValue(string, tempPlaceholders));
        }
        return expandedStrings;
    }

    /**
     * Processes a string value, either expanding it if it contains list placeholders
     * or just parsing it with regular placeholders.
     *
     * @param string the string to process
     * @param listPlaceholders the pre-filtered list placeholders
     * @param placeholders the full placeholders map
     * @param addToResult consumer to add results (allows flexibility for list vs single value)
     */
    private static void processStringValue(@NotNull String string, @NotNull Map<String, List<?>> listPlaceholders,
                                           @NotNull Map<String, Object> placeholders, @NotNull java.util.function.Consumer<Object> addToResult) {
        String listPlaceholderKey = findListPlaceholderInString(string, listPlaceholders);

        if (listPlaceholderKey != null) {
            List<?> listValue = listPlaceholders.get(listPlaceholderKey);
            List<String> expandedStrings = expandStringWithListPlaceholder(string, listPlaceholderKey, listValue, placeholders);
            expandedStrings.forEach(addToResult);
        } else {
            addToResult.accept(parseStringValue(string, placeholders));
        }
    }

    @NotNull
    private static Map<String, List<?>> extractListPlaceholders(@NotNull Map<String, Object> placeholders) {
        Map<String, List<?>> listPlaceholders = new HashMap<>();
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            if (entry.getValue() instanceof List<?> list) {
                listPlaceholders.put(entry.getKey(), list);
            }
        }
        return listPlaceholders;
    }

    @Nullable
    private static String findListPlaceholderInString(@NotNull String string, @NotNull Map<String, List<?>> listPlaceholders) {
        for (String key : listPlaceholders.keySet()) {
            String placeholderPattern = "%" + key + "%";
            if (string.contains(placeholderPattern)) {
                return key;
            }
        }
        return null;
    }
}