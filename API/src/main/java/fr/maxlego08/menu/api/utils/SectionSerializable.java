package fr.maxlego08.menu.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public interface SectionSerializable {

    /**
     * Writes this object's keys into a section.
     *
     * @param section The section to write into.
     * @throws UnsupportedOperationException If this object cannot be serialized.
     */
    void serialize(@NotNull ConfigurationSection section);

    /**
     * Serializes this object into nested maps, for places where the configuration holds it inside a
     * list or a map instead of its own section (an item inside a requirement, for example).
     *
     * @return This object's keys, with nested sections as maps.
     */
    @NotNull
    default Map<String, Object> serializeToMap() {
        MemoryConfiguration section = new MemoryConfiguration();
        this.serialize(section);
        return toMap(section);
    }

    /**
     * Converts a configuration section into nested maps, keeping the key order.
     *
     * @param section The section to convert.
     * @return The section's values, with nested sections as maps.
     */
    @NotNull
    static Map<String, Object> toMap(@NotNull ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            map.put(key, value instanceof ConfigurationSection child ? toMap(child) : value);
        }
        return map;
    }
}
