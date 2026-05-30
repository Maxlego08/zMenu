package fr.maxlego08.menu.rules;

import java.util.List;

public class NamePrefixRule extends NameRule {
    public NamePrefixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase().startsWith(name.toLowerCase())
                : String::startsWith);
    }
}