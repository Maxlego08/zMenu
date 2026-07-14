package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.annotations.AutoFontImage;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import fr.maxlego08.menu.api.font.FontImage;
import fr.maxlego08.menu.api.utils.Comparison;
import io.th0rgal.oraxen.OraxenPlugin;
import org.jspecify.annotations.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AutoFontImage
@RequiresPlugin(value = "Oraxen", version = "1.217", type = Comparison.GREATER_THAN_OR_EQUAL_TO)
public class OraxenFont implements FontImage {

    private final Pattern pattern = Pattern.compile("<glyph_(\\w+)>");

    @Override
    public @NonNull String replace(@NonNull String string) {
        var manager = OraxenPlugin.get().getFontManager();
        Matcher matcher = this.pattern.matcher(string);
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
