package fr.maxlego08.menu.font;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.maxlego08.menu.api.font.FontImage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemsAdderFont implements FontImage {

    private final Pattern pattern = Pattern.compile(":(\\w+):");

    @Override
    public String replace(String string) {
        Matcher matcher = pattern.matcher(string);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = FontImageWrapper.replaceFontImages(matcher.group(0));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
