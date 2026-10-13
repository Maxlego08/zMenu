package fr.maxlego08.menu.serialization;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.loader.PermissibleLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.loader.actions.PlayerCommandAsOPLoader;
import fr.maxlego08.menu.test.serialization.ObjectGraphComparator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every action and permissible type must survive serialize then load.
 * <p>
 * {@code serialization/actions.yml} and {@code serialization/permissibles.yml} hold one entry per
 * type. The coverage tests fail when a registered loader has no entry there, so a new type cannot
 * be added without a serializer.
 */
class ActionSerializationTest {

    private static ZMenuPlugin plugin;
    private static ButtonManager buttonManager;
    private static File actionsFile;
    private static File permissiblesFile;

    @BeforeAll
    static void init() throws Exception {
        MockBukkit.mock().addSimpleWorld("world");
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        buttonManager = plugin.getButtonManager();
        buttonManager.registerAction(new PlayerCommandAsOPLoader());
        actionsFile = new File(Objects.requireNonNull(ActionSerializationTest.class.getResource("/serialization/actions.yml")).toURI());
        permissiblesFile = new File(Objects.requireNonNull(ActionSerializationTest.class.getResource("/serialization/permissibles.yml")).toURI());
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> entries(File file, String key) {
        return (List<Map<String, Object>>) YamlConfiguration.loadConfiguration(file).getList(key);
    }

    private static ObjectGraphComparator comparator() {
        return new ObjectGraphComparator().ignoreField("Action.type");
    }

    @Test
    void everyActionSurvivesARoundTrip() {
        List<String> failures = new ArrayList<>();
        for (Map<String, Object> entry : entries(actionsFile, "actions")) {
            if (buttonManager.getActionLoader(String.valueOf(entry.get("type"))).isEmpty()) {
                System.out.println("Skipped action, not registered here: " + entry.get("type"));
                continue;
            }
            List<Action> loaded = buttonManager.loadActions(List.of(entry), "actions", actionsFile);
            if (loaded.size() != 1) {
                failures.add(entry.get("type") + ": the fixture entry does not load");
                continue;
            }
            Action original = loaded.getFirst();
            try {
                Map<String, Object> serialized = original.serialize();
                List<Action> reloaded = buttonManager.loadActions(List.of(serialized), "actions", actionsFile);
                if (reloaded.size() != 1) {
                    failures.add(entry.get("type") + ": its serialized form " + serialized + " does not load");
                    continue;
                }
                comparator().compare(original, reloaded.getFirst()).forEach(difference -> failures.add(entry.get("type") + ": " + difference + " (serialized as " + serialized + ")"));
            } catch (RuntimeException exception) {
                failures.add(entry.get("type") + ": " + exception);
            }
        }
        assertEquals(List.of(), failures, String.join("\n", failures));
    }

    @Test
    void everyPermissibleSurvivesARoundTrip() {
        List<String> failures = new ArrayList<>();
        for (Map<String, Object> entry : entries(permissiblesFile, "requirements")) {
            if (buttonManager.getPermission(String.valueOf(entry.get("type"))).isEmpty()) {
                System.out.println("Skipped permissible, not registered here: " + entry.get("type"));
                continue;
            }
            List<Permissible> loaded = buttonManager.loadPermissible(List.of(entry), "requirements", permissiblesFile);
            if (loaded.size() != 1) {
                failures.add(entry.get("type") + ": the fixture entry does not load");
                continue;
            }
            Permissible original = loaded.getFirst();
            try {
                Map<String, Object> serialized = original.serialize();
                List<Permissible> reloaded = buttonManager.loadPermissible(List.of(serialized), "requirements", permissiblesFile);
                if (reloaded.size() != 1) {
                    failures.add(entry.get("type") + ": its serialized form " + serialized + " does not load");
                    continue;
                }
                comparator().compare(original, reloaded.getFirst()).forEach(difference -> failures.add(entry.get("type") + ": " + difference + " (serialized as " + serialized + ")"));
            } catch (RuntimeException exception) {
                failures.add(entry.get("type") + ": " + exception);
            }
        }
        assertEquals(List.of(), failures, String.join("\n", failures));
    }

    @Test
    void everyRegisteredActionHasAFixture() throws ReflectiveOperationException {
        Set<ActionLoader> covered = identitySet();
        for (Map<String, Object> entry : entries(actionsFile, "actions")) {
            buttonManager.getActionLoader(String.valueOf(entry.get("type"))).ifPresent(covered::add);
        }
        List<String> missing = new ArrayList<>();
        for (ActionLoader loader : registered("actionsLoader", ActionLoader.class)) {
            if (!covered.contains(loader)) missing.add(loader.getClass().getSimpleName() + " " + loader.getKeys());
        }
        assertEquals(List.of(), missing, "add an entry for these actions to serialization/actions.yml");
    }

    @Test
    void everyRegisteredPermissibleHasAFixture() throws ReflectiveOperationException {
        Set<PermissibleLoader> covered = identitySet();
        for (Map<String, Object> entry : entries(permissiblesFile, "requirements")) {
            buttonManager.getPermission(String.valueOf(entry.get("type"))).ifPresent(covered::add);
        }
        List<String> missing = new ArrayList<>();
        for (PermissibleLoader loader : registered("permissibles", PermissibleLoader.class)) {
            if (!covered.contains(loader)) missing.add(loader.getClass().getSimpleName() + " " + loader.getKey());
        }
        assertEquals(List.of(), missing, "add an entry for these permissibles to serialization/permissibles.yml");
    }

    private static <T> Collection<T> registered(String fieldName, Class<T> type) throws ReflectiveOperationException {
        Field field = buttonManager.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Set<T> loaders = identitySet();
        for (Object loader : ((Map<?, ?>) field.get(buttonManager)).values()) {
            loaders.add(type.cast(loader));
        }
        return loaders;
    }

    private static <T> Set<T> identitySet() {
        return java.util.Collections.newSetFromMap(new IdentityHashMap<>());
    }
}
