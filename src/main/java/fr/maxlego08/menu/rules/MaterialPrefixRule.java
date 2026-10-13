package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MaterialPrefixRule implements Rule {
    private final List<String> prefixes;

    public MaterialPrefixRule(@NotNull List<String> prefixes) {
        this.prefixes = prefixes;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        String materialName = context.getMaterial().name();
        for (String prefix : this.prefixes) {
            if (materialName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.prefixes.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "material-prefix");
        map.put("prefixes", this.prefixes);
        return map;
    }
}
