package fr.maxlego08.menu.serialization;

import fr.maxlego08.menu.ZComponentsManager;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import fr.maxlego08.menu.test.serialization.ObjectGraphComparator;
import fr.maxlego08.menu.test.serialization.SerializationRoundTrip;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every item component must survive serialize then load.
 * <p>
 * {@code serialization/components.yml} holds one item per component; the coverage test fails when
 * a registered component has no item there, so a new component cannot be added without a serializer.
 */
class ItemComponentSerializationTest {

    private static ZMenuPlugin plugin;
    private static File componentsFile;
    private static YamlConfiguration components;

    @TempDir
    Path directory;

    @BeforeAll
    static void init() throws Exception {
        MockBukkit.mock().addSimpleWorld("world");
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        componentsFile = new File(Objects.requireNonNull(ItemComponentSerializationTest.class.getResource("/serialization/components.yml")).toURI());
        components = YamlConfiguration.loadConfiguration(componentsFile);
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void everyComponentSurvivesARoundTrip() throws Exception {
        MenuItemStackLoader loader = new MenuItemStackLoader(plugin.getInventoryManager());
        ConfigurationSection items = Objects.requireNonNull(components.getConfigurationSection("items"));
        List<String> failures = new ArrayList<>();

        for (String name : items.getKeys(false)) {
            String path = "items." + name + ".";
            MenuItemStack original;
            try {
                original = loader.load(components, path, componentsFile);
            } catch (LinkageError error) {
                System.out.println("Skipped " + name + ", it cannot be built on this server version: " + error);
                continue;
            }
            int expected = Objects.requireNonNull(items.getConfigurationSection(name + ".components")).getKeys(false).size();
            if (original.getItemComponents().size() != expected) {
                failures.add(name + ": " + original.getItemComponents().size() + " of its " + expected + " component(s) loaded");
                continue;
            }
            try {
                File file = this.directory.resolve(name + ".yml").toFile();
                YamlConfiguration reread = SerializationRoundTrip.writeAndReread(original, "item", file);
                MenuItemStack reloaded = loader.load(reread, "item.", file);
                new ObjectGraphComparator().ignoreField("ZMenuItemStack.filePath").ignoreField("ZMenuItemStack.path")
                        .compare(original, reloaded)
                        .forEach(difference -> failures.add(name + ": " + difference + "\n  serialized as: " + reread.saveToString().replace("\n", "\n  ")));
            } catch (RuntimeException | LinkageError exception) {
                failures.add(name + ": " + exception);
            }
        }
        assertEquals(List.of(), failures, String.join("\n", failures));
    }

    @Test
    void everyRegisteredComponentHasAFixture() {
        ConfigurationSection items = Objects.requireNonNull(components.getConfigurationSection("items"));
        Set<ItemComponentLoader> covered = Collections.newSetFromMap(new IdentityHashMap<>());
        for (String name : items.getKeys(false)) {
            ConfigurationSection itemComponents = Objects.requireNonNull(items.getConfigurationSection(name + ".components"));
            for (String componentName : itemComponents.getKeys(false)) {
                plugin.getComponentsManager().getLoader(componentName).ifPresent(covered::add);
            }
        }

        ZComponentsManager componentsManager = (ZComponentsManager) plugin.getComponentsManager();
        Set<ItemComponentLoader> registered = Collections.newSetFromMap(new IdentityHashMap<>());
        for (String componentName : componentsManager.getRegisteredComponentNames()) {
            componentsManager.getLoader(componentName).ifPresent(registered::add);
        }

        List<String> missing = new ArrayList<>();
        for (ItemComponentLoader componentLoader : registered) {
            if (!covered.contains(componentLoader)) missing.add(componentLoader.getComponentName());
        }
        Collections.sort(missing);
        assertEquals(List.of(), missing, "add an item using these components to serialization/components.yml");
    }
}
