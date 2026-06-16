package fr.maxlego08.menu.rules;

import java.util.List;
import java.util.Locale;

public class NamePrefixRule extends NameRule {
    public NamePrefixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase(Locale.ROOT).startsWith(name.toLowerCase(Locale.ROOT))
                : String::startsWith);
    }
}