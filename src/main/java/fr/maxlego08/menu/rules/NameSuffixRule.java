package fr.maxlego08.menu.rules;

import java.util.List;
import java.util.Locale;

public class NameSuffixRule extends NameRule {
    public NameSuffixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase(Locale.ROOT).endsWith(name.toLowerCase(Locale.ROOT))
                : String::endsWith);
    }
}