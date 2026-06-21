package fr.maxlego08.menu.api.utils.resolvable.utils;

import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RegistryKeys {
    private static final Map<Class<?>, RegistryKey<?>> CLASS_TO_KEY = new HashMap<>();

    static {
        for (Field field : RegistryKey.class.getDeclaredFields()) {
            if (!RegistryKey.class.isAssignableFrom(field.getType())) continue;

            Type generic = field.getGenericType();
            if (!(generic instanceof ParameterizedType pt)) continue;

            Type[] args = pt.getActualTypeArguments();
            if (args.length != 1 || !(args[0] instanceof Class<?> valueClass)) continue;

            try {
                RegistryKey<?> key = (RegistryKey<?>) field.get(null);
                CLASS_TO_KEY.putIfAbsent(valueClass, key);
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Keyed> @Nullable RegistryKey<T> forClass(Class<T> clazz) {
        return (RegistryKey<T>) CLASS_TO_KEY.get(clazz);
    }
}