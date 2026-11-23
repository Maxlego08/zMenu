package fr.maxlego08.menu;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.command.NameSuggestions;
import fr.maxlego08.menu.hooks.bedrock.ZBedrockManager;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code enable-inventory-paths} for the dialogs and the bedrock inventories: off, they are named by
 * their file name or their declared name as they always were; on, a file of a sub folder of the
 * {@code dialogs} or {@code bedrock} folder is named by its path there.
 */
public class DialogBedrockPathTest {

    private static ZMenuPlugin plugin;
    private static ZDialogManager dialogs;
    private static ZBedrockManager bedrock;
    private static Plugin addon;

    private final List<File> created = new ArrayList<>();

    @BeforeAll
    public static void init() {
        MockBukkit.mock();
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        dialogs = (ZDialogManager) plugin.getDialogManager();
        assertNotNull(dialogs, "dialogs are supported by this server");
        bedrock = new ZBedrockManager(plugin);
        addon = MockBukkit.createMockPlugin("zEssentials");
    }

    @AfterAll
    public static void tearDown() {
        Configuration.enableInventoryPaths = false;
        MockBukkit.unmock();
    }

    @AfterEach
    public void cleanup() {
        for (int index = this.created.size() - 1; index >= 0; index--) {
            this.created.get(index).delete();
        }
        this.reload(false);
    }

    private File write(String folder, String name, String content) throws IOException {
        return this.write(plugin, folder, name, content);
    }

    private File write(Plugin owner, String folder, String name, String content) throws IOException {
        File file = new File(owner.getDataFolder(), folder + "/" + name + ".yml");
        for (File parent = file.getParentFile(); !parent.exists(); parent = parent.getParentFile()) {
            this.created.add(0, parent);
        }
        file.getParentFile().mkdirs();
        Files.writeString(file.toPath(), content);
        this.created.add(file);
        return file;
    }

    private void dialog(String name, String title) throws IOException {
        this.write("dialogs", name, "type: notice\nname: \"" + title + "\"\n");
    }

    private void form(String name, String title) throws IOException {
        this.write("bedrock", name, "type: simple\nname: \"" + title + "\"\n");
    }

    private void reload(boolean inventoryPaths) {
        Configuration.enableInventoryPaths = inventoryPaths;
        dialogs.reloadDialogs();
        bedrock.reloadBedrockInventory();
    }

    /** Loads a dialog and a bedrock inventory of zEssentials, as it does through the API. */
    private void loadAddon(String name) throws Exception {
        dialogs.loadInventory(addon, this.write(addon, "dialogs", name, "type: notice\nname: \"Addon dialog\"\n"));
        bedrock.loadInventory(addon, this.write(addon, "bedrock", name, "type: simple\nname: \"Addon form\"\n"));
    }

    private static String fileOf(Optional<? extends Inventory> inventory) {
        return inventory.map(found -> found.getFile().getParentFile().getName() + "/" + found.getFileName()).orElse(null);
    }

    private static List<String> suggest(Collection<String> names, String input) {
        SuggestionsBuilder builder = new SuggestionsBuilder(input, 0);
        NameSuggestions.suggestReferences(builder, names, Configuration.enableInventoryPaths);
        return builder.build().getList().stream().map(Suggestion::getText).toList();
    }

    @Test
    public void offADialogOfAFolderIsNamedByItsFileName() throws IOException {
        this.dialog("paths_admin/paths_info", "Admin info");
        this.reload(false);

        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("zMenu:paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("Admin info")), "the declared name still opens it");
        assertFalse(dialogs.getDialog("paths_admin/paths_info").isPresent());
        assertTrue(dialogs.getDialogNames().contains("zMenu:paths_info"), dialogs.getDialogNames().toString());
        assertEquals("paths_info", ZDialogManager.reference(dialogs.getDialog("paths_info").orElseThrow()));
    }

    @Test
    public void onADialogOfAFolderNeedsItsFullPath() throws IOException {
        this.dialog("paths_info", "Root info");
        this.dialog("paths_admin/paths_info", "Admin info");
        this.dialog("paths_admin/paths_rules/paths_info", "Rules info");
        this.reload(true);

        assertEquals("dialogs/paths_info", fileOf(dialogs.getDialog("paths_info")), "a file of the folder itself keeps its name");
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("paths_admin/paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("PATHS_ADMIN/paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("zMenu:paths_admin/paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("zmenu:paths_admin/paths_info")));
        assertEquals("paths_rules/paths_info", fileOf(dialogs.getDialog("paths_admin/paths_rules/paths_info")));
        assertEquals("paths_admin/paths_info", fileOf(dialogs.getDialog("Admin info")), "the declared name still opens it");
        assertFalse(dialogs.getDialog("paths_rules/paths_info").isPresent(), "an incomplete path is not resolved");
        assertFalse(dialogs.getDialog("paths_admin/../paths_info").isPresent());
        assertEquals("paths_admin/paths_info", ZDialogManager.reference(dialogs.getDialog("paths_admin/paths_info").orElseThrow()));

        Set<String> names = dialogs.getDialogNames();
        assertTrue(names.containsAll(List.of("zMenu:paths_info", "zMenu:paths_admin/paths_info", "zMenu:paths_admin/paths_rules/paths_info")), names.toString());
        for (String suggestion : suggest(names, "")) {
            assertTrue(dialogs.getDialog(suggestion).isPresent(), suggestion + " is suggested, so it must open");
        }
        assertEquals(List.of("paths_admin/paths_rules/paths_info"), suggest(names, "PATHS_ADMIN/paths_r"), "a dialog of zMenu is suggested without its plugin, as for /zm open");
        assertEquals(List.of(), suggest(names, "zmenu:"));

        dialogs.deleteDialog("paths_admin/paths_info");
        assertFalse(dialogs.getDialog("paths_admin/paths_info").isPresent());
        assertTrue(dialogs.getDialog("paths_info").isPresent(), "only that dialog is deleted");
        assertFalse(dialogs.getDialogNames().contains("zMenu:paths_admin/paths_info"));

        this.reload(true);
        assertTrue(dialogs.getDialog("paths_admin/paths_info").isPresent(), "a reload loads it again under its path");
    }

    @Test
    public void onABedrockInventoryOfAFolderNeedsItsFullPath() throws IOException {
        this.form("paths_form", "Root form");
        this.form("paths_admin/paths_form", "Admin form");
        this.reload(true);

        assertEquals("bedrock/paths_form", fileOf(bedrock.getBedrockInventory("paths_form")));
        assertEquals("paths_admin/paths_form", fileOf(bedrock.getBedrockInventory("paths_admin/paths_form")));
        assertEquals("paths_admin/paths_form", fileOf(bedrock.getBedrockInventory("zMenu:paths_admin/paths_form")));
        assertEquals("paths_admin/paths_form", fileOf(bedrock.getBedrockInventory(plugin, "paths_admin/paths_form")));
        assertEquals("paths_admin/paths_form", fileOf(bedrock.getBedrockInventory("Admin form")), "the declared name still opens it");
        assertTrue(bedrock.getBedrockInventoryNames().containsAll(List.of("zmenu:paths_form", "zmenu:paths_admin/paths_form")), bedrock.getBedrockInventoryNames().toString());
        for (String suggestion : suggest(bedrock.getBedrockInventoryNames(), "")) {
            assertTrue(bedrock.getBedrockInventory(suggestion).isPresent(), suggestion + " is suggested, so it must open");
        }

        bedrock.deleteBedrockInventory("paths_admin/paths_form");
        assertFalse(bedrock.getBedrockInventory("paths_admin/paths_form").isPresent());
        assertTrue(bedrock.getBedrockInventory("paths_form").isPresent());

        this.reload(false);
        assertFalse(bedrock.getBedrockInventory("paths_admin/paths_form").isPresent(), "no path while the option is off");
        assertTrue(bedrock.getBedrockInventoryNames().stream().noneMatch(name -> name.contains("/")));
    }

    @Test
    public void aDialogOrABedrockInventoryOfAnotherPluginIsReadAsAnInventoryIs() throws Exception {
        this.dialog("paths_admin/paths_menu", "zMenu dialog");
        this.form("paths_admin/paths_menu", "zMenu form");

        this.reload(false);
        this.loadAddon("paths_addon");
        assertTrue(dialogs.getDialog("paths_addon").isPresent(), "off, a plain name reaches another plugin, as it always did");
        assertTrue(bedrock.getBedrockInventory("paths_addon").isPresent());
        assertTrue(dialogs.getDialog("zEssentials:paths_addon").isPresent());

        this.reload(true);
        this.loadAddon("paths_addon");
        assertFalse(dialogs.getDialog("paths_addon").isPresent(), "on, a plain name only names a dialog of zMenu, as for /zm open");
        assertFalse(bedrock.getBedrockInventory("paths_addon").isPresent());
        assertFalse(dialogs.getDialog("Addon dialog").isPresent(), "nor by its declared name");
        assertEquals("dialogs/paths_addon", fileOf(dialogs.getDialog("zEssentials:paths_addon")));
        assertEquals("dialogs/paths_addon", fileOf(dialogs.getDialog("ZESSENTIALS:Paths_Addon")));
        assertEquals("bedrock/paths_addon", fileOf(bedrock.getBedrockInventory("zEssentials:paths_addon")));
        assertEquals("paths_admin/paths_menu", fileOf(dialogs.getDialog("paths_admin/paths_menu")));
        assertEquals("paths_admin/paths_menu", fileOf(bedrock.getBedrockInventory("paths_admin/paths_menu")));
        assertEquals("paths_addon", ZDialogManager.reference(dialogs.getDialog("zEssentials:paths_addon").orElseThrow()), "a file of another plugin keeps its file name");

        List<String> dialogSuggestions = suggest(dialogs.getDialogNames(), "");
        List<String> formSuggestions = suggest(bedrock.getBedrockInventoryNames(), "");
        assertTrue(dialogSuggestions.containsAll(List.of("paths_admin/paths_menu", "zEssentials:paths_addon")), dialogSuggestions.toString());
        assertTrue(formSuggestions.containsAll(List.of("paths_admin/paths_menu", "zessentials:paths_addon")), formSuggestions.toString());
        assertFalse(dialogSuggestions.contains("paths_addon"), "another plugin is only suggested with its plugin");
        assertFalse(formSuggestions.contains("paths_addon"));
        for (String suggestion : dialogSuggestions) assertTrue(dialogs.getDialog(suggestion).isPresent(), suggestion);
        for (String suggestion : formSuggestions) assertTrue(bedrock.getBedrockInventory(suggestion).isPresent(), suggestion);
        assertEquals(List.of("zEssentials:paths_addon"), suggest(dialogs.getDialogNames(), "zess"));

        dialogs.deleteDialog("paths_addon");
        assertTrue(dialogs.getDialog("zEssentials:paths_addon").isPresent(), "a plain name deletes nothing of another plugin");
        dialogs.deleteDialog(addon);
        bedrock.deleteBedrockInventory(addon);
        assertFalse(dialogs.getDialog("zEssentials:paths_addon").isPresent());
        assertTrue(dialogs.getDialogNames().stream().noneMatch(name -> name.startsWith("zEssentials:")));
        assertTrue(bedrock.getBedrockInventoryNames().stream().noneMatch(name -> name.startsWith("zessentials:")));
    }
}
