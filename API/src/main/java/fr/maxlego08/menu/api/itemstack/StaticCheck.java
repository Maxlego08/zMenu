package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.robie.minimessageflow.Template;
import fr.robie.minimessageflow.TemplateList;
import fr.robie.minimessageflow.TextTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Whether what goes into a built item is the same for every player and every build: no placeholder, no value tag, no
 * nested item that depends on the player. A built item whose parts are all static can be kept and given again.
 * <p>
 * zMenu's own resolvables and item components answer by looking at their fields once: a {@link Resolvable}, an
 * {@link ItemComponent}, an item, a template, and the lists, maps and arrays holding them, answer for themselves; a
 * record or a class of zMenu's item and resolvable packages is looked into; any other value is a fixed value. A
 * resolvable or a component of another plugin is not looked into: it is dynamic unless it says otherwise.
 */
public final class StaticCheck {

    private static final String ZMENU = "fr.maxlego08.menu.";
    private static final int MAX_DEPTH = 16;
    private static final ClassValue<List<Field>> FIELDS = new ClassValue<>() {
        @Override
        protected List<Field> computeValue(@NotNull Class<?> type) {
            List<Field> fields = new ArrayList<>();
            for (Class<?> current = type; current != null && current.getName().startsWith(ZMENU); current = current.getSuperclass()) {
                if (current == ItemComponent.class) {
                    break;
                }
                for (Field field : current.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
                        continue;
                    }
                    try {
                        field.setAccessible(true);
                        fields.add(field);
                    } catch (RuntimeException ignored) {
                        return null;
                    }
                }
            }
            return List.copyOf(fields);
        }
    };

    private StaticCheck() {
    }

    /**
     * Whether an object of zMenu (a resolvable, an item component) is static, from its fields. An object of another
     * plugin is not: it says itself whether it is.
     *
     * @param object the object
     * @return true when every field holds a static value
     */
    public static boolean fieldsStatic(@NotNull Object object) {
        return object.getClass().getName().startsWith(ZMENU) && fieldsStatic(object, 0);
    }

    /**
     * Whether a value is static: see the class description.
     *
     * @param value the value, or null
     * @return true when it is the same for every player and every build
     */
    public static boolean isStatic(@Nullable Object value) {
        return isStatic(value, 0);
    }

    private static boolean isStatic(@Nullable Object value, int depth) {
        if (depth > MAX_DEPTH) {
            return false;
        }
        return switch (value) {
            case null -> true;
            case Resolvable<?> resolvable -> resolvable.isStatic();
            case ItemComponent component -> component.isStatic();
            case MenuItemStackContext item -> item.isStatic();
            case Template template -> template.isStatic();
            case TemplateList templates -> templates.isStatic();
            case TextTemplate text -> text.isStatic();
            case Collection<?> collection -> allStatic(collection, depth);
            case Map<?, ?> map -> allStatic(map.keySet(), depth) && allStatic(map.values(), depth);
            case Object[] array -> allStatic(Arrays.asList(array), depth);
            default -> !lookedInto(value.getClass()) || fieldsStatic(value, depth);
        };
    }

    private static boolean allStatic(@NotNull Collection<?> values, int depth) {
        for (Object value : values) {
            if (!isStatic(value, depth + 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * A record of zMenu, or a class of its item, attribute or resolvable packages: a value made of other values.
     */
    private static boolean lookedInto(@NotNull Class<?> type) {
        String name = type.getName();
        return name.startsWith(ZMENU) && (type.isRecord() || name.contains(".itemstack.") || name.contains(".resolvable.") || name.contains(".attribute."));
    }

    private static boolean fieldsStatic(@NotNull Object object, int depth) {
        List<Field> fields = FIELDS.get(object.getClass());
        if (fields == null) {
            return false;
        }
        for (Field field : fields) {
            Object value;
            try {
                value = field.get(object);
            } catch (IllegalAccessException e) {
                return false;
            }
            if (value != object && !isStatic(value, depth + 1)) {
                return false;
            }
        }
        return true;
    }
}
