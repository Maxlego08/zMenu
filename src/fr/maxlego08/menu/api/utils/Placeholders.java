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
            string = string.replace(entry.getKey(), entry.getValue());
        }
        return string;
    }

}
