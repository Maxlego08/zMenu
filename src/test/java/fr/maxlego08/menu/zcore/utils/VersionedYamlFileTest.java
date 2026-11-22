package fr.maxlego08.menu.zcore.utils;

import dev.dejvokep.boostedyaml.YamlDocument;
import fr.maxlego08.menu.api.utils.yaml.LegacyBooleanSchema;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionedYamlFileTest {

    private static final String DEFAULTS = """
            # Header of the file
            #

            # Version of this file
            config-version: 1

            # Enables the thing
            enable-thing: true
            # Name of the thing
            name: "zMenu"
            answer: "yes"
            free: {}
            """;

    @TempDir
    Path folder;

    private static Supplier<InputStream> defaults() {
        return () -> new ByteArrayInputStream(DEFAULTS.getBytes(StandardCharsets.UTF_8));
    }

    private YamlDocument open(File file) throws IOException {
        return VersionedYamlFile.open(file, defaults(), Logger.getLogger("test"), Set.of("free"), false, Map.of());
    }

    @Test
    void missingFileIsACopyOfTheDefaults() throws IOException {
        File file = this.folder.resolve("config.yml").toFile();
        this.open(file);
        assertEquals(DEFAULTS, Files.readString(file.toPath()));
    }

    @Test
    void legacyFileIsBackedUpUpdatedAndConverted() throws IOException {
        File file = this.folder.resolve("config.yml").toFile();
        String legacy = "# Header of the file\n#\n\n# My own comment\nenable-thing: no\nobsolete: 3\nfree:\n  custom: 1\n";
        Files.writeString(file.toPath(), legacy);

        YamlDocument document = this.open(file);

        assertEquals(legacy, Files.readString(this.folder.resolve("config.yml.bak-v0")));
        String content = Files.readString(file.toPath());
        assertEquals(1, document.getInt("config-version"));
        assertFalse(document.getBoolean("enable-thing"));
        assertTrue(content.contains("enable-thing: false"), content);
        assertTrue(content.contains("# Name of the thing"), content);
        assertTrue(content.contains("config-version: 1"), content);
        assertTrue(content.contains("name: zMenu") || content.contains("name: \"zMenu\""), content);
        assertFalse(content.contains("obsolete"), content);
        assertEquals(1, document.getInt("free.custom"));
        assertEquals("yes", document.getString("answer"));
        assertEquals(1, content.split("# Header of the file", -1).length - 1, content);
        assertTrue(content.contains("# My own comment\nenable-thing: false"), content);
        assertEquals("yes", YamlDocument.create(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), LegacyBooleanSchema.loaderSettings()).get("answer"));

        // Configuration#save goes through Bukkit, which must keep the version and the comments
        YamlConfiguration bukkit = YamlConfiguration.loadConfiguration(file);
        assertEquals("yes", bukkit.getString("answer"));
        bukkit.set("enable-thing", true);
        bukkit.save(file);
        String saved = Files.readString(file.toPath());
        assertTrue(saved.contains("config-version: 1") && saved.contains("# Name of the thing") && saved.contains("# Header of the file"), saved);
    }

    @Test
    void sameVersionIsLeftUntouched() throws IOException {
        File file = this.folder.resolve("config.yml").toFile();
        String current = "config-version: 1\nenable-thing: off\nobsolete: 3\n";
        Files.writeString(file.toPath(), current);

        YamlDocument document = this.open(file);

        assertEquals(current, Files.readString(file.toPath()));
        assertFalse(this.folder.resolve("config.yml.bak-v1").toFile().exists());
        assertFalse(document.getBoolean("enable-thing"));
    }

    @Test
    void legacyBooleans() throws IOException {
        assertEquals(false, YamlDocument.create(new ByteArrayInputStream("a: off".getBytes(StandardCharsets.UTF_8)), LegacyBooleanSchema.loaderSettings()).get("a"));
        assertTrue(LegacyBooleanSchema.parse("Yes"));
        assertFalse(LegacyBooleanSchema.parse("OFF"));
        assertNull(LegacyBooleanSchema.parse("y"));
        assertNull(LegacyBooleanSchema.parse("yEs"));
    }
}
