package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Placeholders {

    private final Map<String, String> placeholders;

    public Placeholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    public Placeholders() {
        this(new HashMap<>());
    }

    public void register(String key, String value) {
        this.placeholders.put(key, value);
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public List<String> parse(List<String> strings) {
        return strings.stream().map(this::parse).collect(Collectors.toList());
    }

    public String parse(String string) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            try {

                string = string.replace("%" + entry.getKey() + "%", entry.getValue());
                string = string.replace("%upper_" + entry.getKey() + "%", entry.getValue().toUpperCase());
                string = string.replace("%lower_" + entry.getKey() + "%", entry.getValue().toLowerCase());
                String capitalize = entry.getValue();
                if (capitalize.length() > 1) {
                    capitalize = capitalize.substring(0, 1).toUpperCase() + capitalize.substring(1);
                }
                string = string.replace("%capitalize_" + entry.getKey() + "%", capitalize);
            } catch (Exception exception) {
                exception.printStackTrace();
                Logger.info("Error with placeholder key " + entry.getKey() + " !", Logger.LogType.ERROR);
            }
        }
        return string;
    }

    public String parse(String string, String key, String value) {
        try {

            string = string.replace("%" + key + "%", value);
            string = string.replace("%upper_" + key + "%", value.toUpperCase());
            string = string.replace("%lower_" + key + "%", value.toLowerCase());
            String capitalize = value;
            if (capitalize.length() > 1) {
                capitalize = capitalize.substring(0, 1).toUpperCase() + capitalize.substring(1);
            }
            string = string.replace("%capitalize_" + key + "%", capitalize);

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

}
