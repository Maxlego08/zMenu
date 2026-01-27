package fr.maxlego08.menu.hooks.itemsadder;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import fr.maxlego08.menu.api.font.FontImage;
import org.jspecify.annotations.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemsAdderFont implements FontImage {

    private final Pattern pattern = Pattern.compile(":(\\w+):");

    @Override
    public @NonNull String replace(@NonNull String string) {
        Matcher matcher = pattern.matcher(string);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String replacement = FontImageWrapper.replaceFontImages(matcher.group(0));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
