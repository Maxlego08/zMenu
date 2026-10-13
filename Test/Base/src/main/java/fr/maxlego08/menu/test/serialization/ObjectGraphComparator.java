package fr.maxlego08.menu.test.serialization;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Compares two object graphs field by field, so an object loaded from a file can be checked against
 * the same object after a serialize then load round trip, without every model class implementing
 * {@code equals}.
 * <p>
 * zMenu classes are walked field by field. Anything else is compared with {@code equals}. References
 * to the plugin, managers, files and lambdas are skipped: they are wiring, not configuration, and a
 * round trip is expected to change them.
 */
public final class ObjectGraphComparator {

    private static final String MODEL_PACKAGE = "fr.maxlego08.";
    private static final Set<String> WIRING_SUFFIXES = Set.of("Manager", "Engine", "Scheduler", "Registry");

    private final Set<String> ignoredFields = new HashSet<>();

    /**
     * Ignores a field wherever it appears, or only on one class when written {@code SimpleClassName.field}.
     *
     * @param field The field name, optionally prefixed by the simple name of the class declaring it.
     * @return This comparator.
     */
    public ObjectGraphComparator ignoreField(String field) {
        this.ignoredFields.add(field);
        return this;
    }

    /**
     * Compares two graphs.
     *
     * @param expected The graph the other one should match.
     * @param actual   The graph to check.
     * @return One line per difference, each starting with the path to the differing value; empty when both match.
     */
    public List<String> compare(Object expected, Object actual) {
        List<String> differences = new ArrayList<>();
        this.compare("$", expected, actual, differences, new IdentityHashMap<>());
        return differences;
    }

    private void compare(String path, Object expected, Object actual, List<String> differences, IdentityHashMap<Object, Object> visited) {
        if (expected == actual) return;
        if (expected == null || actual == null) {
            differences.add(path + ": expected " + describe(expected) + " but was " + describe(actual));
            return;
        }
        if (isWiring(expected) || isWiring(actual)) return;

        if (expected instanceof List<?> expectedList && actual instanceof List<?> actualList) {
            this.compareSequences(path, expectedList, actualList, differences, visited);
            return;
        }
        if (expected instanceof Collection<?> expectedCollection && actual instanceof Collection<?> actualCollection) {
            this.compareCollections(path, expectedCollection, actualCollection, differences, visited);
            return;
        }
        if (expected instanceof Map<?, ?> expectedMap && actual instanceof Map<?, ?> actualMap) {
            this.compareMaps(path, expectedMap, actualMap, differences, visited);
            return;
        }
        if (expected.getClass().isArray() && actual.getClass().isArray()) {
            this.compareSequences(path, arrayToList(expected), arrayToList(actual), differences, visited);
            return;
        }

        if (expected instanceof Pattern expectedPattern && actual instanceof Pattern actualPattern) {
            if (!expectedPattern.pattern().equals(actualPattern.pattern()) || expectedPattern.flags() != actualPattern.flags()) {
                differences.add(path + ": expected " + describe(expected) + " but was " + describe(actual));
            }
            return;
        }

        if (!isModel(expected.getClass()) || !isModel(actual.getClass())) {
            if (!expected.equals(actual)) {
                differences.add(path + ": expected " + describe(expected) + " but was " + describe(actual));
            }
            return;
        }

        if (expected.getClass() != actual.getClass()) {
            differences.add(path + ": expected type " + expected.getClass().getName() + " but was " + actual.getClass().getName());
            return;
        }

        if (visited.put(expected, actual) != null) return;

        for (Class<?> type = expected.getClass(); type != null && isModel(type); type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) continue;
                if (this.ignoredFields.contains(field.getName()) || this.ignoredFields.contains(type.getSimpleName() + "." + field.getName())) continue;

                try {
                    field.setAccessible(true);
                    this.compare(path + "." + field.getName(), field.get(expected), field.get(actual), differences, visited);
                } catch (ReflectiveOperationException | RuntimeException exception) {
                    differences.add(path + "." + field.getName() + ": cannot be read (" + exception + ")");
                }
            }
        }
    }

    private void compareSequences(String path, List<?> expected, List<?> actual, List<String> differences, IdentityHashMap<Object, Object> visited) {
        if (expected.size() != actual.size()) {
            differences.add(path + ": expected " + expected.size() + " element(s) but was " + actual.size() + " " + describe(expected) + " vs " + describe(actual));
            return;
        }
        for (int index = 0; index < expected.size(); index++) {
            this.compare(path + "[" + index + "]", expected.get(index), actual.get(index), differences, visited);
        }
    }

    private void compareCollections(String path, Collection<?> expected, Collection<?> actual, List<String> differences, IdentityHashMap<Object, Object> visited) {
        if (expected.size() != actual.size()) {
            differences.add(path + ": expected " + expected.size() + " element(s) but was " + actual.size() + " " + describe(expected) + " vs " + describe(actual));
            return;
        }
        boolean plainValues = expected.stream().allMatch(value -> value == null || !isModel(value.getClass()));
        if (plainValues) {
            if (!new HashSet<>(expected).equals(new HashSet<>(actual))) {
                differences.add(path + ": expected " + describe(expected) + " but was " + describe(actual));
            }
            return;
        }
        Iterator<?> actualIterator = actual.iterator();
        int index = 0;
        for (Object expectedValue : expected) {
            this.compare(path + "[" + index++ + "]", expectedValue, actualIterator.next(), differences, visited);
        }
    }

    private void compareMaps(String path, Map<?, ?> expected, Map<?, ?> actual, List<String> differences, IdentityHashMap<Object, Object> visited) {
        if (!expected.keySet().equals(actual.keySet())) {
            differences.add(path + ": expected keys " + expected.keySet() + " but was " + actual.keySet());
            return;
        }
        for (Map.Entry<?, ?> entry : expected.entrySet()) {
            this.compare(path + "[" + entry.getKey() + "]", entry.getValue(), actual.get(entry.getKey()), differences, visited);
        }
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

    private static List<Object> arrayToList(Object array) {
        int length = Array.getLength(array);
        List<Object> list = new ArrayList<>(length);
        for (int index = 0; index < length; index++) {
            list.add(Array.get(array, index));
        }
        return list;
    }

    private static String describe(Object value) {
        if (value == null) return "null";
        String text = String.valueOf(value);
        if (text.length() > 120) text = text.substring(0, 117) + "...";
        return "<" + text + "> (" + value.getClass().getSimpleName() + ")";
    }
}
