package fr.maxlego08.menu.test;

import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.MiniMessageColorUtils;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.Placeholder;
import fr.robie.minimessageflow.Template;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Renders every text of the bundled configuration files with zMenu's MiniMessageFlow instance and with the conversion
 * zMenu used before it (legacy codes to MiniMessage by regular expressions, then MiniMessage), and compares what a
 * player sees: each character with the style it is shown in (colour, decorations, font, click, hover, insertion).
 * The component trees may be shaped differently; only the result has to match.
 */
class TextEngineParityTest extends MiniMessageColorUtils {

    private static final Path RESOURCES = Path.of("..", "..", "src", "main", "resources");
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();
    private static final List<String> EXTRA = List.of(
            "&cRed &lBold &rReset",
            "§x§a§1§b§2§c§3Hex",
            "&#ff8800Orange and #00ff00green",
            "<red>Mixed &aLegacy</red> text",
            "<gradient:#ff0000:#0000ff>Gradient &lbold</gradient>",
            "<hover:show_text:'&cHover'><click:run_command:'/say hi'>Click</click></hover>",
            "<rainbow>Rainbow text</rainbow>",
            "&7Price: &a1,250$ &8(-10%)",
            "<newline>Two<br>lines",
            "§x§a§1§b§2§c§3Hello", "§x§f§f§0§0§0§0Red§x§0§0§f§f§0§0Blue", "§x§A§B§C§D§E§FText", "§x§a§B§c§D§e§FText",
            "&#55ff55Hello", "&#ff0000Red&#00ff00Green&#0000ffBlue", "&#ABCDEF Text", "#55ff55Hello",
            "#ff0000Red#00ff00Green#0000ffBlue", "<#55ff55Hello", "Test#55ff55Hello", "&aGreen&cRed&9Blue",
            "§aGreen§cRed§9Blue", "&0&1&2&3&4&5&6&7&8&9&a&b&c&d&e&f", "&AHello&CWorld", "&aHello&CWorld&Etest",
            "&lBold Text", "&oItalic Text", "&nUnderlined Text", "&mStrikethrough Text", "&kObfuscated Text",
            "&rReset Text", "&k&l&m&n&o&r", "§lBold§oItalic§nUnderlined", "&LBold&OItalic", "&#ff0000&lBold Red",
            "&a&lBold Green&r&cRed", "§x§f§f§0§0§0§0A&#00ff00B#0000ffC",
            "&a&lGreen Bold §x§f§f§0§0§0§0&#00ff00Hex&r#0000ff&oBlue Italic", "&aAmpersand§cSection&lBold§oItalic",
            "Plain text without colors", "&#gghhii Invalid", "&#ff00 Incomplete", "<#ff0000>Already converted",
            "&a   Green   &c   Red", "&aGreen\n&cRed\n&9Blue", "&a&l&n&oText", "Text&a&l", "&#ff0000Start", "End&#ff0000",
            "&aHello!@#$%^&*()_+-=[]{}|;':\",./<>?", "&aHéllo Wörld 你好 مرحبا", "&a&l&n&#ff0000§x§0§0§f§f§0§0",
            "&#ff0000R&#ff3300a&#ff6600i&#ff9900n&#ffcc00b&#ffff00o&#ccff00w",
            "&#55ff55&l✦ &#55ff8e&lMY SERVER &#55ffc6&l✦\n&7Join us for fun!",
            "&#ff0000[&#ff9900Admin&#ff0000]&r &aPlayerName&f: Message", "<color:#FF5555>This is a <color:#55FF55>test!");

    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final MiniMessageFlow flow = TextService.get().flow();

    @Test
    @DisplayName("Every text of the bundled menus renders the same as with the previous conversion")
    void bundledTexts() throws IOException {
        Set<String> texts = this.texts();
        Assertions.assertTrue(texts.size() > 100, "only " + texts.size() + " texts found in " + RESOURCES.toAbsolutePath());
        List<String> different = new ArrayList<>();
        for (String text : texts) {
            List<Run> expected = runs(this.miniMessage.deserialize(this.colorMiniMessage(text)));
            List<Run> actual = runs(this.flow.format(text));
            if (!expected.equals(actual)) {
                different.add(text + "\n    before: " + expected + "\n    after:  " + actual);
            }
        }
        Assertions.assertTrue(different.isEmpty(), different.size() + " of " + texts.size() + " texts differ:\n" + String.join("\n", different));
    }

    @Test
    @DisplayName("Every bundled text as an item line (root style, italic off unless asked) shows as before")
    void bundledItemLines() throws IOException {
        Set<String> texts = this.texts();
        Component reset = Component.empty().decoration(TextDecoration.ITALIC, false);
        List<String> different = new ArrayList<>();
        for (String text : texts) {
            TextDecoration.State italic = ComponentMeta.italicState(text);
            Component old = reset.append(this.miniMessage.deserialize(this.colorMiniMessage(text)).decoration(TextDecoration.ITALIC, italic));
            Template line = this.flow.compile(text).style(Style.style(TextDecoration.ITALIC.withState(italic)));
            List<Run> expected = runs(old);
            List<Run> actual = runs(line.render());
            if (!expected.equals(actual)) {
                different.add(text + "\n    before: " + expected + "\n    after:  " + actual);
            }
        }
        Assertions.assertTrue(different.isEmpty(), different.size() + " of " + texts.size() + " lines differ:\n" + String.join("\n", different));
    }

    @Test
    @DisplayName("A formatted value keeps its colours; its colour ends with it")
    void formattedValues() {
        Placeholders placeholders = new Placeholders();
        placeholders.register("rank", "&c[Admin]");
        Component rendered = this.flow.compile("&7Rank: %rank% &7- online").render(placeholders.values(), null);
        Assertions.assertEquals("Rank: [Admin] - online", PLAIN.serialize(rendered));
        List<Run> runs = runs(rendered);
        Assertions.assertEquals(List.of("Rank: ", "[Admin]", " - online"), runs.stream().map(Run::text).toList());
        Assertions.assertEquals(NamedTextColor.RED, runs.get(1).style().color());
        Assertions.assertEquals(NamedTextColor.GRAY, runs.get(2).style().color());
    }

    @Test
    @DisplayName("Rendering from Placeholders.values() shows the same as giving each resolved value as formatted text")
    void valuesPath() {
        Placeholders root = new Placeholders();
        root.register("page", "2");
        root.register("rank", "&c[Admin]");
        root.registerLookup("state_", rest -> rest.equals("count") ? "1250" : null);
        Placeholders slot = root.child();
        slot.register("name", "steve");
        slot.register("price", "1250.5");
        for (String text : List.of("&7%rank% <white>%name.upper%", "&ePage %page% - %price.comma%", "%state_count.comma% %unknown%",
                "<gradient:red:blue>%name.capitalize%</gradient>", "&7Seller: &b%name% &8(%page%)", "%upper_name% %add_one_page%")) {
            Template template = this.flow.compile(text);
            Assertions.assertEquals(runs(template.render(resolved(template, slot), null)), runs(template.render(slot.values(), null)), text);
        }
    }

    @Test
    @DisplayName("A raw value is shown as typed, in text, in a click argument and with a modifier")
    void rawValues() {
        Placeholders placeholders = new Placeholders();
        placeholders.registerRaw("new_text", "&c<red>x' <click:run_command:'/op me'>y");
        Component shown = this.flow.compile("&7You typed: %new_text%").render(placeholders.values(), null);
        Assertions.assertEquals("You typed: &c<red>x' <click:run_command:'/op me'>y", PLAIN.serialize(shown));
        Assertions.assertTrue(runs(shown).stream().allMatch(run -> run.style().clickEvent() == null));
        Component upper = this.flow.compile("%new_text.upper%").render(placeholders.values(), null);
        Assertions.assertEquals("&C<RED>X' <CLICK:RUN_COMMAND:'/OP ME'>Y", PLAIN.serialize(upper));
        Component click = this.flow.compile("<click:suggest_command:'/name %new_text%'>[set]").render(placeholders.values(), null);
        Assertions.assertEquals("[set]", PLAIN.serialize(click));
    }

    /**
     * Each placeholder of a template resolved by {@link Placeholders#parse(String)}, given as formatted text: how a
     * value read as it was before reaches the template.
     */
    private static Placeholder resolved(Template template, Placeholders placeholders) {
        Placeholder.Builder builder = Placeholder.builder();
        for (String key : template.placeholders()) {
            String token = "%" + key + "%";
            String value = placeholders.parse(token);
            if (!value.equals(token)) {
                builder.registerFormatted(key, value);
            }
        }
        return builder.build();
    }

    private Set<String> texts() throws IOException {
        Set<String> texts = new LinkedHashSet<>(EXTRA);
        try (Stream<Path> files = Files.walk(RESOURCES)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".yml")).toList()) {
                collect(YamlConfiguration.loadConfiguration(file.toFile()), texts);
            }
        }
        return texts;
    }

    private static void collect(ConfigurationSection section, Set<String> texts) {
        for (String key : section.getKeys(false)) {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection child) {
                collect(child, texts);
            } else if (value instanceof String text) {
                add(text, texts);
            } else if (value instanceof List<?> list) {
                for (Object element : list) {
                    if (element instanceof String text) {
                        add(text, texts);
                    }
                }
            }
        }
    }

    private static void add(String text, Set<String> texts) {
        if (text.indexOf('&') >= 0 || text.indexOf('§') >= 0 || text.indexOf('<') >= 0 || text.indexOf('#') >= 0) {
            texts.add(text);
        }
    }

    /**
     * A stretch of text shown in one style, or a component that is not text, with its style.
     */
    private record Run(String text, Style style) {
        @Override
        public String toString() {
            return "'" + this.text + "'" + (this.style.isEmpty() ? "" : this.style);
        }
    }

    private static List<Run> runs(Component component) {
        List<Run> runs = new ArrayList<>();
        walk(component, Style.empty(), runs);
        return runs;
    }

    private static void walk(Component node, Style parent, List<Run> runs) {
        Style style = parent.merge(node.style());
        if (node instanceof TextComponent text) {
            if (!text.content().isEmpty()) {
                Run last = runs.isEmpty() ? null : runs.getLast();
                if (last != null && Objects.equals(last.style(), style) && !last.text().startsWith("\0")) {
                    runs.set(runs.size() - 1, new Run(last.text() + text.content(), style));
                } else {
                    runs.add(new Run(text.content(), style));
                }
            }
        } else {
            runs.add(new Run("\0" + node.getClass().getSimpleName(), style));
        }
        for (Component child : node.children()) {
            walk(child, style, runs);
        }
    }
}
