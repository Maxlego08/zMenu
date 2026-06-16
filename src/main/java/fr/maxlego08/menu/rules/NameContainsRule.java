package fr.maxlego08.menu.rules;

import java.util.List;
import java.util.Locale;

public class NameContainsRule extends NameRule {
    public NameContainsRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT))
                : String::contains);
    }
}