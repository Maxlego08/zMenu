package fr.maxlego08.menu.zcore.utils.toast.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorParser {

    private static final Pattern pattern = Pattern.compile(
            "(#[0-9a-fA-F]{6})|(§[0-9a-fk-or])|(&[0-9a-fk-or])|([^§&#]+)");

    public static List<Map<String, Object>> process(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);

        List<Map<String, Object>> result = new ArrayList<>();
        Matcher matcher = pattern.matcher(input);

        String color = null;
        Set<String> formats = new HashSet<>();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String formatCode = matcher.group(2);
            String text = matcher.group(4);

            if (hexColor != null) {
                color = hexColor;

            } else if (formatCode != null) {
                char code = formatCode.charAt(1);
                if (code == 'r') {
                    color = null;
                    formats.clear();
                } else if ("0123456789abcdef".indexOf(code) != -1) {
                    color = ColorCode.getColorName(code);
                } else {
                    formats.add(String.valueOf(code));
                }
            } else if (text != null) {
                Map<String, Object> part = new LinkedHashMap<>();
                part.put("text", text);
                if (color != null) part.put("color", color);

                for (String f : formats) {
                    switch (f) {
                        case "l": part.put("bold", true); break;
                        case "o": part.put("italic", true); break;
                        case "n": part.put("underlined", true); break;
                        case "m": part.put("strikethrough", true); break;
                        case "k": part.put("obfuscated", true); break;
                    }
                }

                result.add(part);
            }
        }

        return result;
    }

    public static String formatToJsonString(List<Map<String, Object>> msgList) {
        StringBuilder msgJson = new StringBuilder("[");
        for (int i = 0; i < msgList.size(); i++) {
            msgJson.append(mapToJson(msgList.get(i)));
            if (i < msgList.size() - 1) {
                msgJson.append(", ");
            }
        }
        msgJson.append("]");
        return msgJson.toString();
    }

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            if (i < map.size() - 1) sb.append(", ");
            i++;
        }
        sb.append("}");
        return sb.toString();
    }

}