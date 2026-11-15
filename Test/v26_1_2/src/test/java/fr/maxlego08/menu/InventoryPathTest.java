package fr.maxlego08.menu;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.InventoryButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.command.NameSuggestions;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.maxlego08.menu.requirement.actions.InventoryAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code enable-inventory-paths}: off, inventories are named by their file name as they always
 * were; on, an inventory of zMenu is named by its path inside the inventories folder and an
 * inventory of another plugin needs {@code plugin:name}.
 */
public class InventoryPathTest {

    private static final String SHARED_NAME = "is provided by several plugins";
    private static final String SAME_NAME = "has the same name as another inventory";

    private static ServerMock server;
    private static ZMenuPlugin plugin;
    private static Plugin addon;
    private static final List<String> logs = new ArrayList<>();
    private static PlayerOpenInventoryEvent opened;

    private ZInventoryManager inventoryManager;
    private final List<File> created = new ArrayList<>();

    @BeforeAll
    public static void init() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        addon = MockBukkit.createMockPlugin("zEssentials");
        // MockBukkit cannot show a menu: the opening is recorded then cancelled
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onOpen(PlayerOpenInventoryEvent event) {
                opened = event;
                event.setCancelled(true);
            }
        }, plugin);
        // A logger registers itself when it is created: this one keeps what zMenu logs
        new Logger("") {
            @Override
            public void log(@NotNull String message, @NotNull LogType type, @NotNull Object... args) {
                logs.add(message);
            }
        };
    }

    @AfterAll
    public static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    public void setup() {
        this.inventoryManager = plugin.getInventoryManager();
        opened = null;
    }

    @AfterEach
    public void cleanup() {
        for (int index = this.created.size() - 1; index >= 0; index--) {
            this.created.get(index).delete();
        }
        this.inventoryManager.deleteInventories(addon);
        this.reload(false);
    }

    private File write(String name, String content) throws IOException {
        return this.write(plugin, name, content);
    }

    private File write(Plugin owner, String name, String content) throws IOException {
        File file = new File(owner.getDataFolder(), "inventories/" + name + ".yml");
        for (File folder = file.getParentFile(); !folder.exists(); folder = folder.getParentFile()) {
            this.created.add(0, folder);
        }
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), content);
        this.created.add(file);
        return file;
    }

    private void delete(File file) {
        assertTrue(file.delete());
        this.created.remove(file);
    }

    private static String menu(String title) {
        return "size: 9\nname: \"" + title + "\"\nitems: {}\n";
    }

    private void reload(boolean inventoryPaths) {
        Configuration.enableInventoryPaths = inventoryPaths;
        YamlFileCache.clearCache();
        logs.clear();
        this.inventoryManager.loadInventories();
    }

    private void loadAddon(String name, String title) throws IOException, InventoryException {
        this.inventoryManager.loadInventory(addon, this.write(addon, name, menu(title)));
    }

    private Optional<Inventory> find(String reference) {
        return this.inventoryManager.findInventory(reference);
    }

    private String title(String reference) {
        return this.find(reference).map(Inventory::getName).orElse(null);
    }

    private static boolean logged(String text) {
        return logs.stream().anyMatch(line -> line.contains(text));
    }

    private List<String> suggest(String input) {
        SuggestionsBuilder builder = new SuggestionsBuilder(input, 0);
        NameSuggestions.suggestInventories(builder, this.inventoryManager);
        return builder.build().getList().stream().map(Suggestion::getText).toList();
    }

    private void runInventoryAction(PlayerMock viewer, String reference, String page, List<String> arguments) {
        InventoryAction action = new InventoryAction(this.inventoryManager, plugin.getCommandManager(), reference, null, page, arguments);
        action.setChance(100);
        action.preExecute(viewer, null, this.inventoryManager.getFakeInventory(), new Placeholders());
    }

    @Test
    public void theOptionIsOffByDefaultAndWhenItsKeyIsMissing() throws InvalidConfigurationException {
        assertTrue(plugin.getConfig().isBoolean("enable-inventory-paths"), "the key is at the root of config.yml");
        assertFalse(plugin.getConfig().getBoolean("enable-inventory-paths"));
        assertFalse(plugin.getConfig().contains("menus"), "MENUS is a comment, not a section");
        assertFalse(Configuration.enableInventoryPaths);
        assertFalse(this.inventoryManager.usesInventoryPaths());

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(plugin.getConfig().saveToString());
        try {
            configuration.set("enable-inventory-paths", true);
            Configuration.getInstance().load(configuration);
            assertTrue(Configuration.enableInventoryPaths);

            configuration.set("enable-inventory-paths", null);
            Configuration.getInstance().load(configuration);
            assertFalse(Configuration.enableInventoryPaths, "a config.yml written before the option keeps it off");
        } finally {
            Configuration.getInstance().load(plugin.getConfig());
        }
    }

    @Test
    public void offAFolderInventoryIsNamedByItsFileName() throws IOException {
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_admin/paths_dup", menu("Dup admin"));
        this.write("paths_player/paths_dup", menu("Dup player"));
        this.reload(false);

        assertEquals("Admin", this.title("paths_shop"));
        assertEquals("Admin", this.title("zMenu:paths_shop"));
        assertEquals(Key.key("zmenu", "paths_shop"), this.find("paths_shop").orElseThrow().key());
        assertFalse(this.find("paths_admin/paths_shop").isPresent(), "no path while the option is off");
        assertFalse(this.find("paths_player/paths_dup").isPresent());

        assertEquals(1, this.inventoryManager.getInventories(plugin).stream().filter(inventory -> inventory.getFileName().equals("paths_dup")).count());
        assertTrue(logged(SAME_NAME), "the second file of the same name is still refused");
        assertTrue(this.inventoryManager.getInventoryKeys().stream().noneMatch(key -> key.value().contains("/")));
    }

    @Test
    public void offANameSharedWithAnotherPluginKeepsItsResolutionAndItsWarning() throws IOException, InventoryException {
        this.write("paths_baltop", menu("zMenu baltop"));
        this.reload(false);
        this.loadAddon("paths_baltop", "zEssentials baltop");
        this.loadAddon("paths_addon_only", "zEssentials only");

        assertEquals("zMenu baltop", this.title("paths_baltop"));
        assertEquals("zEssentials baltop", this.title("zEssentials:paths_baltop"));
        assertEquals("zEssentials only", this.title("paths_addon_only"), "a plain name still reaches another plugin");
        assertTrue(logged(SHARED_NAME));
        assertTrue(logged("always open the one from zMenu"));
    }

    @Test
    public void onARootFileKeepsItsNameAndAFolderFileNeedsItsFullPath() throws IOException {
        this.write("paths_root", menu("Root"));
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_categories/paths_weapons/paths_swords", menu("Swords"));
        this.reload(true);

        assertTrue(this.inventoryManager.usesInventoryPaths());
        assertEquals("Root", this.title("paths_root"));
        assertEquals("Root", this.title("zMenu:paths_root"), "plugin:name is unchanged");
        assertEquals("Admin", this.title("paths_admin/paths_shop"));
        assertEquals("Admin", this.title("Paths_Admin/PATHS_SHOP"), "the case is ignored, as for file names");
        assertEquals("Swords", this.title("paths_categories/paths_weapons/paths_swords"));
        assertEquals(Key.key("zmenu", "paths_admin/paths_shop"), this.find("paths_admin/paths_shop").orElseThrow().key());
        assertEquals("paths_shop", this.find("paths_admin/paths_shop").orElseThrow().getFileName());

        assertFalse(this.find("paths_shop").isPresent(), "the file name alone does not open a folder inventory, even when unique");
        assertFalse(this.find("zMenu:paths_shop").isPresent());
        assertFalse(this.find("paths_swords").isPresent());
        assertFalse(this.find("paths_weapons/paths_swords").isPresent(), "an incomplete path is not resolved");
        assertFalse(this.find("paths_categories/paths_swords").isPresent());
    }

    @Test
    public void onFilesOfTheSameNameAreDifferentInventories() throws IOException {
        this.write("paths_shop", menu("Root"));
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_player/paths_shop", menu("Player"));
        this.reload(true);

        assertEquals("Root", this.title("paths_shop"));
        assertEquals("Admin", this.title("paths_admin/paths_shop"));
        assertEquals("Player", this.title("paths_player/paths_shop"));
        assertNotSame(this.find("paths_admin/paths_shop").orElseThrow(), this.find("paths_player/paths_shop").orElseThrow());
        assertEquals(3, this.inventoryManager.getInventories(plugin).stream().filter(inventory -> inventory.getFileName().equals("paths_shop")).count());
        assertFalse(logged(SAME_NAME));

        // The same result whatever the order they are loaded in
        this.reload(true);
        assertEquals("Root", this.title("paths_shop"));
        assertEquals("Admin", this.title("paths_admin/paths_shop"));
        assertEquals("Player", this.title("paths_player/paths_shop"));
    }

    @Test
    public void onAnInventoryOfAnotherPluginNeedsItsNamespace() throws IOException, InventoryException {
        this.write("paths_baltop", menu("zMenu baltop"));
        this.write("paths_stats/paths_top", menu("zMenu top"));
        this.reload(true);
        this.loadAddon("paths_baltop", "zEssentials baltop");
        this.loadAddon("paths_top", "zEssentials top");
        this.loadAddon("paths_addon_only", "zEssentials only");

        assertEquals("zMenu baltop", this.title("paths_baltop"));
        assertEquals("zEssentials baltop", this.title("zEssentials:paths_baltop"));
        assertEquals("zEssentials baltop", this.title("ZESSENTIALS:Paths_Baltop"));

        assertFalse(this.find("paths_top").isPresent(), "neither the folder inventory of zMenu nor the one of zEssentials");
        assertEquals("zMenu top", this.title("paths_stats/paths_top"));
        assertEquals("zEssentials top", this.title("zEssentials:paths_top"));

        assertFalse(this.find("paths_addon_only").isPresent(), "a plain name does not reach another plugin anymore");
        assertEquals("zEssentials only", this.title("zEssentials:paths_addon_only"));

        assertFalse(logged(SHARED_NAME), "the name is not ambiguous anymore");
    }

    @Test
    public void onAReferenceIsNeverReadAsAFilePath() throws IOException {
        this.write("paths_shop", menu("Root"));
        File nested = this.write("paths_admin/paths_shop", menu("Admin"));
        File outside = new File(plugin.getDataFolder(), "paths_outside.yml");
        Files.writeString(outside.toPath(), menu("Outside"));
        this.created.add(outside);
        this.reload(true);

        for (String reference : List.of(
                "../paths_outside", "../inventories/paths_shop", "./paths_shop", "/paths_shop", "paths_admin/../paths_shop",
                "paths_admin/./paths_shop", "paths_admin//paths_shop", "paths_admin/paths_shop/", "/paths_admin/paths_shop",
                "paths_admin\\paths_shop", "..\\paths_outside", "paths_admin/paths_shop.yml", "paths_shop.yml", "inventories/paths_shop",
                nested.getAbsolutePath(), nested.getAbsolutePath().replace('\\', '/'), outside.getAbsolutePath(), "")) {
            assertFalse(this.find(reference).isPresent(), reference);
        }
        assertEquals(Key.key("zmenu", "../paths_shop"), ZMenuKeys.inventoryPathKey("../paths_shop"), "a path is a name, it is not resolved");
    }

    @Test
    public void onAFileWithSpecialCharactersIsNamedLikeItsFileName() throws IOException {
        this.write("Paths Folder/My Shop", menu("Spaces"));
        this.write("paths-dash/paths-shop", menu("Dash"));
        this.reload(true);

        assertEquals("Spaces", this.title("Paths Folder/My Shop"));
        assertEquals("Spaces", this.title("paths_folder/my_shop"));
        assertEquals("Dash", this.title("paths-dash/paths-shop"));
        assertFalse(this.find("paths_dash/paths_shop").isPresent(), "\"-\" stays \"-\", as in a file name");
    }

    @Test
    public void suggestionsFollowTheReferences() throws IOException, InventoryException {
        this.write("paths_root", menu("Root"));
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_player/paths_shop", menu("Player"));
        this.write("paths_categories/paths_weapons/paths_swords", menu("Swords"));
        this.reload(true);
        this.loadAddon("paths_baltop", "zEssentials baltop");

        List<String> all = this.suggest("");
        assertTrue(all.containsAll(List.of("paths_root", "paths_admin/paths_shop", "paths_player/paths_shop", "paths_categories/paths_weapons/paths_swords", "zessentials:paths_baltop")), all.toString());
        assertFalse(all.contains("paths_shop"));
        assertFalse(all.contains("paths_swords"));
        assertFalse(all.contains("paths_baltop"), "an inventory of another plugin is only suggested with its namespace");
        assertTrue(all.stream().noneMatch(suggestion -> suggestion.startsWith("zmenu:")));
        for (String suggestion : all) {
            assertTrue(this.find(suggestion).isPresent(), suggestion + " is suggested, so it must open");
        }

        assertEquals(List.of("paths_admin/paths_shop"), this.suggest("paths_admin/"));
        assertEquals(List.of("paths_admin/paths_shop"), this.suggest("PATHS_ADMIN/p"));
        assertEquals(List.of("zessentials:paths_baltop"), this.suggest("zessentials:paths_"));
        assertEquals(List.of(), this.suggest("paths_shop"));

        this.reload(false);
        this.loadAddon("paths_baltop", "zEssentials baltop");
        List<String> historical = this.suggest("");
        assertTrue(historical.containsAll(List.of("paths_root", "paths_shop", "paths_swords", "zessentials:paths_baltop")), historical.toString());
        assertTrue(historical.stream().noneMatch(suggestion -> suggestion.contains("/")), "no path while the option is off");
        assertEquals(List.of("zmenu:paths_root"), this.suggest("zmenu:paths_r"));
    }

    @Test
    public void onTheInventoryActionAndButtonOpenAPath() throws IOException {
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_player/paths_shop", menu("Player"));
        this.write("paths_menu", "size: 9\nname: \"Menu\"\nitems:\n  open:\n    type: INVENTORY\n    inventory: \"paths_player/paths_shop\"\n    to-page: 2\n    slot: 0\n    item:\n      material: STONE\n");
        this.reload(true);
        Inventory admin = this.find("paths_admin/paths_shop").orElseThrow();
        Inventory player = this.find("paths_player/paths_shop").orElseThrow();
        PlayerMock viewer = server.addPlayer();

        this.runInventoryAction(viewer, "paths_admin/paths_shop", "3", List.of("target:steve"));
        server.getScheduler().performTicks(2L);
        assertSame(admin, opened.getInventory());
        assertSame(viewer, opened.getPlayer());
        assertEquals(3, opened.getPage());
        assertEquals("steve", plugin.getCommandManager().getPlayerArgument(viewer, "target").orElse(null));

        Button button = this.find("paths_menu").orElseThrow().getButtons(InventoryButton.class).getFirst();
        InventoryClickEvent event = new InventoryClickEvent(viewer.openInventory(server.createInventory(null, 9)), InventoryType.SlotType.CONTAINER, 0, ClickType.LEFT, org.bukkit.event.inventory.InventoryAction.PICKUP_ALL);
        button.onClick(viewer, event, this.inventoryManager.getFakeInventory(), 0, new Placeholders());
        server.getScheduler().performTicks(2L);
        assertSame(player, opened.getInventory());
        assertEquals(2, opened.getPage());
    }

    @Test
    @SuppressWarnings("removal")
    public void onOpeningByNameKeepsThePlayerThePageAndTheMessage() throws IOException {
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.reload(true);
        Inventory admin = this.find("paths_admin/paths_shop").orElseThrow();
        PlayerMock viewer = server.addPlayer();

        assertSame(admin, this.inventoryManager.getInventory("paths_admin/paths_shop").orElseThrow());
        this.inventoryManager.openInventory(viewer, "paths_admin/paths_shop", 2);
        assertSame(admin, opened.getInventory());
        assertSame(viewer, opened.getPlayer());
        assertEquals(2, opened.getPage());

        opened = null;
        this.inventoryManager.openInventory(viewer, admin.key());
        assertSame(admin, opened.getInventory());

        opened = null;
        while (viewer.nextMessage() != null) ;
        this.inventoryManager.openInventory(viewer, "paths_shop", 1);
        assertNull(opened);
        assertTrue(String.valueOf(viewer.nextMessage()).contains("paths_shop"), "the usual inventory not found message");
    }

    @Test
    public void onGiveOpenItemAndASingleReloadUseThePath() throws IOException {
        String openItem = "size: 9\nname: \"%title%\"\nopen-with-item:\n  item:\n    material: COMPASS\n  actions:\n    - RIGHT_CLICK_AIR\nitems: {}\n";
        this.write("paths_admin/paths_shop", openItem.replace("%title%", "Admin 1"));
        this.write("paths_player/paths_shop", menu("Player"));
        this.reload(true);
        Inventory admin = this.find("paths_admin/paths_shop").orElseThrow();
        Inventory player = this.find("paths_player/paths_shop").orElseThrow();

        assertTrue(admin.getOpenWithItem() != null, "what /zmenu giveopenitem paths_admin/paths_shop gives");
        assertTrue(this.inventoryManager.matches(admin, "paths_admin/paths_shop"));
        assertFalse(this.inventoryManager.matches(admin, "paths_shop"));
        assertFalse(this.inventoryManager.matches(admin, "paths_player/paths_shop"));

        this.write("paths_admin/paths_shop", menu("Admin 2"));
        YamlFileCache.clearCache();
        this.inventoryManager.reloadInventory(admin);

        assertEquals("Admin 2", this.title("paths_admin/paths_shop"));
        assertSame(player, this.find("paths_player/paths_shop").orElseThrow(), "the other file of the same name is untouched");
        assertEquals(2, this.inventoryManager.getInventories(plugin).stream().filter(inventory -> inventory.getFileName().equals("paths_shop")).count());
    }

    @Test
    public void onADeletedOrMovedFileLeavesNoReference() throws IOException, InventoryException {
        File admin = this.write("paths_admin/paths_shop", menu("Admin"));
        File gone = this.write("paths_admin/paths_gone", menu("Gone"));
        this.reload(true);

        // Moved to another folder, then everything is loaded again
        this.delete(admin);
        this.write("paths_player/paths_shop", menu("Moved"));
        this.delete(gone);
        this.reload(true);
        assertFalse(this.find("paths_admin/paths_shop").isPresent());
        assertFalse(this.find("paths_admin/paths_gone").isPresent());
        assertEquals("Moved", this.title("paths_player/paths_shop"));

        // Added, changed then removed one file at a time, as the hot reload does
        File added = this.write("paths_added/paths_new", menu("New 1"));
        this.inventoryManager.loadInventory(plugin, added);
        assertEquals("New 1", this.title("paths_added/paths_new"));
        assertFalse(this.find("paths_new").isPresent());

        this.write("paths_added/paths_new", menu("New 2"));
        YamlFileCache.clearCache();
        this.inventoryManager.loadInventory(plugin, added);
        assertEquals("New 2", this.title("paths_added/paths_new"));
        assertEquals(1, this.inventoryManager.getInventories(plugin).stream().filter(inventory -> inventory.getFileName().equals("paths_new")).count());

        this.inventoryManager.deleteInventory(this.find("paths_added/paths_new").orElseThrow());
        assertFalse(this.find("paths_added/paths_new").isPresent());
        assertEquals("Moved", this.title("paths_player/paths_shop"));
    }

    @Test
    public void switchingTheOptionRenamesTheInventoriesOnceTheyAreLoadedAgain() throws IOException {
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.write("paths_admin/paths_broken", menu("Broken"));
        this.reload(false);
        assertEquals("Admin", this.title("paths_shop"));

        // Until they are loaded again, the inventories keep the names they were loaded with
        Configuration.enableInventoryPaths = true;
        assertFalse(this.inventoryManager.usesInventoryPaths());
        assertEquals("Admin", this.title("paths_shop"));

        this.write("paths_admin/paths_broken", "size: [\n");
        this.reload(true);
        assertEquals("Admin", this.title("paths_admin/paths_shop"));
        assertFalse(this.find("paths_shop").isPresent());
        assertFalse(this.find("paths_broken").isPresent(), "a version named by its file name is not kept");
        assertFalse(this.find("paths_admin/paths_broken").isPresent());

        this.reload(false);
        assertFalse(this.inventoryManager.usesInventoryPaths());
        assertEquals("Admin", this.title("paths_shop"));
        assertFalse(this.find("paths_admin/paths_shop").isPresent(), "no path is left once the option is off again");
        assertTrue(this.inventoryManager.getInventoryKeys().stream().noneMatch(key -> key.value().contains("/")));
        assertEquals(List.of(), this.suggest("paths_admin/"));
    }

    @Test
    public void onABrokenFileKeepsItsPreviousVersionUnderItsPath() throws IOException {
        this.write("paths_admin/paths_shop", menu("Admin"));
        this.reload(true);
        Inventory first = this.find("paths_admin/paths_shop").orElseThrow();

        this.write("paths_admin/paths_shop", "size: [\n");
        this.reload(true);

        assertSame(first, this.find("paths_admin/paths_shop").orElseThrow());
    }
}
