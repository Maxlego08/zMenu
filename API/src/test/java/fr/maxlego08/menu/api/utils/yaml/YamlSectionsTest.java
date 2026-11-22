package fr.maxlego08.menu.api.utils.yaml;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every read zMenu's loaders make gives the same result through BoostedYAML and {@link YamlSections} as through
 * Bukkit's configuration API, on the same YAML.
 */
class YamlSectionsTest {

    private static final String YAML = """
            name: "Hello"
            number: 5
            quoted-number: "5"
            decimal: 2.5
            big: 12345678901
            negative: -3
            flag-yes: yes
            flag-off: Off
            flag-true: TRUE
            flag-y: y
            quoted-yes: "yes"
            empty:
            nothing: ~
            text-with-colon: "a: b"
            lore:
              - "a"
              - 3
              - true
              - 2.5
              - {type: message, message: hi}
              - [x, y]
              - yes
            actions:
              - type: message
                messages: [a, b]
              - "plain"
            "dotted.key": 1
            dotted:
              other: 2
            "dotted.section":
              inner: 3
            section:
              "0": zero
              1: one
              nested:
                deep: value
                list: []
                map: {}
            yes: boolean-key
            """;

    private static YamlConfiguration bukkit() throws InvalidConfigurationException {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(YAML);
        return configuration;
    }

    private static Object normalize(Object value) {
        if (value instanceof ConfigurationSection || value instanceof Section) return "<section>";
        if (value instanceof List<?> list) {
            List<Object> result = new ArrayList<>();
            list.forEach(element -> result.add(normalize(element)));
            return result;
        }
        if (value instanceof Map<?, ?> map) {
            Map<Object, Object> result = new LinkedHashMap<>();
            map.forEach((key, element) -> result.put(String.valueOf(key), normalize(element)));
            return result;
        }
        return value;
    }

    @Test
    void readsLikeBukkit() throws Exception {
        YamlConfiguration bukkit = bukkit();
        YamlDocument document = YamlSections.load(YAML);

        assertEquals(bukkit.getKeys(false), document.getRoutesAsStrings(false));
        assertEquals(bukkit.getKeys(true), document.getRoutesAsStrings(true));
        assertEquals(bukkit.getValues(true).keySet(), document.getStringRouteMappedValues(true).keySet());

        Set<String> paths = new LinkedHashSet<>(bukkit.getKeys(true));
        paths.addAll(List.of("missing", "section.missing", "name.inner", "section.nested.list.x"));
        List<String> differences = new ArrayList<>();
        for (String path : paths) {
            // On a section both give a text that only says it is one
            boolean isSection = bukkit.isConfigurationSection(path);
            check(differences, path, "getString", isSection ? "<section>" : bukkit.getString(path), isSection ? "<section>" : document.getString(path));
            check(differences, path, "getString(def)", isSection ? "<section>" : bukkit.getString(path, "def"), isSection ? "<section>" : document.getString(path, "def"));
            check(differences, path, "getInt", bukkit.getInt(path), document.getInt(path));
            check(differences, path, "getInt(def)", bukkit.getInt(path, 7), document.getInt(path, 7));
            check(differences, path, "getLong", bukkit.getLong(path), document.getLong(path));
            check(differences, path, "getDouble", bukkit.getDouble(path), document.getDouble(path));
            check(differences, path, "getBoolean", bukkit.getBoolean(path), document.getBoolean(path));
            check(differences, path, "getBoolean(def)", bukkit.getBoolean(path, true), document.getBoolean(path, true));
            check(differences, path, "get", normalize(bukkit.get(path)), normalize(document.get(path)));
            check(differences, path, "get(def)", normalize(bukkit.get(path, "def")), normalize(document.get(path, "def")));
            check(differences, path, "isConfigurationSection", bukkit.isConfigurationSection(path), document.isSection(path));
            check(differences, path, "isList", bukkit.isList(path), document.isList(path));
            check(differences, path, "isString", bukkit.isString(path), document.isString(path));
            check(differences, path, "isInt", bukkit.isInt(path), document.isInt(path));
            check(differences, path, "isBoolean", bukkit.isBoolean(path), document.isBoolean(path));
            check(differences, path, "contains", bukkit.contains(path), document.contains(path));
            check(differences, path, "getStringList", bukkit.getStringList(path), YamlSections.getStringList(document, path));
            check(differences, path, "getList", normalize(bukkit.getList(path)), normalize(YamlSections.getList(document, path)));
            check(differences, path, "getMapList", normalize(bukkit.getMapList(path)), normalize(document.getMapList(path)));
            check(differences, path, "getIntegerList", bukkit.getIntegerList(path), document.getIntList(path));
            check(differences, path, "getSection==null", bukkit.getConfigurationSection(path) == null, document.getSection(path) == null);
            ConfigurationSection bukkitSection = bukkit.getConfigurationSection(path);
            if (bukkitSection != null) {
                Section section = document.getSection(path);
                check(differences, path, "getCurrentPath", bukkitSection.getCurrentPath(), YamlSections.getCurrentPath(section));
                check(differences, path, "getName", bukkitSection.getName(), YamlSections.getName(section));
                check(differences, path, "getKeys", bukkitSection.getKeys(false), section.getRoutesAsStrings(false));
            }
        }
        check(differences, "", "getCurrentPath", bukkit.getCurrentPath(), YamlSections.getCurrentPath(document));
        assertEquals(List.of(), differences);
    }

    private static void check(List<String> differences, String path, String method, Object expected, Object actual) {
        if (!java.util.Objects.equals(expected, actual)) {
            differences.add(method + "(" + path + "): Bukkit " + expected + " / BoostedYAML " + actual);
        }
    }

    @Test
    void copiesBothWays() throws Exception {
        YamlConfiguration bukkit = bukkit();
        YamlDocument document = YamlSections.load(YAML);

        YamlConfiguration copy = YamlSections.toBukkit(document);
        assertEquals(normalize(bukkit.getValues(true)), normalize(copy.getValues(true)));

        YamlDocument back = YamlSections.toDocument(bukkit);
        assertEquals(normalize(document.getStringRouteMappedValues(true)), normalize(back.getStringRouteMappedValues(true)));

        assertEquals("section.nested", YamlSections.toBukkit(copy, document.getSection("section.nested")).getCurrentPath());
        assertEquals("section.nested", YamlSections.toSection(back, bukkit.getConfigurationSection("section.nested")).getRouteAsString());
    }

    @Test
    void writesLikeBukkit() throws IOException {
        YamlDocument document = YamlSections.empty();
        document.set("name", "yes");
        document.set("removed", null);
        document.createSection("section").set("value", 1);
        YamlSections.removeNulls(document);
        assertEquals("yes", YamlSections.load(document.dump()).get("name"));
        assertEquals(Set.of("name", "section"), document.getRoutesAsStrings(false));
    }
}
