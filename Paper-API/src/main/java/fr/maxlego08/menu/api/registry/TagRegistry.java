package fr.maxlego08.menu.api.registry;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public final class TagRegistry extends Registry<String, Tag<Material>> {
    private static final TagRegistry INSTANCE = new TagRegistry();

    private TagRegistry() {
        // Private constructor to prevent instantiation
    }

    static {
        for (Field field : Tag.class.getDeclaredFields()) {
            if (Tag.class.isAssignableFrom(field.getType())) {
                try {
                    Class<?> genericType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (Material.class.isAssignableFrom(genericType)) {
                        //noinspection unchecked
                        registerTag(field.getName(), (Tag<Material>) field.get(null));
                    }
                } catch (Exception _) {
                }
            }
        }
    }

    public static void registerTag(@NotNull String key,@NotNull Tag<Material> tag) {
        INSTANCE.register(key, tag);
    }

    @Nullable
    public static Tag<Material> getMaterialTag(@NotNull String key) {
        return INSTANCE.get(key).orElse(null);
    }
}
