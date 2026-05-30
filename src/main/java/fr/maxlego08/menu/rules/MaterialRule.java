package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleContext;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MaterialRule implements Rule {
    private final Set<Material> materials;

    public MaterialRule(@NotNull Set<@NotNull Material> materials) {
        this.materials = materials;
    }

    @Override
    public boolean matches(@NotNull RuleContext context) {
        return this.materials.contains(context.getMaterial());
    }
}
