package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleContext;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TagRule implements Rule {
    private final Set<Tag<Material>> tags;

    public TagRule(Set<Tag<Material>> tags) {
        this.tags = tags;
    }

    @Override
    public boolean matches(@NotNull RuleContext context) {
        Material material = context.getMaterial();
        for (Tag<Material> tag : this.tags) {
            if (tag.isTagged(material)) {
                return true;
            }
        }
        return false;
    }
}
