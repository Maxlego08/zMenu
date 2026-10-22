package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MaterialSuffixRule implements Rule {
    private final List<String> suffixes;

    public MaterialSuffixRule(@NotNull List<@NotNull String> suffixes) {
        this.suffixes = suffixes;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        String materialName = context.getMaterial().name();
        for (String suffix : this.suffixes) {
            if (materialName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.suffixes.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "material-suffix");
        map.put("suffixes", this.suffixes);
        return map;
    }
}
