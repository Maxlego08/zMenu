package fr.maxlego08.menu.command;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class NameSuggestions {

    private NameSuggestions() {
    }

    public static void suggest(SuggestionsBuilder builder, Set<String> qualifiedNames) {
        String input = builder.getRemainingLowerCase();

        Map<String, Integer> shortNameCount = new HashMap<>();
        for (String qualified : qualifiedNames) {
            shortNameCount.merge(shortNameOf(qualified), 1, Integer::sum);
        }

        for (String qualified : qualifiedNames) {
            String lowerQualified = qualified.toLowerCase(Locale.ROOT);

            if (input.indexOf(':') >= 0) {
                if (lowerQualified.startsWith(input)) {
                    builder.suggest(qualified);
                }
                continue;
            }

            String shortName = shortNameOf(qualified);
            boolean unique = shortNameCount.getOrDefault(shortName, 0) == 1;

            if (unique && shortName.toLowerCase(Locale.ROOT).startsWith(input)) {
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
