package fr.maxlego08.menu.rules;

import java.util.List;

public class NameSuffixRule extends NameRule {
    public NameSuffixRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? (display, name) -> display.toLowerCase().endsWith(name.toLowerCase())
                : String::endsWith);
    }
}