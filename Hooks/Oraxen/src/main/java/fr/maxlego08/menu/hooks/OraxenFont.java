package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.font.FontImage;
import io.th0rgal.oraxen.OraxenPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OraxenFont implements FontImage {

    private final Pattern pattern = Pattern.compile("<glyph_(\\w+)>");

    @Override
    public String replace(String string) {
        var manager = OraxenPlugin.get().getFontManager();
        Matcher matcher = pattern.matcher(string);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            var value = manager.getGlyphFromID(matcher.group(1));
            if (value == null) continue;
            String replacement = value.getCharacter();
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
