package fr.maxlego08.menu.api.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Placeholders {

    private final Map<String, String> placeholders = new HashMap<>();

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
            string = string.replace("%" + entry.getKey() + "%", entry.getValue());
            string = string.replace("%upper_" + entry.getKey() + "%", entry.getValue().toUpperCase());
            string = string.replace("%lower_" + entry.getKey() + "%", entry.getValue().toLowerCase());
            String capitalize = entry.getValue();
            if (capitalize.length() > 1) {
                capitalize = capitalize.substring(0, 1).toUpperCase() + capitalize.substring(1);
            }
            string = string.replace("%capitalize_" + entry.getKey() + "%", entry.getValue().toLowerCase());
        }
        return string;
    }

}
