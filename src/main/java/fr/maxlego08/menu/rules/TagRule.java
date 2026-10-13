package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.rules.Rule;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class TagRule implements Rule {
    private final Set<Tag<Material>> tags;

    public TagRule(Set<Tag<Material>> tags) {
        this.tags = tags;
    }

    @Override
    public boolean matches(@NotNull ItemRuleContext context) {
        Material material = context.getMaterial();
        for (Tag<Material> tag : this.tags) {
            if (tag.isTagged(material)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return !this.tags.isEmpty();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "tag");
        map.put("tags", this.tags.stream().map(TagRule::getTagName).sorted().toList());
        return map;
    }

    /**
     * Finds the name the tag registry knows the tag by: the {@link Tag} constant holding it.
     *
     * @throws UnsupportedOperationException for a tag that is not a {@link Tag} constant, such as one registered by another plugin
     */
    private static @NotNull String getTagName(@NotNull Tag<Material> tag) {
        for (Field field : Tag.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Tag.class.isAssignableFrom(field.getType())) continue;
            try {
                if (field.get(null) == tag) return field.getName();
            } catch (IllegalAccessException ignored) {
            }
        }
        throw new UnsupportedOperationException("The tag " + tag.getKey() + " cannot be serialized: it is not a Bukkit Tag constant");
    }
}
