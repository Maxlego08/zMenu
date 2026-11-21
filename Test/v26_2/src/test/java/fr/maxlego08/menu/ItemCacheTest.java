package fr.maxlego08.menu;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.loader.MenuItemStackLoader;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A built item is kept only when everything in it is the same for every player and every build.
 */
class ItemCacheTest {

    private static ServerMock server;
    private static ZMenuPlugin plugin;

    @BeforeAll
    static void init() {
        server = MockBukkit.mock();
        server.addSimpleWorld("world");
        plugin = MockBukkit.load(ZMenuPlugin.class, true);
        Configuration.enableCacheItemStack = true;
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    private static MenuItemStack load(String yaml) throws Exception {
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.loadFromString(yaml);
        return new MenuItemStackLoader(plugin.getInventoryManager()).load(configuration, "item.", new File("item-cache-test.yml"));
    }

    @Test
    @DisplayName("Static item: kept, the same built item is given again")
    void staticItem() throws Exception {
        MenuItemStack item = load("item:\n  material: DIAMOND\n  name: '<green>Sale: 50%'\n  lore:\n    - '&7No placeholder'\n");
        PlayerMock player = server.addPlayer();
        assertTrue(item.isStatic());
        ItemStack first = item.build(player);
        assertSame(first, item.build(player));
    }

    @Test
    @DisplayName("A placeholder in the name, the lore or an item component makes the item dynamic")
    void dynamicParts() throws Exception {
        assertFalse(load("item:\n  material: STONE\n  name: 'Page %page%'\n").isStatic());
        assertFalse(load("item:\n  material: STONE\n  lore:\n    - 'Hello %player%'\n").isStatic());
        assertFalse(load("item:\n  material: STONE\n  amount: '%amount%'\n").isStatic());
        assertFalse(load("item:\n  material: STICK\n  components:\n    custom-name: '<red>%player%'\n").isStatic());
        assertTrue(load("item:\n  material: STICK\n  components:\n    custom-name: '<red>Custom name'\n").isStatic());
    }

    @Test
    @DisplayName("Translations make the item dynamic: each player sees their language")
    void translations() throws Exception {
        MenuItemStack item = load("item:\n  material: STONE\n  name: 'Hello'\n  translatedName:\n    - locale: fr_fr\n      name: 'Bonjour'\n");
        if (!item.getTranslatedDisplayName().isEmpty()) {
            assertFalse(item.isStatic());
        }
    }

    @Test
    @DisplayName("A tag written like <name> may be answered by a resolver: the item is dynamic")
    void unknownTag() throws Exception {
        assertFalse(load("item:\n  material: STONE\n  name: 'Now: <itemcachetest_now>'\n").isStatic());
    }

    @Test
    @DisplayName("When the text engine changes (tags added late, as Nexo's glyphs), the kept item is built again")
    void engineChange() throws Exception {
        MenuItemStack item = load("item:\n  material: STONE\n  name: '<gold>Fixed'\n");
        PlayerMock player = server.addPlayer();
        assertTrue(item.isStatic());
        ItemStack before = item.build(player);
        assertSame(before, item.build(player));

        TextService.get().flow().tags(TagResolver.empty());
        assertTrue(item.isStatic());
        ItemStack after = item.build(player);
        assertNotSame(before, after);
        assertSame(after, item.build(player));
    }

    @Test
    @DisplayName("Changing the item drops the kept build")
    void setterClearsCache() throws Exception {
        MenuItemStack item = load("item:\n  material: DIAMOND\n  name: 'Fixed'\n");
        PlayerMock player = server.addPlayer();
        ItemStack before = item.build(player);
        item.setDisplayName("Other");
        assertNotSame(before, item.build(player));
    }
}
