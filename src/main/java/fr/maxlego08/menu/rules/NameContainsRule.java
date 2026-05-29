package fr.maxlego08.menu.rules;

import java.util.List;

public class NameContainsRule extends NameRule {
    public NameContainsRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase().contains(name.toLowerCase())
                : String::contains);
    }
}