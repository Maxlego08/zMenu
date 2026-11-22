package fr.maxlego08.menu.test.serialization;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Writes an object graph as text, walked like {@link ObjectGraphComparator} does, so a loaded menu can be compared
 * with the text of the same menu loaded by an earlier version of zMenu. Sets and maps are sorted, and identity
 * hashes removed, so the same graph always gives the same text.
 */
public final class ObjectGraphDumper {

    private static final String MODEL_PACKAGE = "fr.maxlego08.";
    private static final Set<String> WIRING_SUFFIXES = Set.of("Manager", "Engine", "Scheduler", "Registry");
    private static final Pattern IDENTITY = Pattern.compile("@[0-9a-f]{5,8}\\b");

    private final Set<String> ignoredFields;

    public ObjectGraphDumper(Set<String> ignoredFields) {
        this.ignoredFields = ignoredFields;
    }

    public String dump(Object value) {
        StringBuilder builder = new StringBuilder();
        this.dump(builder, "", value, new IdentityHashMap<>());
        return builder.toString();
    }

    private void dump(StringBuilder builder, String indent, Object value, IdentityHashMap<Object, Boolean> visiting) {
        if (value == null) {
            builder.append("null\n");
            return;
        }
        if (isWiring(value)) {
            builder.append("<wiring>\n");
            return;
        }
        if (value instanceof List<?> list) {
            this.dumpSequence(builder, indent, new ArrayList<>(list), visiting);
            return;
        }
        if (value.getClass().isArray()) {
            List<Object> list = new ArrayList<>();
            for (int index = 0; index < Array.getLength(value); index++) list.add(Array.get(value, index));
            this.dumpSequence(builder, indent, list, visiting);
            return;
        }
        if (value instanceof Collection<?> collection) {
            List<String> elements = new ArrayList<>();
            for (Object element : collection) elements.add(this.dump(element, visiting));
            elements.sort(null);
            builder.append(value.getClass().getSimpleName()).append(" (").append(elements.size()).append(")\n");
            for (String element : elements) builder.append(indent).append("  - ").append(element.replace("\n", "\n" + indent + "    ").stripTrailing()).append('\n');
            return;
        }
        if (value instanceof Map<?, ?> map) {
            List<String> entries = new ArrayList<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                entries.add(clean(String.valueOf(entry.getKey())) + ": " + this.dump(entry.getValue(), visiting).replace("\n", "\n" + indent + "    ").stripTrailing());
            }
            entries.sort(null);
            builder.append(value.getClass().getSimpleName()).append(" (").append(entries.size()).append(")\n");
            for (String entry : entries) builder.append(indent).append("  ").append(entry).append('\n');
            return;
        }
        if (value instanceof Pattern pattern) {
            builder.append("Pattern ").append(pattern.pattern()).append(' ').append(pattern.flags()).append('\n');
            return;
        }
        if (!isModel(value.getClass())) {
            builder.append(clean(String.valueOf(value))).append(" (").append(value.getClass().getSimpleName()).append(")\n");
            return;
        }
        if (visiting.containsKey(value)) {
            builder.append("<cycle ").append(value.getClass().getSimpleName()).append(">\n");
            return;
        }
        visiting.put(value, true);
        builder.append(value.getClass().getName()).append('\n');
        for (Class<?> type = value.getClass(); type != null && isModel(type); type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) continue;
                if (this.ignoredFields.contains(field.getName()) || this.ignoredFields.contains(type.getSimpleName() + "." + field.getName())) continue;
                builder.append(indent).append("  ").append(type.getSimpleName()).append('.').append(field.getName()).append(" = ");
                try {
                    field.setAccessible(true);
                    this.dump(builder, indent + "    ", field.get(value), visiting);
                } catch (ReflectiveOperationException | RuntimeException exception) {
                    builder.append("<unreadable ").append(exception.getClass().getSimpleName()).append(">\n");
                }
            }
        }
        visiting.remove(value);
    }

    private String dump(Object value, IdentityHashMap<Object, Boolean> visiting) {
        StringBuilder builder = new StringBuilder();
        this.dump(builder, "", value, visiting);
        return builder.toString();
    }

    private void dumpSequence(StringBuilder builder, String indent, List<Object> list, IdentityHashMap<Object, Boolean> visiting) {
        builder.append("List (").append(list.size()).append(")\n");
        for (Object element : list) {
            builder.append(indent).append("  - ");
            this.dump(builder, indent + "    ", element, visiting);
        }
    }

    private static String clean(String text) {
        return IDENTITY.matcher(text).replaceAll("@#");
    }

    private static boolean isModel(Class<?> type) {
        return !Enum.class.isAssignableFrom(type) && type.getName().startsWith(MODEL_PACKAGE);
    }

    private static boolean isWiring(Object value) {
        if (value instanceof Plugin || value instanceof File || value instanceof Path) return true;
        Class<?> type = value.getClass();
        if (type.isSynthetic() || type.isHidden()) return true;
        for (String suffix : WIRING_SUFFIXES) {
            if (type.getSimpleName().endsWith(suffix)) return true;
        }
        return false;
    }
}
