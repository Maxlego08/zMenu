package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrRule implements Rule {
    private final List<Rule> children;

    public OrRule(@NotNull List<@NotNull Rule> children) {
        this.children = children;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        for (Rule child : this.children) {
            if (child.matches(context)) return true;
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.children.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "or");
        map.put("rules", this.children.stream().map(Rule::serialize).toList());
        return map;
    }
}
