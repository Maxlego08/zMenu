package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Placeholders {

    private final Map<String, String> placeholders;

    public Placeholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public Placeholders() {
        this(new HashMap<>());
    }

    /**
     * Registers a placeholder with the given key and value.
     *
     * @param key   the key of the placeholder.
     * @param value the value of the placeholder.
     */
    public void register(@Nullable String key,@Nullable String value) {
        this.placeholders.put(key, value);
    }

    /**
     * Gets the map of placeholders.
     *
     * @return the map of placeholders.
     */
    @NotNull
    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    /**
     * Replace all placeholders in each string of the given list by their respective values.
     *
     * @param strings the list of strings to parse
     * @return the list of parsed strings
     */
    @NotNull
    public List<String> parse(@NotNull List<String> strings) {
        List<String> parsed = new ArrayList<>(strings.size());
        for (String string : strings) {
            parsed.add(this.parse(string));
        }
        return parsed;
    }

    /**
     * Replace all placeholders in the given string by their respective values.
     *
     * @param string the string to parse
     * @return the parsed string
     */
    @NotNull
    public String parse(@NotNull String string) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = parse(string, entry.getKey(), entry.getValue());
        }
        return string;
    }

    /**
     * Replace all placeholders in the given string by their respective values.
     * The method support the following placeholders:
     * - %key%: the value of the key
     * - %upper_key%: the value of the key in upper case
     * - %lower_key%: the value of the key in lower case
     * - %capitalize_key%: the value of the key with the first letter capitalized
     * - %add_one_key%: the value of the key incremented by one (if the value is a number)
     * - %remove_one_key%: the value of the key decremented by one (if the value is a number)
     *
     * @param string the string to parse
     * @param key    the key of the placeholder
     * @param value  the value of the placeholder
     * @return the parsed string
     */
    @NotNull
    public String parse(@NotNull String string,@NotNull String key,@NotNull String value) {
        try {

            if (string.contains("%" + key + "%")) {
                string = string.replace("%" + key + "%", value);
            }

            if (string.contains("%upper_" + key + "%")) {
                string = string.replace("%upper_" + key + "%", value.toUpperCase());
            }

            if (string.contains("%lower_" + key + "%")) {
                string = string.replace("%lower_" + key + "%", value.toLowerCase());
            }

            if (string.contains("%capitalize_" + key + "%")) {
                String capitalize = value.isEmpty()
                        ? value
                        : value.substring(0, 1).toUpperCase() + value.substring(1);
                string = string.replace("%capitalize_" + key + "%", capitalize);
            }

            if (string.contains("%add_one_" + key + "%")) {
                try {
                    string = string.replace("%add_one_" + key + "%", String.valueOf(Integer.parseInt(value) + 1));
                } catch (NumberFormatException ignored) {
                }
            }

            if (string.contains("%remove_one_" + key + "%")) {
                try {
                    string = string.replace("%remove_one_" + key + "%", String.valueOf(Integer.parseInt(value) - 1));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Logger.info("Error with placeholder key " + key + " !", Logger.LogType.ERROR);
        }
        return string;
    }

    public void merge(@NotNull Placeholders placeholders) {
        this.placeholders.putAll(placeholders.getPlaceholders());
    }
}
