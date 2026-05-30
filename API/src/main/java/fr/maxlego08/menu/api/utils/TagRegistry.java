package fr.maxlego08.menu.api.utils;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class TagRegistry {
    private static final Map<String, Tag<Material>> MATERIAL_TAGS = new HashMap<>();

    private TagRegistry() {
        // Utility class
    }

    static {
        for (Field field : Tag.class.getDeclaredFields()) {
            if (Tag.class.isAssignableFrom(field.getType())) {
                try {
                    Class<?> genericType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (Material.class.isAssignableFrom(genericType)) {
                        //noinspection unchecked
                        register(field.getName(), (Tag<Material>) field.get(null));
                    }
                } catch (Exception _) {
                }
            }
        }
    }

    public static void register(@NotNull String key,@NotNull Tag<Material> tag) {
        MATERIAL_TAGS.put(key, tag);
    }

    @Nullable
    public static Tag<Material> getMaterialTag(@NotNull String key) {
        return MATERIAL_TAGS.get(key);
    }
}
