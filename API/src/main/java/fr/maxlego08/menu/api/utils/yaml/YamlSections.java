package fr.maxlego08.menu.api.utils.yaml;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.Block;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Documents of zMenu read with BoostedYAML, and the few reads where BoostedYAML and Bukkit's configuration API
 * do not give the same result.
 * <p>
 * Everything else reads the same through {@link Section}: {@code getString}, {@code getInt}, {@code getBoolean},
 * {@code getDouble}, {@code getLong}, {@code get}, {@code getSection}, {@code isSection}, {@code isList},
 * {@code contains}, {@code getMapList}, {@code getRoutesAsStrings} for {@code getKeys} and {@code getStringRouteMappedValues} for
 * {@code getValues}.
 * <p>
 * BoostedYAML is loaded by zMenu's plugin loader: an addon that uses these types must also declare it.
 */
public final class YamlSections {

    /**
     * Getters read the document only, like a Bukkit configuration without defaults.
     */
    public static final GeneralSettings GENERAL_SETTINGS = GeneralSettings.builder().setUseDefaults(false).build();

    private YamlSections() {
    }

    /**
     * Reads a file like {@code YamlConfiguration#loadConfiguration}: a missing or invalid file gives an empty
     * document, an invalid one is logged.
     */
    public static @NotNull YamlDocument load(@NotNull File file) {
        try {
            return normalize(YamlDocument.create(file, GENERAL_SETTINGS, LegacyBooleanSchema.loaderSettings(), LegacyBooleanSchema.dumperSettings()));
        } catch (Exception exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, exception);
            return empty();
        }
    }

    public static @NotNull YamlDocument load(@NotNull String content) throws IOException {
        return load(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static @NotNull YamlDocument load(@NotNull InputStream inputStream) throws IOException {
        return normalize(YamlDocument.create(inputStream, GENERAL_SETTINGS, LegacyBooleanSchema.loaderSettings(), LegacyBooleanSchema.dumperSettings()));
    }

    public static @NotNull YamlDocument empty() {
        try {
            return YamlDocument.create(InputStream.nullInputStream(), GENERAL_SETTINGS, LegacyBooleanSchema.loaderSettings(), LegacyBooleanSchema.dumperSettings());
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Bukkit builds its sections key by key: a key with dots is split ({@code "a.b": 1} is {@code b} inside
     * {@code a}), a later section replaces an earlier one, and a key without value is left out.
     */
    private static YamlDocument normalize(YamlDocument document) {
        normalize((Section) document);
        return document;
    }

    private static void normalize(Section section) {
        Map<Object, Block<?>> values = section.getStoredValue();
        boolean rebuild = values.entrySet().stream().anyMatch(entry -> String.valueOf(entry.getKey()).indexOf('.') >= 0
                || !(entry.getValue() instanceof Section) && entry.getValue().getStoredValue() == null);
        if (rebuild) {
            List<Map.Entry<Object, Block<?>>> entries = new ArrayList<>(values.entrySet());
            values.clear();
            for (Map.Entry<Object, Block<?>> entry : entries) {
                String name = String.valueOf(entry.getKey());
                section.remove(name);
                if (entry.getValue() instanceof Section || entry.getValue().getStoredValue() != null) {
                    section.set(name, entry.getValue());
                }
            }
        }
        for (Block<?> block : new ArrayList<>(values.values())) {
            if (block instanceof Section child) normalize(child);
        }
    }

    /**
     * A read-only Bukkit copy of the section, for the methods that still take Bukkit's types.
     */
    public static @NotNull YamlConfiguration toBukkit(@NotNull Section section) {
        YamlConfiguration configuration = new YamlConfiguration();
        copy(section, configuration);
        return configuration;
    }

    /**
     * The section at the same place in a Bukkit copy of its document, see {@link #toBukkit(Section)}.
     */
    public static @Nullable ConfigurationSection toBukkit(@NotNull YamlConfiguration copy, @Nullable Section section) {
        if (section == null) return null;
        return section.isRoot() ? copy : copy.getConfigurationSection(section.getRouteAsString());
    }

    /**
     * A BoostedYAML copy of a Bukkit section, for the methods that still receive Bukkit's types.
     */
    public static @NotNull YamlDocument toDocument(@NotNull ConfigurationSection section) {
        YamlDocument document = empty();
        copy(section, document);
        return document;
    }

    /**
     * The section at the same place in a copy of its Bukkit document, see {@link #toDocument(ConfigurationSection)}.
     */
    public static @Nullable Section toSection(@NotNull YamlDocument copy, @Nullable ConfigurationSection section) {
        if (section == null) return null;
        String path = section.getCurrentPath();
        return path == null || path.isEmpty() ? copy : copy.getSection(path);
    }

    /**
     * Copies the values of a section into a Bukkit section, null values left out as Bukkit's {@code set} does.
     */
    public static void copy(@NotNull Section from, @NotNull ConfigurationSection to) {
        for (Map.Entry<String, Object> entry : from.getStringRouteMappedValues(false).entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Section section) {
                copy(section, to.createSection(entry.getKey()));
            } else if (value != null) {
                to.set(entry.getKey(), value);
            }
        }
    }

    public static void copy(@NotNull ConfigurationSection from, @NotNull Section to) {
        for (String key : from.getKeys(false)) {
            Object value = from.get(key);
            if (value instanceof ConfigurationSection section) {
                copy(section, to.createSection(key));
            } else if (value != null) {
                to.set(key, value);
            }
        }
    }

    /**
     * Runs a writer of Bukkit's types on a copy of the section, then copies what it wrote into the section.
     */
    public static void write(@NotNull Section section, @NotNull Consumer<ConfigurationSection> writer) {
        YamlConfiguration copy = toBukkit(section);
        writer.accept(copy);
        copy(copy, section);
    }

    /**
     * Runs a writer of BoostedYAML's types on a copy of the Bukkit section, then copies what it wrote into it.
     */
    public static void write(@NotNull ConfigurationSection section, @NotNull Consumer<Section> writer) {
        YamlDocument copy = toDocument(section);
        writer.accept(copy);
        copy(copy, section);
    }

    /**
     * Removes the null values, which Bukkit's {@code set} never keeps, before a document is written.
     */
    public static void removeNulls(@NotNull Section section) {
        for (Object key : new ArrayList<>(section.getKeys())) {
            Block<?> block = section.getStoredValue().get(key);
            if (block instanceof Section child) removeNulls(child);
            else if (block.getStoredValue() == null) section.getStoredValue().remove(key);
        }
    }

    /**
     * {@code ConfigurationSection#getStringList}: only the strings and the primitive values of the list, an empty
     * list when there is none. BoostedYAML also turns the maps of the list into strings.
     */
    public static @NotNull List<String> getStringList(@NotNull Section section, @NotNull String path) {
        List<?> list = getList(section, path);
        List<String> result = new ArrayList<>();
        if (list == null) return result;
        for (Object object : list) {
            if (object instanceof String || object instanceof Number || object instanceof Boolean || object instanceof Character) {
                result.add(String.valueOf(object));
            }
        }
        return result;
    }

    /**
     * {@code ConfigurationSection#getList}: {@code null} when the value is not a list. BoostedYAML gives an empty list.
     */
    public static @Nullable List<?> getList(@NotNull Section section, @NotNull String path) {
        return getList(section, path, null);
    }

    public static @Nullable List<?> getList(@NotNull Section section, @NotNull String path, @Nullable List<?> def) {
        Object value = section.get(path, def);
        return value instanceof List<?> list ? list : def;
    }

    /**
     * {@code ConfigurationSection#getCurrentPath}: empty for the document itself. BoostedYAML gives {@code null}.
     */
    public static @NotNull String getCurrentPath(@NotNull Section section) {
        return section.isRoot() ? "" : section.getRouteAsString();
    }

    /**
     * {@code ConfigurationSection#getName}: empty for the document itself.
     */
    public static @NotNull String getName(@NotNull Section section) {
        return section.isRoot() ? "" : section.getNameAsString();
    }
}
