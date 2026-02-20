package fr.maxlego08.menu.hooks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiniMessageColorUtils {
    private static final Pattern LEGACY_HEX_PATTERN = Pattern.compile("§x(§[0-9a-fA-F]){6}");
    private static final Pattern HEX_SHORT_PATTERN = Pattern.compile("(?<!<)(?<!:)(?<!</)(&?)#([a-fA-F0-9]{6})");
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

    protected String colorMiniMessage(String message) {

        String newMessage = message;

        // §x§r§g§b§2§f§3 → <#rgb2f3>
        newMessage = convertLegacyHex(newMessage);
        // &#a1b2c3 → <#a1b2c3>
        newMessage = convertShorLegacyHex(newMessage);
        // #a1b2c3 → <#a1b2c3>
        newMessage = newMessage.replaceAll("(?<![<&])(?<!:)(?<!</)#([A-Fa-f0-9]{6})", "<#$1>");

        // &a → <green>, §c → <red>, etc.
        newMessage = replaceLegacyColors(newMessage);

        return newMessage;
    }

    private @NotNull String convertLegacyHex(String message) {
        Matcher matcher = LEGACY_HEX_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String hex = matcher.group().replaceAll("§x|§", "");
            matcher.appendReplacement(sb, "<#" + hex + ">");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private @NotNull String convertShorLegacyHex(String message) {
        Matcher matcher = HEX_SHORT_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(2) + ">");
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    private String replaceLegacyColors(String message) {
        for (var entry : this.COLORS_MAPPINGS.entrySet()) {
            String key = entry.getKey();
            String value = "<" + entry.getValue() + ">";

            message = message.replace("&" + key, value)
                    .replace("§" + key, value)
                    .replace("&" + key.toUpperCase(), value)
                    .replace("§" + key.toUpperCase(), value);
        }
        return message;
    }
}
