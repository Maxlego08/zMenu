package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MaterialContainsRule implements Rule {
    private final List<String> patterns;

    public MaterialContainsRule(@NotNull List<@NotNull String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        String materialName = context.getMaterial().name();
        for (String pattern : this.patterns) {
            if (materialName.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.patterns.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "material-contains");
        map.put("patterns", this.patterns);
        return map;
    }
}
