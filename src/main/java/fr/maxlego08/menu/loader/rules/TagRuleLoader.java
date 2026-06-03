package fr.maxlego08.menu.loader.rules;

import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.registry.TagRegistry;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.RuleConfigHelper;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.rules.TagRule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AutoRuleLoader
public class TagRuleLoader implements RuleLoader {
    @Override
    public @NotNull String getType() {
        return "tag";
    }

    @Override
    public List<String> getAliases() {
        return List.of("material-tag", "material_tag", "tags");
    }

    @Override
    public @Nullable Rule load(@NotNull Map<String, Object> configuration) {
        List<String> tags = RuleConfigHelper.getStringList(configuration, "tags");

        Set<Tag<Material>> materialTags = tags.stream()
                .map(TagRegistry::getMaterialTag)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (materialTags.isEmpty()) {
            return null;
        }

        return new TagRule(materialTags);
    }
}
