package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.MaterialRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@AutoRuleLoader
public class MaterialRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "material";
    }

    @Override
    public Rule load(@NotNull Map<String, Object> configuration) {
        List<String> materials = RuleConfigHelper.getStringList(configuration, "materials");
        if (materials.isEmpty()) {
            return null;
        }
        Set<Material> materialSet = materials.stream()
                .map(name -> {
                    try {
                        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase(Locale.ROOT));
                        return key != null ? Registry.MATERIAL.get(key) : null;
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return materialSet.isEmpty() ? null : new MaterialRule(materialSet);
    }
}
