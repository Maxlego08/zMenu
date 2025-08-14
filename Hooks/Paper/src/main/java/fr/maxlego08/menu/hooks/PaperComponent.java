package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.utils.SimpleCache;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PaperComponent {
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");
    private static final Pattern HEX_SHORT_PATTERN = Pattern.compile("(?<!<)&#([A-Fa-f0-9]{6})");

    private final SimpleCache<String, Component> cache = new SimpleCache<>();

    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();

    private final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final Map<String, String> COLORS_MAPPINGS = Map.ofEntries(
            Map.entry("0", "black"),
            Map.entry("1", "dark_blue"),
            Map.entry("2", "dark_green"),
            Map.entry("3", "dark_aqua"),
            Map.entry("4", "dark_red"),
            Map.entry("5", "dark_purple"),
            Map.entry("6", "gold"),
            Map.entry("7", "gray"),
            Map.entry("8", "dark_gray"),
            Map.entry("9", "blue"),
            Map.entry("a", "green"),
            Map.entry("b", "aqua"),
            Map.entry("c", "red"),
            Map.entry("d", "light_purple"),
            Map.entry("e", "yellow"),
            Map.entry("f", "white"),
            Map.entry("k", "obfuscated"),
            Map.entry("l", "bold"),
            Map.entry("m", "strikethrough"),
            Map.entry("n", "underlined"),
            Map.entry("o", "italic"),
            Map.entry("r", "reset")
    );


    private static volatile PaperComponent instance;


    public static PaperComponent getInstance() {
        if (instance == null) {
            synchronized (PaperComponent.class) {
                if (instance == null) {
                    instance = new PaperComponent();
                }
            }
        }
        return instance;
    }
    private PaperComponent() {
    }
    private TextDecoration.State getState(String text) {
        return text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>") ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
    }
    private String colorMiniMessage(String message) {
        // §x§r§g§b§2§f§3 → <#rgb2f3>
        message = convertLegacyHex(message);
        // &#a1b2c3 → <#a1b2c3>
        message = convertShorLegacyHex(message);
        // &a → <green>, §c → <red>, etc.
        message = replaceLegacyColors(message);
        return message;
    }
    private static @NotNull String convertLegacyHex(String message) {
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group().replaceAll("§x|§", "");
            matcher.appendReplacement(sb, "<#" + hex + ">");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private static @NotNull String convertShorLegacyHex(String message) {
        Matcher matcher = HEX_SHORT_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
    private String replaceLegacyColors(String message) {
        for (var entry : this.COLORS_MAPPINGS.entrySet()) {
            String key = entry.getKey();
            String value = "<"+entry.getValue()+">";

            message = message.replace("&" + key, value)
                    .replace("§" + key, value)
                    .replace("&" + key.toUpperCase(), value)
                    .replace("§" + key.toUpperCase(), value);
        }
        return message;
    }

    public List<String> getMessagesColorized(List<String> messages) {
        return messages.stream()
                .map(this::colorMiniMessage)
                .collect(Collectors.toList());
    }
    public String getMessageColorized(String message) {
        return colorMiniMessage(message);
    }
    public String getLegacyColorized(String message){
        return LEGACY_SERIALIZER.serialize(this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
    }
    public List<Component> getComponents(List<String> messages) {
        return messages.stream()
                .map(this::getComponent)
                .collect(Collectors.toList());
    }
    public List<Component> getComponent(List<String> messages, TextDecoration textDecoration, boolean state) {
        return messages.stream()
                .map(message -> getComponent(message).decoration(textDecoration, state))
                .collect(Collectors.toList());
    }
    public Component getComponent(String message) {
        return this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
    }

    public String getString(Component component) {
        return this.MINI_MESSAGE.serialize(component);
    }
}
