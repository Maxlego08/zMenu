package fr.maxlego08.menu.rules;

import java.util.List;

public class NameExactRule extends NameRule {
    public NameExactRule(List<String> names, boolean ignoreCase) {
        super(names, ignoreCase
                ? String::equalsIgnoreCase
                : String::equals);
    }
}
