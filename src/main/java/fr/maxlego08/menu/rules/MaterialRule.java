package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MaterialRule implements Rule {
    private final Set<Material> materials;

    public MaterialRule(@NotNull Set<@NotNull Material> materials) {
        this.materials = materials;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        return this.materials.contains(context.getMaterial());
    }

    @Override
    public boolean isValid() {
        return !this.materials.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "material");
        map.put("materials", this.materials.stream().map(material -> material.getKey().toString()).sorted().toList());
        return map;
    }
}
