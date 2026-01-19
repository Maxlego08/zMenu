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
        Placeholders placeholderParser = new Placeholders();

        for (String key : source.getKeys(false)) {
            String fullPath = currentPath.isEmpty() ? key : currentPath + "." + key;
            Object value = source.get(key);

            switch (value) {
                case ConfigurationSection section -> parseSection(section, destination.createSection(key), fullPath, placeholders);
                case List<?> list -> destination.set(key, parseList(list, placeholders, placeholderParser));
                case String string -> destination.set(key, parseStringValue(string, placeholders, placeholderParser));
                case null, default -> destination.set(key, value);
            }
        }
    }

    @NotNull
    private static List<Object> parseList(@NotNull List<?> list, @NotNull Map<String, Object> placeholders, @NotNull Placeholders placeholderParser) {
        List<Object> result = new ArrayList<>();

        for (Object item : list) {
            switch (item) {
                case ConfigurationSection section -> {
                    YamlConfiguration temp = new YamlConfiguration();
                    parseSection(section, temp, "", placeholders);
                    result.add(temp.getValues(true));
                }
                case List<?> nestedList -> result.add(parseList(nestedList, placeholders, placeholderParser));
                case String string -> {
                    String listPlaceholderKey = findListPlaceholderInString(string, placeholders);

                    if (listPlaceholderKey != null) {
                        List<?> listValue = (List<?>) placeholders.get(listPlaceholderKey);
                        for (Object listElement : listValue) {
                            Map<String, Object> tempPlaceholders = new HashMap<>(placeholders);
                            tempPlaceholders.put(listPlaceholderKey, listElement);

                            String expandedLine = parseStringValue(string, tempPlaceholders, placeholderParser);
                            result.add(expandedLine);
                        }
                    } else {
                        String parsedValue = parseStringValue(string, placeholders, placeholderParser);
                        result.add(parsedValue);
                    }
                }
                case null, default -> result.add(item);
            }
        }

        return result;
    }


    @NotNull
    private static String parseStringValue(@NotNull String value, @NotNull Map<String, Object> placeholders, @NotNull Placeholders placeholderParser) {
        String result = value;

        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            Object placeholderValue = entry.getValue();
            String replacementValue = placeholderValue != null ? placeholderValue.toString() : "";
            result = placeholderParser.parse(result, key, replacementValue);
        }

        return result;
    }

    @Nullable
    private static String findListPlaceholderInString(@NotNull String string, @NotNull Map<String, Object> placeholders) {
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            if (entry.getValue() instanceof List<?>) {
                String placeholderPattern = "%" + entry.getKey() + "%";
                if (string.contains(placeholderPattern)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}