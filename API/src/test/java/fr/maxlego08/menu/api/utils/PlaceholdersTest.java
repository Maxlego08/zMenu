package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.text.TextService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholdersTest {

    @Test
    @DisplayName("register, get, has and parse in one scope")
    void basics() {
        Placeholders placeholders = new Placeholders();
        assertEquals("Hello %name%", placeholders.parse("Hello %name%"));
        placeholders.register("name", "Steve");
        assertEquals("Steve", placeholders.get("name"));
        assertTrue(placeholders.has("name"));
        assertFalse(placeholders.has("other"));
        assertEquals("Hello Steve, 100% sure %other%", placeholders.parse("Hello %name%, 100% sure %other%"));
        placeholders.register("empty", null);
        assertEquals("[]", placeholders.parse("[%empty%]"));
        placeholders.register(null, "ignored");
    }

    @Test
    @DisplayName("A child reads its parent; its own values shadow and stay its own")
    void scopes() {
        Placeholders root = new Placeholders();
        root.register("page", "1");
        Placeholders child = root.child();
        child.register("index", "4");
        assertEquals("1-4", child.parse("%page%-%index%"));
        assertEquals("1-%index%", root.parse("%page%-%index%"));
        child.register("page", "9");
        assertEquals("9", child.get("page"));
        assertEquals("1", root.get("page"));
        assertSame(root, child.getParent());
        assertSame(root, child.getRoot());
        assertNull(root.getParent());
    }

    @Test
    @DisplayName("Scope.PARENT and Scope.ROOT write above; without a parent they write here")
    void writeScopes() {
        Placeholders root = new Placeholders();
        Placeholders middle = root.child();
        Placeholders leaf = middle.child();
        leaf.register("a", "parent", Placeholders.Scope.PARENT);
        leaf.register("b", "root", Placeholders.Scope.ROOT);
        assertEquals("parent", middle.get("a"));
        assertNull(root.get("a"));
        assertEquals("root", root.get("b"));
        root.register("c", "here", Placeholders.Scope.PARENT);
        assertEquals("here", root.get("c"));
    }

    @Test
    @DisplayName("Prefix lookups answer keys without a value, each time they are read, through scopes")
    void lookups() {
        Placeholders root = new Placeholders();
        int[] counter = {1};
        root.registerLookup("state_", rest -> rest.equals("count") ? String.valueOf(counter[0]) : null);
        Placeholders child = root.child();
        assertEquals("1", child.parse("%state_count%"));
        counter[0] = 2;
        assertEquals("2 %state_other%", child.parse("%state_count% %state_other%"));
    }

    @Test
    @DisplayName("Modifiers and legacy prefixes")
    void modifiers() {
        Placeholders placeholders = new Placeholders();
        placeholders.register("name", "steve");
        placeholders.register("coins", "1250");
        assertEquals("STEVE Steve 1,250 1251 1249", placeholders.parse("%name.upper% %name.capitalize% %coins.comma% %coins.add_one% %coins.remove_one%"));
        assertEquals("STEVE Steve 1251", placeholders.parse("%upper_name% %capitalize_name% %add_one_coins%"));
        assertEquals("", placeholders.parse("%name.nosuchmodifier%"));
    }

    @Test
    @DisplayName("merge copies the other scope's values and lookups; flatten gives everything visible")
    void mergeAndFlatten() {
        Placeholders other = new Placeholders();
        other.register("x", "1");
        other.registerLookup("l_", rest -> "L" + rest);
        Placeholders target = new Placeholders();
        target.register("y", "2");
        target.merge(other);
        assertEquals("1 2 Lz", target.parse("%x% %y% %l_z%"));
        Placeholders child = target.child();
        child.register("y", "3");
        Map<String, String> flat = child.flatten();
        assertEquals("1", flat.get("x"));
        assertEquals("3", flat.get("y"));
    }

    @Test
    @DisplayName("parse with one key and value, and lists")
    void oneShotAndLists() {
        Placeholders placeholders = new Placeholders(Map.of("a", "A"));
        assertEquals("%a%-B", placeholders.parse("%a%-%b%", "b", "B"));
        assertEquals(java.util.List.of("A", "%b%"), placeholders.parse(java.util.List.of("%a%", "%b%")));
    }

    @Test
    @DisplayName("registerRaw: the same text through parse, marked raw through scopes, merge and odd names")
    void raw() {
        Placeholders root = new Placeholders();
        root.registerRaw("input", "&c<red>typed");
        root.register("config", "&aok");
        root.registerRaw("odd key", "<b>x");
        Placeholders child = root.child();
        assertEquals("&c<red>typed &aok <b>x", child.parse("%input% %config% %odd key%"));
        assertTrue(child.isRaw("input"));
        assertTrue(child.isRaw("odd key"));
        assertFalse(child.isRaw("config"));
        assertFalse(child.isRaw("missing"));
        child.register("input", "shadow");
        assertFalse(child.isRaw("input"));
        Placeholders merged = new Placeholders();
        merged.merge(root);
        assertTrue(merged.isRaw("input"));
        assertFalse(merged.isRaw("config"));
    }

    @Test
    @DisplayName("A modifier a plugin registers works in parse and in a compiled template")
    void customModifier() {
        Placeholders.registerModifier("shout", (value, args) -> value + "!");
        Placeholders placeholders = new Placeholders();
        placeholders.register("name", "Steve");
        assertEquals("Steve!", placeholders.parse("%name.shout%"));
        fr.robie.minimessageflow.MiniMessageFlow flow = fr.maxlego08.menu.api.text.TextService.get().flow();
        assertEquals("Steve!", net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(flow.compile("%name.shout%").render(placeholders.values(), null)));
    }

    @Test
    @DisplayName("of(Values) reads an addon's values and their parents, without changing them")
    void ofValues() {
        fr.robie.minimessageflow.Values base = fr.maxlego08.menu.api.text.TextService.get().flow().newValues().setFormatted("shop", "&aShop");
        fr.robie.minimessageflow.Values layer = base.child().set("buyer", "&cSteve")
                .set("item", net.kyori.adventure.text.Component.text("Sword"));
        Placeholders placeholders = Placeholders.of(layer);
        placeholders.register("price", "10");
        assertEquals("&aShop &cSteve Sword 10", placeholders.parse("%shop% %buyer% %item% %price%"));
        assertTrue(placeholders.isRaw("buyer"));
        assertFalse(placeholders.isRaw("shop"));
        assertFalse(layer.isSet("price"));
        assertThrows(IllegalArgumentException.class, () -> Placeholders.of(fr.robie.minimessageflow.MiniMessageFlow.create().newValues()));
    }

    @Test
    @DisplayName("Keys a template could not name still work")
    void oddKeys() {
        Placeholders placeholders = new Placeholders();
        placeholders.register("my key", "spaced");
        placeholders.register("a#b", "hash");
        assertEquals("spaced hash", placeholders.parse("%my key% %a#b%"));
        assertEquals("spaced", placeholders.flatten().get("my key"));
    }

    @Test
    @DisplayName("Player input: hidden from PlaceholderAPI in text, shown as typed in a template")
    void rawInputPercent() {
        Placeholders placeholders = new Placeholders();
        placeholders.registerRaw("typed", "%server_ip% <red>hi");
        placeholders.register("configured", "%server_ip%");

        assertEquals("%server_ip% <red>hi", placeholders.get("typed"));
        assertEquals("say \uF000server_ip\uF000 <red>hi %server_ip%", placeholders.parse("say %typed% %configured%"));

        Component rendered = TextService.get().flow().format("%typed%", placeholders.values(), null);
        assertEquals("%server_ip% <red>hi", PlainTextComponentSerializer.plainText().serialize(rendered));
    }

    @Test
    @DisplayName("A raw lookup is raw: shown as typed in a template, its % hidden from PlaceholderAPI in text")
    void rawLookup() {
        Placeholders placeholders = new Placeholders();
        placeholders.registerRawLookup("state_", key -> key.equals("name") ? "<red>%server_ip%" : null);
        placeholders.registerLookup("fmt_", key -> "<red>x");

        assertTrue(placeholders.isRaw("state_name"));
        assertFalse(placeholders.isRaw("fmt_any"));
        assertEquals("\uF000server_ip\uF000", placeholders.parse("%state_name%").substring(5));

        Component rendered = TextService.get().flow().format("%state_name% %fmt_any%", placeholders.values(), null);
        assertEquals("<red>%server_ip% x", PlainTextComponentSerializer.plainText().serialize(rendered));
    }
}
