
package fr.maxlego08.menu.command;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.maxlego08.menu.api.ZMenuKeys;
import net.kyori.adventure.key.Key;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public final class NameSuggestions {

    private NameSuggestions() {
    }

    public static void suggest(SuggestionsBuilder builder, Collection<String> qualifiedNames) {
        suggest(builder, qualifiedNames, Function.identity());
    }

    public static void suggestKeys(SuggestionsBuilder builder, Collection<Key> keys) {
        suggest(builder, keys, Key::asString);
    }

    private static <T> void suggest(SuggestionsBuilder builder, Collection<T> values, Function<T, String> mapper) {
        String input = builder.getRemainingLowerCase();
        boolean qualifiedInput = input.indexOf(':') >= 0;

        Map<String, Integer> shortNameCount = new HashMap<>();

        for (T value : values) {
            String qualified = mapper.apply(value);
            shortNameCount.merge(shortNameOf(qualified), 1, Integer::sum);
        }

        for (T value : values) {
            String qualified = mapper.apply(value);
            String lowerQualified = qualified.toLowerCase(Locale.ROOT);

            if (qualifiedInput) {
                if (lowerQualified.startsWith(input)) {
                    builder.suggest(qualified);
                }
                continue;
            }

            String shortName = shortNameOf(qualified);
            boolean unique = shortNameCount.get(shortName) == 1;
            boolean defaultNamespace = qualified.startsWith(ZMenuKeys.NAMESPACE + ":");

            if (unique && defaultNamespace && shortName.toLowerCase(Locale.ROOT).startsWith(input)) {
                builder.suggest(shortName);
            } else if (lowerQualified.startsWith(input)) {
                builder.suggest(qualified);
            }
        }
    }

    private static String shortNameOf(String qualified) {
        int separator = qualified.indexOf(':');
        return separator < 0 ? qualified : qualified.substring(separator + 1);
    }
}
