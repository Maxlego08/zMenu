package fr.maxlego08.menu;

import fr.maxlego08.menu.test.serialization.ObjectGraphDumper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every example menu, pattern, command, dialog and animation of the jar loads into the same objects as when the
 * golden files were written. Set the environment variable {@code ZMENU_GOLDEN_UPDATE=true} to write them again,
 * only when a change of the loaded objects is intended.
 */
class GoldenLoadingTest {

    private static final Path GOLDEN = Path.of("src/test/resources/golden");
    // Values that change from one start to another, not configuration
    private static final ObjectGraphDumper DUMPER = new ObjectGraphDumper(Set.of("uniqueId", "uuid", "id", "createdAt", "lastUpdate", "cache", "file"));

    private static ZMenuPlugin plugin;

    @BeforeAll
    static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    private static <T> Map<String, String> dump(Collection<T> values, Function<T, String> name) {
        Map<String, String> dumps = new LinkedHashMap<>();
        List<T> sorted = new ArrayList<>(values);
        sorted.sort(Comparator.comparing(name));
        for (T value : sorted) {
            String key = name.apply(value);
            for (int index = 2; dumps.containsKey(key); index++) key = name.apply(value) + " #" + index;
            dumps.put(key, DUMPER.dump(value));
        }
        return dumps;
    }

    private static void check(String kind, Map<String, String> dumps) throws IOException {
        Path file = GOLDEN.resolve(kind + ".txt");
        StringBuilder builder = new StringBuilder();
        dumps.forEach((name, dump) -> builder.append("### ").append(name).append('\n').append(dump).append('\n'));
        // The data folder is a new temporary folder on every run, and its separator depends on the system
        String root = plugin.getDataFolder().getAbsolutePath();
        String actual = builder.toString().replace(root, "<data>").replace(root.replace('\\', '/'), "<data>");
        actual = Pattern.compile("<data>[^\\s)]*").matcher(actual).replaceAll(match -> Matcher.quoteReplacement(match.group().replace('\\', '/')));
        if (Boolean.parseBoolean(System.getenv("ZMENU_GOLDEN_UPDATE")) || !Files.exists(file)) {
            Files.createDirectories(GOLDEN);
            Files.writeString(file, actual);
        }
        assertEquals(Files.readString(file).replace("\r\n", "\n"), actual, kind + " no longer load into the same objects");
    }

    @Test
    void inventories() throws IOException {
        check("inventories", dump(plugin.getInventoryManager().getInventories(), inventory -> inventory.getFile().getPath().replace('\\', '/')));
    }

    @Test
    void patterns() throws IOException {
        check("patterns", dump(plugin.getPatternManager().getPatterns(), pattern -> pattern.name()));
        check("action-patterns", dump(plugin.getPatternManager().getActionPatterns(), pattern -> pattern.name()));
    }

    @Test
    void commands() throws IOException {
        check("commands", dump(plugin.getCommandManager().getCommands(), command -> command.command() + " " + command.path()));
    }

    @Test
    void dialogs() throws IOException {
        check("dialogs", dump(plugin.getDialogManager().getDialogs(), dialog -> dialog.getFile().getPath().replace('\\', '/')));
    }

    @Test
    void animations() throws IOException {
        check("animations", dump(plugin.getInventoryManager().getAnimationManager().getAnimations(), animation -> animation.key().asString()));
    }
}
