package fr.maxlego08.menu;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.event.events.InventoryLoadEvent;
import fr.maxlego08.menu.api.loader.animation.slot.SlotAnimationLoader;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.reload.ReloadableRegistry;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.*;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryRegistryTest {

    private static ZMenuPlugin plugin;
    private static final List<String> loaded = new ArrayList<>();

    private InventoryManager inventoryManager;
    private final List<File> created = new ArrayList<>();

    @BeforeAll
    public static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onLoad(InventoryLoadEvent event) {
                loaded.add(event.getInventory().getFileName());
            }
        }, plugin);
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setup() {
        this.inventoryManager = plugin.getInventoryManager();
        loaded.clear();
    }

    @AfterEach
    public void cleanup() {
        this.created.forEach(File::delete);
        this.reload();
    }

    private void write(String name, String content) throws IOException {
        File file = new File(plugin.getDataFolder(), "inventories/" + name + ".yml");
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), content);
        this.created.add(file);
    }

    private static String menu(String title) {
        return "size: 9\nname: \"" + title + "\"\nitems: {}\n";
    }

    private void reload() {
        YamlFileCache.clearCache();
        this.inventoryManager.loadInventories();
    }

    private ReloadableRegistry<Inventory> registry() {
        return (ReloadableRegistry<Inventory>) this.inventoryManager.getRegistries().registry(Registries.INVENTORY);
    }

    @Test
    public void aBrokenFileKeepsItsPreviousVersionWhileTheOthersUpdate() throws IOException {
        this.write("registry_a", menu("A1"));
        this.write("registry_b", menu("B1"));
        this.reload();
        Inventory firstA = this.inventoryManager.findInventory("registry_a").orElseThrow();

        this.write("registry_a", "size: [\n");
        this.write("registry_b", menu("B2"));
        this.reload();

        assertSame(firstA, this.inventoryManager.findInventory("registry_a").orElseThrow(), "the previous version is kept");
        assertEquals("B2", this.inventoryManager.findInventory("registry_b").orElseThrow().getName());
    }

    @Test
    public void loadEventsFireOnlyForParsedFilesOnceTheSnapshotIsLive() throws IOException {
        this.write("registry_events", menu("E1"));
        this.write("registry_broken", menu("X1"));
        this.reload();
        assertTrue(loaded.contains("registry_events"));

        loaded.clear();
        this.write("registry_broken", "size: [\n");
        this.reload();

        assertTrue(loaded.contains("registry_events"));
        assertFalse(loaded.contains("registry_broken"), "a kept version is not loaded again");
    }

    @Test
    public void aFileWithASpaceInItsNameOpensByName() throws IOException {
        this.write("My Registry Shop", menu("Shop"));
        this.reload();

        assertTrue(this.inventoryManager.findInventory("My Registry Shop").isPresent());
        assertTrue(this.inventoryManager.getInventory(ZMenuKeys.fileKey(plugin.getName(), "my registry shop")).isPresent());
        assertTrue(this.inventoryManager.getInventoryKeys().contains(Key.key("zmenu", "my_registry_shop")));
    }

    @Test
    public void everyBuiltInSlotAnimationIsRegisteredByItsAnnotation() {
        KeyedRegistry<SlotAnimationLoader> animations = this.inventoryManager.getRegistries().registry(Registries.SLOT_ANIMATION);

        for (String name : List.of("sequential", "horizontal_snake", "vertical_snake", "spiral", "random", "from_center", "wave", "checkerboard", "columns", "matrix", "custom")) {
            assertTrue(animations.contains(ZMenuKeys.key(name)), name + " is missing, is its loader annotated with @AutoSlotAnimationLoader?");
            assertEquals("zMenu", animations.owner(ZMenuKeys.key(name)).orElseThrow());
        }
        assertSame(animations.getOrThrow(ZMenuKeys.key("sequential")), animations.getOrThrow(ZMenuKeys.key("rows")), "rows is an alias of sequential");
        assertEquals(11, animations.size());
    }

    @Test
    @SuppressWarnings("removal")
    public void deprecatedNameMethodsStillWorkThroughTheKeys() throws IOException {
        this.write("Registry_Legacy", menu("Legacy"));
        this.reload();
        Inventory inventory = this.inventoryManager.getInventory(Key.key("zmenu", "registry_legacy")).orElseThrow();

        assertEquals("Registry_Legacy", inventory.getFileName());
        assertSame(inventory, this.inventoryManager.getInventory("registry_legacy").orElseThrow());
        assertSame(inventory, this.inventoryManager.getInventory("Registry_Legacy").orElseThrow());
        assertSame(inventory, this.inventoryManager.getInventory(plugin, "registry_legacy").orElseThrow());
        assertSame(inventory, this.inventoryManager.getInventory("ZMENU", "Registry_Legacy").orElseThrow());
        assertSame(inventory, this.inventoryManager.findInventory("zMenu:Registry_Legacy").orElseThrow());
        assertTrue(this.inventoryManager.getInventoryNames().contains("zmenu:registry_legacy"));

        assertTrue(this.inventoryManager.deleteInventory("Registry_Legacy"));
        assertFalse(this.inventoryManager.getInventory(inventory.key()).isPresent());
        assertFalse(this.inventoryManager.deleteInventory(inventory.key()), "nothing left to delete");
    }

    @Test
    public void singleReloadAndDeleteGoThroughTheRegistry() throws IOException {
        this.write("registry_single", menu("S1"));
        this.reload();
        long generation = this.registry().generation();
        Inventory first = this.inventoryManager.findInventory("registry_single").orElseThrow();

        this.write("registry_single", menu("S2"));
        YamlFileCache.clearCache();
        this.inventoryManager.reloadInventory(first);

        assertEquals("S2", this.inventoryManager.findInventory("registry_single").orElseThrow().getName());
        assertEquals(generation + 1, this.registry().generation());

        this.write("registry_single", "size: [\n");
        YamlFileCache.clearCache();
        Inventory second = this.inventoryManager.findInventory("registry_single").orElseThrow();
        this.inventoryManager.reloadInventory(second);
        assertSame(second, this.inventoryManager.findInventory("registry_single").orElseThrow(), "a broken single reload keeps the menu");

        this.inventoryManager.deleteInventory(second);
        assertFalse(this.inventoryManager.findInventory("registry_single").isPresent());
        assertTrue(this.inventoryManager.getInventories(plugin).stream().noneMatch(inventory -> inventory.getFileName().equals("registry_single")));
    }
}
