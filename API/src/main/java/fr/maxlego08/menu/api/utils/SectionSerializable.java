package fr.maxlego08.menu.api.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public interface SectionSerializable {

    /**
     * Writes this object's keys into a section.
     *
     * @param section The section to write into.
     * @throws UnsupportedOperationException If this object cannot be serialized.
     * @deprecated use {@link #serialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void serialize(@NotNull ConfigurationSection section);

    /**
     * Writes this object's keys into a section. zMenu calls this method, override it.
     * By default it writes through the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     *
     * @param section The section to write into.
     * @throws UnsupportedOperationException If this object cannot be serialized.
     */
    default void serialize(@NotNull Section section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }

    /**
     * Serializes this object into nested maps, for places where the configuration holds it inside a
     * list or a map instead of its own section (an item inside a requirement, for example).
     *
     * @return This object's keys, with nested sections as maps.
     */
    @NotNull
    default Map<String, Object> serializeToMap() {
        YamlDocument section = YamlSections.empty();
        this.serialize(section);
        return toMap(section);
    }

    /**
     * Converts a configuration section into nested maps, keeping the key order.
     *
     * @param section The section to convert.
     * @return The section's values, with nested sections as maps.
     * @deprecated use {@link #toMap(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @NotNull
    static Map<String, Object> toMap(@NotNull ConfigurationSection section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            map.put(key, value instanceof ConfigurationSection child ? toMap(child) : value);
        }
        return map;
    }

    /**
     * Converts a section into nested maps, keeping the key order. Null values are left out, as Bukkit's
     * {@code set} never keeps them.
     *
     * @param section The section to convert.
     * @return The section's values, with nested sections as maps.
     */
    @NotNull
    static Map<String, Object> toMap(@NotNull Section section) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (String key : section.getRoutesAsStrings(false)) {
            Object value = section.get(key);
            if (value != null) map.put(key, value instanceof Section child ? toMap(child) : value);
        }
        return map;
    }
}
